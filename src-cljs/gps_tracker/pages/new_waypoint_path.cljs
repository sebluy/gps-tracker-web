(ns gps-tracker.pages.new-waypoint-path
  (:require [gps-tracker.waypoint-map :as map]
            [sigsub.core :as sigsub :include-macros true]
            [gps-tracker.handlers :as handlers]))

(defn show-point [index point]
  [:tr
   [:td (point :latitude)]
   [:td (point :longitude)]])

(defn path-table []
  (sigsub/with-reagent-subs
    [path [:page :waypoint-path]]
    (fn []
      [:table.table
       [:thead
        [:td "Latitude"]
        [:td "Longitude"]
        [:tbody
         (doall
           (map-indexed
             (fn [index point]
               ^{:key index}
               [show-point index point]) @path))]]])))

(defn upload-button []
  [:input.btn.btn-primary
   {:type     "button"
    :value    "Upload"
    :on-click handlers/upload-waypoint-path}])

(defn page []
  (sigsub/with-reagent-subs
    [path [:page :waypoint-path]]
    (fn []
      [:div
       [:div.page-header
        [:h1 "New Waypoint"
         [:p.pull-right.btn-toolbar
          [upload-button]]]]
       [:div.col-md-6 [path-table]]
       [:div.col-md-6 [map/google-map @path]]])))

