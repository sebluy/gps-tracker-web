(ns gps-tracker.pages.new-waypoint-path
  (:require [gps-tracker.map :as map]
            [gps-tracker.waypoint-paths :as wp]
            [gps-tracker.util :as util]))

(defn upload-button [on-click]
  [:input.btn.btn-lg.btn-primary
   {:type "button"
    :value "Create"
    :on-click on-click}])

(defn handle [action page]
  (case (first action)
    :add-point
    (update-in page [:path :points] conj (second action))

    page))

(defn segment-distance-list [path]
  (->> path
       (wp/segment-distances)
       (map util/distance->str)
       (map-indexed (fn [index distance] [:li {:key index} distance]))
       (into [:ul.list-unstyled])))

(defn view [address {:keys [path]}]
  [:div
   [:div.page-header
    [:h1 "New Waypoint Path"
     [:p.pull-right.btn-toolbar
      (upload-button #(address `(:create ~path)))]]]
   [:div.col-md-2
    [:h3 (util/distance->str (wp/distance path))]
    [:h3 (str (count (path :points)) " points")]
    (segment-distance-list path)]
   [:div.col-md-10 (map/WaypointCreationMap address)]])
