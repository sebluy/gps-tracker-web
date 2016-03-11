(ns gps-tracker.pages.waypoint-path
  (:require [gps-tracker.map :as map]
            [gps-tracker.util :as util]
            [gps-tracker.waypoint-paths :as wp]))

(defn delete-button [on-click]
  [:input.btn.btn-lg.btn-danger
   {:value "Delete"
    :type "button"
    :on-click on-click}])

(defn view [address {:keys [points id] :as path}]
  [:div
   [:div.page-header
    [:h1 (.toLocaleString id)
     [:p.pull-right.btn-toolbar
      (delete-button #(address `(:delete ~id)))]]]
   [:h3 (str (count points) " points, " (util/distance->str (wp/distance path)))]
   (map/ViewingMap points)])
