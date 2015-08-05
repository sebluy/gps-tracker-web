(ns gps-tracker.db
  (:require [clojure.set :as set]
            [clojure.java.jdbc :as sql]))

(def db-spec {:subprotocol "postgresql"
              :subname     "//localhost/gpstracker"
              :user        "admin"
              :password    "admin"})

(defn create-point-table! []
  (sql/db-do-commands
    db-spec
    (sql/create-table-ddl
      :point
      [:latitude "double precision"]
      [:longitude "double precision"]
      [:path_id :int])))

(defn add-index-to-point-table! []
  (sql/db-do-commands db-spec "ALTER TABLE point ADD index int"))

(defn point->db-point [point]
  (set/rename-keys point {:path-id :path_id}))

(defn add-point! [point]
  (sql/insert! db-spec :point (point->db-point point)))

(defn get-points []
  (sql/query db-spec ["SELECT * FROM point"]))

(defn get-path [id]
  (let [raw (sql/query
              db-spec
              ["SELECT * FROM point
              WHERE path_id = ?
              ORDER BY index ASC" id])]
    (into [] (map #(dissoc % :path_id :index) raw))))

(defn get-path-ids []
  (map :path_id (sql/query db-spec ["SELECT DISTINCT path_id FROM point"])))

(defn next-path-id []
  (let [ids (get-path-ids)]
    (if (seq ids)
      (+ (apply max ids) 1)
      0)))

(defn add-path! [path]
  (let [path-id (next-path-id)]
    (doall (map-indexed
             (fn [index point]
               (add-point! (assoc point :path-id path-id :index index)))
             path))))

(defn clear-points! []
  (sql/delete! db-spec :point []))

#_(add-path! [{:latitude 43.6 :longitude -70.6}
            {:latitude 43.9 :longitude -70.6}
            {:latitude 43.9 :longitude -70.3}
            {:latitude 43.6 :longitude -70.3}
            {:latitude 43.6 :longitude -70.6}])


