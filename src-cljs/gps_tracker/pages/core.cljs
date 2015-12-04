(ns gps-tracker.pages.core
  (:require [gps-tracker.pages.navbar :as navbar]
            [gps-tracker.pages.waypoint-paths :as waypoint-paths]
            [gps-tracker.pages.new-waypoint-path :as new-waypoint-path]
            [gps-tracker.pages.waypoint-path :as waypoint-path]
            [sigsub.core :as sigsub :include-macros true]))


;Todo: reorganize pages more restful like rails

;;;; map from page-ids to views (rename ui to view)
(def view-map {;;:paths paths/ui
             ;;:path path/ui
             :waypoint-paths waypoint-paths/view
             :waypoint-path waypoint-path/view
             :new-waypoint-path new-waypoint-path/view})

(defn current-view []
  (sigsub/with-reagent-subs
    [id [:page :id]]
    (fn []
      [(or (view-map @id) :div)])))

(defn view []
  [:div
   [:div.container
    [:div.row
     [:div.span12
      [navbar/navbar]
      [current-view]]]]])
