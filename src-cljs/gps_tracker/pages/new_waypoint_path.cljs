(ns gps-tracker.pages.new-waypoint-path
  (:require [gps-tracker.map :as map]
            [sigsub.core :as sigsub :include-macros true]
            [gps-tracker.handlers :as handlers]))

(defn upload-button []
  [:input.btn.btn-primary
   {:type     "button"
    :value    "Upload"
    :on-click handlers/upload-waypoint-path}])

(defn view []
  [:div
   [:div.page-header
    [:h1 "New Waypoint"
     [:p.pull-right.btn-toolbar
      [upload-button]]]]
   [:div.col-md-6 [map/waypoint-creation-map]]])
