(ns gps-tracker.db
  (:require [clojure.set :as set]
            [clojure.java.jdbc :as jdbc]
            [schema.core :as s]
            [gps-tracker-common.schema :as t])
  (:import [java.sql Timestamp]
           [java.util Date]))

;Todo: refactor waypoint and path code together

(def db-spec (or (System/getenv "DATABASE_URL")
                 {:subprotocol "postgresql" :subname     "//localhost/gpstracker"
                  :user        "dev"
                  :password    "dev"}))

;;;; maybe instead of using dynamic transaction, build (monad?) string of
;;;; functions that are later threaded with transaction
(declare ^:dynamic *txn*)

(defn date->sql [date]
  (Timestamp. (.getTime date)))

(defn sql->date [date]
  (Date. (.getTime date)))

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

#_(defn add-waypoint! [point]
  (jdbc/insert! db-spec :waypoint (point->db-point point)))

(defn get-tracking-points []
  (jdbc/query *txn* ["SELECT * FROM tracking_point"]))

;;;; get tracking paths

(s/defn get-tracking-path [id :- t/Date] :- t/TrackingPath
  (let [raw (jdbc/query
              *txn*
              ["SELECT * FROM tracking_point
              WHERE path_id = ?
              ORDER BY index ASC" (date->sql id)])
        points (into [] (map sql->tracking-point raw))]
    {:id id :points points}))

(defn get-waypoint-path [id]
  (let [raw (jdbc/query
              *txn*
              ["SELECT * FROM waypoint
                WHERE path_id = ?
                ORDER BY index ASC" id])]
    (into [] (map #(dissoc % :path_id :index) raw))))

(defn get-tracking-path-ids []
  (map #(-> % :path_id sql->date) (jdbc/query *txn* ["SELECT DISTINCT path_id
                                                      FROM tracking_point
                                                      ORDER BY path_id DESC"])))

(defn get-waypoint-path-ids []
  (map #(-> :path_id sql->date) (jdbc/query db-spec ["SELECT DISTINCT path_id FROM waypoint"])))

(defn get-waypoint-paths []
  (reduce
    (fn [paths id] (assoc paths id (get-waypoint-path id)))
    {} (get-waypoint-path-ids)))

;;;; tracking paths

(s/defn tracking-point->sql [point :- t/TrackingPoint path-id index]
  (-> point
      (assoc :path_id (date->sql path-id) :index index)
      (update :time date->sql)))

(s/defn sql->tracking-point [point] :- t/TrackingPoint
  (-> point
      (dissoc :path_id :index)
      (update :time sql->date)))

(s/defn tracking-path->sql [{:keys [id points]} :- t/TrackingPath]
  (map-indexed
   (fn [index point]
     (tracking-point->sql point id index))
   points))

(s/defn sql->tracking-path [points id] :- t/TrackingPath
  {:id id
   :points (into [] (map sql->tracking-point points))})

(s/defn get-tracking-path [id :- t/Date] :- t/TrackingPath
  (-> (jdbc/query
       *txn*
       ["SELECT * FROM tracking_point
        WHERE path_id = ?
        ORDER BY index ASC" (date->sql id)])
      (sql->tracking-path id)))

(s/defn add-tracking-path! [path :- t/TrackingPath]
  (apply jdbc/insert! *txn* :tracking_point (tracking-path->sql path)))

;;;; add waypoint paths

#_(s/defn add-waypoint-path! [path]
  (let [path-id (next-waypoint-path-id)]
    (doall (map-indexed
             (fn [index point]
               (add-waypoint! (update point :path-id path-id :index index)))
             path))))

(defn delete-tracking-path! [id]
  (jdbc/delete! *txn* :tracking_point ["path_id = ?" (date->sql id)]))

(defn delete-waypoint-path! [path-id]
  (jdbc/delete! db-spec :waypoint ["path_id = ?" path-id]))

(defn clear-tracking-points! []
  (jdbc/delete! db-spec :tracking_point []))

;;;; api actions

;; todo: add schema declarations and validations for api-actions
;; if multi methods worked with schema, this would be redundant
(defmulti api-action (comp keyword first))

(defmethod api-action :add-tracking-path [[_ path]]
  (add-tracking-path! path))

(defmethod api-action :get-tracking-path [[_ id]]
  (get-tracking-path id))

(defmethod api-action :get-tracking-path-ids [_]
  (get-tracking-path-ids))

(defmethod api-action :delete-tracking-path [[_ id]]
  (delete-tracking-path! id))

(defn execute-api-actions [actions]
  "Executes all actions in a transaction and returns results in a list"
  (jdbc/with-db-transaction
    [transaction db-spec]
    (binding [*txn* transaction]
      (map api-action actions))))
