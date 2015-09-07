(ns gps-tracker.pages.core
  (:require [gps-tracker.pages.navbar :as navbar]
            [gps-tracker.pages.paths :as paths]
            [gps-tracker.pages.path :as path]
            [gps-tracker.pages.waypoint-path :as waypoint-path]
            [sigsub.core :as sigsub :include-macros true]))

(def pages {:paths paths/page
            :path path/page
            :waypoint-path waypoint-path/page})

(defn current-page []
  (sigsub/with-reagent-subs
    [handler [:page :handler]]
    (fn []
      [(or (pages @handler) :div)])))

(defn view []
  [:div.container
   [:div.row
    [:div.span12
     [navbar/navbar]
     [current-page]]]])

