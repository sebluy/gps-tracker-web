(ns gps-tracker.handlers
  (:require [gps-tracker.db :as db]
            [gps-tracker.remote :as remote]
            [gps-tracker.util :as util]))

(defn delete-path [id]
  (if (= (db/query [:page :path-id]) id)
    (db/transition (fn [db] (util/dissoc-in db [:page :path-id]))))
  (remote/delete-path id))

(defn show-path [id]
  (db/transition (fn [db] (assoc-in db [:page :path-id] id))))

(defn upload-waypoint-path []
  (remote/upload-waypoint-path (db/query [:page :waypoint-path])))

(defn add-waypoint-to-path [point]
    (db/transition
      (fn [db]
        (update-in db [:page :waypoint-path]
                   (fn [path]
                     (if (nil? path) [point] (conj path point)))))))

