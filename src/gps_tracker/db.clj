(ns gps-tracker.db
  (:require [clojure.set :as set]
            [clojure.java.jdbc :as sql]
            [schema.core :as s]
            [gps-tracker-common.schema :as gps-schema])
  (:import [gps_tracker_common.schema]
           [java.sql Timestamp]))

;Todo: refactor waypoint and path code together

(def db-spec (or (System/getenv "DATABASE_URL")
                 {:subprotocol "postgresql"
                  :subname     "//localhost/gpstracker"
                  :user        "dev"
                  :password    "dev"}))

(defn sql-datetime [datetime]
  (Timestamp. (.getTime datetime)))

;(sql-datetime (java.util.Date.))

(defn create-point-table! []
  (sql/db-do-commands
    db-spec
    (sql/create-table-ddl
      :tracking_point
      [:index :int]
      [:latitude "double precision"]
      [:longitude "double precision"]
      [:speed "double precision"]
      [:accuracy "double precision"]
      [:time "timestamp with time zone"]
      [:path_id "timestamp with time zone"])))

(defn create-waypoint-table! []
  (sql/db-do-commands
    db-spec
    (sql/create-table-ddl
      :waypoint
      [:index :int]
      [:latitude "double precision"]
      [:longitude "double precision"]
      [:path_id "timestamp with time zone"])))

(defn add-waypoint! [point]
  (sql/insert! db-spec :waypoint (point->db-point point)))

(defn get-points []
  (sql/query db-spec ["SELECT * FROM tracking_point"]))

(defn get-path [id]
  (let [raw (sql/query
              db-spec
              ["SELECT * FROM point
              WHERE path_id = ?
              ORDER BY index ASC" id])]
    (into [] (map #(dissoc % :path_id :index) raw))))

(defn get-waypoint-path [id]
  (let [raw (sql/query
              db-spec
              ["SELECT * FROM waypoint
              WHERE path_id = ?
              ORDER BY index ASC" id])]
    (into [] (map #(dissoc % :path_id :index) raw))))

(defn get-path-ids []
  (map :path_id (sql/query db-spec ["SELECT DISTINCT path_id FROM point"])))

(defn get-waypoint-path-ids []
  (map :path_id (sql/query db-spec ["SELECT DISTINCT path_id FROM waypoint"])))

(defn get-waypoint-paths []
  (reduce
    (fn [paths id] (assoc paths id (get-waypoint-path id)))
    {} (get-waypoint-path-ids)))

;;;; add tracking points

(defn tracking-point->db [point]
  "Change key :path-id to :path_id and convert all
   java.util.Dates to java.sql.Timestamp. Point has keys
   [:latitude :longitude :time :speed :accuracy :path-id :index]"
  (-> point
      (dissoc :path-id)
      (update :time sql-datetime)
      (assoc :path_id (sql-datetime (point :path-id)))))

(defn add-tracking-point! [point]
  "Point is TrackingPoint + extra keys [:path-id :index]"
  (sql/insert! db-spec :tracking_point (tracking-point->db point)))

(s/defn add-tracking-path! [{:keys [id points]} :- TrackingPath]
  (doall (map-indexed
          (fn [index point]
            (add-point! (assoc point :path-id id :index index)))
          points)))

#_(s/defn add-waypoint-path! [path]
  (let [path-id (next-waypoint-path-id)]
    (doall (map-indexed
             (fn [index point]
               (add-waypoint! (update point :path-id path-id :index index)))
             path))))

(defn delete-path! [path-id]
  (sql/delete! db-spec :point ["path_id = ?" path-id]))

(defn delete-waypoint-path! [path-id]
  (sql/delete! db-spec :waypoint ["path_id = ?" path-id]))

(defn clear-points! []
  (sql/delete! db-spec :point []))
