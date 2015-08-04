(ns gps-tracker.db
  (:require [clojure.set :as set]
            [clojure.java.jdbc :as sql]))

(def db-spec {:subprotocol "postgresql"
              :subname "//localhost/gpstracker"
              :user "admin"
              :password "admin"})

(defn create-point-table! []
  (sql/db-do-commands db-spec
                      (sql/create-table-ddl :point
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
  (sql/query db-spec ["SELECT * FROM point WHERE path_id = ?" id]))

(get-path 0)

(defn next-path-id []
  (-> (sql/query db-spec ["SELECT max(path_id) + 1 FROM point"])
      first vals first (or 0)))

(defn add-path! [path]
  (let [path-id (next-path-id)]
    (doall (map-indexed
             (fn [index point]
               (add-point! (assoc point :path-id path-id :index index)))
             path))))

(defn clear-points! []
  (sql/delete! db-spec :point []))

(add-path! [{:latitude 34.6 :longitude 45.8}
            {:latitude 34.7 :longitude 49.4}
            {:latitude 23.4 :longitude 51.1}])

