(ns gps-tracker.pages.new-waypoint-path
  (:require [gps-tracker.map :as map]
            [gps-tracker.waypoint-paths :as wp]))

(defn upload-button [on-click]
  [:input.btn.btn-primary
   {:type "button"
    :value "Create"
    :on-click on-click}])

(defn handle [action page]
  (case (first action)
    :add-point
    (update-in page [:path :points] conj (second action))

    page))

(defn segment-distance-list [{:keys [points]}]
  (->> points
       (partition 2 1)
       (map wp/distance-between)
       (map-indexed (fn [index distance] [:li {:key index} distance]))
       (into [:ul])))

(defn view [address {:keys [path]}]
  [:div
   [:div.page-header
    [:h1 "New Waypoint Path"
     [:p.pull-right.btn-toolbar
      (upload-button #(address `(:create ~path)))]]]
   [:div.col-md-8 (map/WaypointCreationMap address)]
   [:div.col-md-2
    [:h1 (str "Count: " (count (path :points)))]
    (segment-distance-list path)]])
