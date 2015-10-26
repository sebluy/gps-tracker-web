(ns gps-tracker.handlers
  (:require [gps-tracker.db :as db]
            [gps-tracker.remote :as remote]
            [gps-tracker.util :as util]
            [gps-tracker.navigation :as navigation]))

; Todo: find a solution to this nil shit

(defn delete-path [id]
  (remote/delete-path id)
  (navigation/redirect {:handler :paths})
  nil)

(defn delete-waypoint-path [id]
  (remote/delete-waypoint-path id)
  (navigation/redirect {:handler :waypoint-paths})
  nil)

(defn show-path [id]
  (db/transition (fn [db] (assoc-in db [:page :path-id] id)))
  nil)

(defn create-waypoint-path []
  (navigation/redirect {:handler :new-waypoint-path})
  (db/transition (fn [db] (assoc-in db [:page :waypoint-path] {:id (js/Date.) :points []}))))

(defn upload-waypoint-path []
  (remote/upload-waypoint-path (db/query [:page :waypoint-path]))
  (navigation/redirect {:handler :waypoint-paths})
  nil)

(defn add-waypoint-to-path [point]
  (db/transition
   (fn [db] (update-in db [:page :waypoint-path :points] conj point)))
  nil)
