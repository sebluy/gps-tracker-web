(ns gps-tracker.db
  (:require [clojure.set :as set]
            [clojure.java.jdbc :as jdbc]
            [schema.core :as s]
            [gps-tracker-common.schema :as t])
  (:import [java.sql Timestamp]
           [java.util Date]))

;;;;
;; This namespace serves to coerce paths (tracking and waypoint) between
;; their 'map' form and their relational database friendly form and handle
;; external API calls that require this translation in interacting with
;; the database.
;;;;

;Todo: refactor waypoint and path code together

(def db-spec (or (System/getenv "DATABASE_URL")
                 {:subprotocol "postgresql"
                  :subname     "//localhost/gpstracker"
                  :user        "dev"
                  :password    "dev"}))

;;;; maybe instead of using dynamic transaction, build (monad?) string of
;;;; functions that are later threaded with transaction
(declare ^:dynamic *txn*)

;; date conversions

(defn date->sql [date]
  (Timestamp. (.getTime date)))

(defn sql->date [date]
  (Date. (.getTime date)))

;; setup, eventually move to migrations

(defn create-point-table! []
  (jdbc/db-do-commands
   *txn*
   (jdbc/create-table-ddl
    :tracking_point
    [:index :int]
    [:latitude "double precision"]
    [:longitude "double precision"]
    [:speed "double precision"]
    [:accuracy "double precision"]
    [:time "timestamp with time zone"]
    [:path_id "timestamp with time zone"])))

(defn create-waypoint-table! []
  (jdbc/db-do-commands
   *txn*
   (jdbc/create-table-ddl
    :waypoint
    [:index :int]
    [:latitude "double precision"]
    [:longitude "double precision"]
    [:path_id "timestamp with time zone"])))

(def tables {:tracking :tracking_point
             :waypoint :waypoint})

;;;; path coercion

(defn path->sql [type {:keys [id points]}]
  "Converts a path to the equivalent sql point rows representation
   using point->sql to convert each point in path"
  (map-indexed
   (fn [index point]
     (point->sql type point id index))
   points))

(defn sql->path [type [id points]]
  "Converts the sql point rows representation to a path using
   sql->point to convert each row"
  {:id (sql->date id)
   :points (mapv #(sql->point type %) points)})

;;;; point coercion

;; to sql
(defmulti typed-point->sql (fn [type _] type))

(defmethod typed-point->sql :tracking [_ point]
  (update point :time date->sql))

(defmethod typed-point->sql :waypoint [_ point]
  point)

(defn common-point->sql [point path-id index]
  (assoc point :path_id (date->sql path-id) :index index))

(defn point->sql [type point path-id index]
  "Convert a point to a sql row. First apply common point transformation
   and then apply type specific transformation"
  (->> (common-point->sql point path-id index)
       (typed-point->sql type)))

;; from sql
(defmulti sql->typed-point (fn [type _] type))

(defn remove-nils [map]
  (->> (filter second map)
       (into {})))

(defmethod sql->typed-point :tracking [_ point]
  (-> (remove-nils point)
      (update :time sql->date)))

(defmethod sql->typed-point :waypoint [_ point]
  point)

(defn sql->common-point [point]
  (dissoc point :path_id :index))

(defn sql->point [type point]
  "Convert a sql row to a point. Does the 'reverse' of point->sql"
  (->> (sql->common-point point)
       (sql->typed-point type)))

;; alias waypoint coercion because it is the same as generic point coercion
(def sql->waypoint sql->point)
(def waypoint->sql point->sql)

(defn sql->paths [type points]
  (->> (group-by :path_id points)
       (mapv #(sql->path type %))))

;;;; db operations

(defn get-points [table]
  (jdbc/query *txn*
              [(str "SELECT * FROM " (name table)
                    " ORDER BY path_id DESC, index ASC")]))

(defn insert-points [table sql-points]
  (apply jdbc/insert! *txn* table sql-points))

(defn delete-points [table id]
  (jdbc/delete! *txn* table ["path_id = ?" (date->sql id)]))

;;;; api actions

(defmulti api-action (comp keyword first))

;; tracking paths
(s/defmethod api-action :add-path [[_ type path] :- t/AddPath]
  (insert-points (tables type) (path->sql type path))
  nil)

(s/defmethod api-action :get-paths :- [t/Path] [[_ type] :- t/GetPaths]
  (->> (tables type)
       (get-points)
       (sql->paths type)))

(s/defmethod api-action :delete-path [[_ type id] :- t/DeletePath]
  (delete-points (tables type) id)
  nil)

(defn execute-api-actions [actions]
  "Executes all actions in a transaction and returns results in a list"
  (try
    (jdbc/with-db-transaction
      [transaction db-spec]
      (binding [*txn* transaction]
        (doall (mapv api-action actions))))
    (catch Exception e
      (throw (Exception. "Invalid API Actions")))))
