(ns gps-tracker.handlers
  (:require [gps-tracker.db :as db]
            [gps-tracker.remote :as remote]
            [gps-tracker.util :as util]
            [gps-tracker.navigation :as navigation]))

; Todo: find a solution to this nil shit

(defn delete-path [id]
  (remote/delete-path id)
  nil)

(defn delete-waypoint-path [id]
  (navigation/redirect {:handler :waypoint-paths})
  (remote/delete-waypoint-path id)
  nil)

(defn show-path [id]
  (db/transition (fn [db] (assoc-in db [:page :path-id] id)))
  nil)

(defn upload-waypoint-path []
  (remote/upload-waypoint-path (db/query [:page :waypoint-path]))
  nil)

(defn add-waypoint-to-path [point]
    (db/transition
      (fn [db]
        (update-in db [:page :waypoint-path]
                   (fn [path]
                     (if (nil? path) [point] (conj path point))))))
  nil)

