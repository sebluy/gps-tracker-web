(ns gps-tracker.db
  (:require [clojure.set :as set]
            [clojure.java.jdbc :as jdbc]
            [schema.core :as s]
            [gps-tracker-common.schema :as t])
  (:import [java.sql Timestamp]
           [java.util Date]))

;Todo: refactor waypoint and path code together

(def db-spec (or (System/getenv "DATABASE_URL")
                 {:subprotocol "postgresql"
                  :subname     "//localhost/gpstracker"
                  :user        "dev"
                  :password    "dev"}))

;;;; maybe instead of using dynamic transaction, build (monad?) string of
;;;; functions that are later threaded with transaction
(declare ^:dynamic *txn*)

;; useful for debugging

(defn clear-tracking-points! []
  (jdbc/delete! db-spec :tracking_point []))

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

;;;; waypoint paths

#_(defn add-waypoint! [point]
  (jdbc/insert! db-spec :waypoint (point->db-point point)))

#_(defn get-waypoint-path [id]
  (let [raw (jdbc/query
              *txn*
              ["SELECT * FROM waypoint
                WHERE path_id = ?
                ORDER BY index ASC" id])]
    (into [] (map #(dissoc % :path_id :index) raw))))

#_(defn get-waypoint-path-ids []
  (map #(-> :path_id sql->date) (jdbc/query db-spec ["SELECT DISTINCT path_id FROM waypoint"])))

#_(defn get-waypoint-paths []
  (reduce
    (fn [paths id] (assoc paths id (get-waypoint-path id)))
    {} (get-waypoint-path-ids)))

#_(s/defn add-waypoint-path! [path]
  (let [path-id (next-waypoint-path-id)]
    (doall (map-indexed
             (fn [index point]
               (add-waypoint! (update point :path-id path-id :index index)))
             path))))

#_(defn delete-waypoint-path! [path-id]
  (jdbc/delete! db-spec :waypoint ["path_id = ?" path-id]))

;;;; tracking paths

(s/defn tracking-point->sql [point :- t/TrackingPoint path-id index]
  (-> point
      (assoc :path_id (date->sql path-id) :index index)
      (update :time date->sql)))

(s/defn sql->tracking-point [point] :- t/TrackingPoint
  (-> point
      (dissoc :path_id :index)
      (->> (filter second)
           (into {}))
      (update :time sql->date)))

(s/defn tracking-path->sql [{:keys [id points]} :- t/TrackingPath]
  (map-indexed
   (fn [index point]
     (tracking-point->sql point id index))
   points))

(s/defn sql->tracking-path [[id points]] :- t/TrackingPath
  {:id (sql->date id)
   :points (into [] (map sql->tracking-point points))})

(defn get-tracking-points []
  (jdbc/query *txn* ["SELECT * FROM tracking_point
                      ORDER BY path_id DESC, index ASC"]))

(s/defn sql->tracking-paths [points] :- [t/TrackingPath]
  (->> (group-by :path_id (get-tracking-points))
       (mapv sql->tracking-path)))


;;;; api actions

(defmulti api-action (comp keyword first))

(s/defmethod api-action :add-tracking-path [[_ path] :- t/AddTrackingPath]
  (apply jdbc/insert! *txn* :tracking_point (tracking-path->sql path))
  nil)

(s/defmethod api-action :get-tracking-paths :- [t/TrackingPath] [_ :- t/GetTrackingPaths]
  (sql->tracking-paths (get-tracking-points)))

(s/defmethod api-action :delete-tracking-path [[_ id] :- t/DeleteTrackingPath]
  (jdbc/delete! *txn* :tracking_point ["path_id = ?" (date->sql id)])
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
