(ns gps-tracker.pages.show-waypoint-path
  (:require [gps-tracker.map :as map]
            [sigsub.core :as sigsub :include-macros true]
            [gps-tracker.handlers :as handlers]))

(defn page []
  (sigsub/with-reagent-subs
    [id [:page :path-id]
     path [:waypoint-path @id]]
    (fn []
      (when (not= @path :pending)
        [map/viewing-map @path]))))

