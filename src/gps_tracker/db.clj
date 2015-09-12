(ns gps-tracker.db
  (:require [clojure.set :as set]
            [clojure.java.jdbc :as sql]))

;Todo: refactor waypoint and path code together

(def db-spec (or (System/getenv "DATABASE_URL")
                 {:subprotocol "postgresql"
                  :subname     "//localhost/gpstracker"
                  :user        "admin"
                  :password    "admin"}))

(defn create-point-table! []
  (sql/db-do-commands
    db-spec
    (sql/create-table-ddl
      :point
      [:latitude "double precision"]
      [:longitude "double precision"]
      [:path_id :int])))

(defn create-waypoint-table! []
  (sql/db-do-commands
    db-spec
    (sql/create-table-ddl
      :waypoint
      [:latitude "double precision"]
      [:longitude "double precision"]
      [:path_id :int])))

(defn add-index-to-point-table! []
  (sql/db-do-commands db-spec "ALTER TABLE point ADD index int"))

(defn add-index-to-waypoint-table! []
  (sql/db-do-commands db-spec "ALTER TABLE waypoint ADD index int"))

(defn add-speed-to-point-table! []
  (sql/db-do-commands db-spec "ALTER TABLE point ADD speed double precision"))

(defn add-accuracy-to-point-table! []
  (sql/db-do-commands
    db-spec
    "ALTER TABLE point ADD accuracy double precision"))

(defn point->db-point [point]
  (set/rename-keys point {:path-id :path_id}))

(defn add-point! [point]
  (sql/insert! db-spec :point (point->db-point point)))

(defn add-waypoint! [point]
  (sql/insert! db-spec :waypoint (point->db-point point)))

(defn get-points []
  (sql/query db-spec ["SELECT * FROM point"]))

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

(defn next-path-id []
  (let [ids (get-path-ids)]
    (if (seq ids)
      (+ (apply max ids) 1)
      0)))

(defn next-waypoint-path-id []
  (let [ids (get-waypoint-path-ids)]
    (if (seq ids)
      (+ (apply max ids) 1)
      0)))

(defn add-path! [path]
  (let [path-id (next-path-id)]
    (doall (map-indexed
             (fn [index point]
               (add-point! (assoc point :path-id path-id :index index)))
             path))))

(defn add-waypoint-path! [path]
  (let [path-id (next-waypoint-path-id)]
    (doall (map-indexed
             (fn [index point]
               (add-waypoint! (assoc point :path-id path-id :index index)))
             path))))

(defn delete-path! [path-id]
  (sql/delete! db-spec :point ["path_id = ?" path-id]))

(defn delete-waypoint-path! [path-id]
  (sql/delete! db-spec :waypoint ["path_id = ?" path-id]))

(defn clear-points! []
  (sql/delete! db-spec :point []))


