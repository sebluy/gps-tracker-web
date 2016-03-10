(ns gps-tracker.pages.waypoint-path
  (:require [gps-tracker.map :as map]
            [gps-tracker.waypoint-paths :as wp]))

(defn delete-button [on-click]
  [:input.btn.btn-danger
   {:value "Delete"
    :type "button"
    :on-click on-click}])

(defn view [address {:keys [points id]}]
  [:div
   [:div.page-header
    [:h1 (.toLocaleString id)
     [:p.pull-right.btn-toolbar
      (delete-button #(address `(:delete ~id)))]]]
   [:h3 (str "Points: " (count points))]
   [:h3 (str "Distance: " (js/Math.floor (wp/distance points)) " m")]
   (map/ViewingMap points)])
