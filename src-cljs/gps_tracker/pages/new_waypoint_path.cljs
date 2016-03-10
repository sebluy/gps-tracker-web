(ns gps-tracker.pages.new-waypoint-path
  (:require [gps-tracker.map :as map]
            [gps-tracker.address :as a]
            [sablono.core :as s]))

(defn upload-button [on-click]
  [:input.btn.btn-primary
   {:type "button"
    :value "Upload"
    :on-click on-click}])

(defn handle [action page]
  (case (first action)
    :add-point
    (update-in page [:path :points] conj (second action))

    page))

;; this distance code could probably live somewhere else
(defn point->latlng [point]
  (js/google.maps.LatLng. (point :latitude) (point :longitude)))

(defn distance-between [[a b]]
  (js/google.maps.geometry.spherical.computeDistanceBetween
   (point->latlng a)
   (point->latlng b)))

(defn segment-distance-list [{:keys [points]}]
  (->> points
       (partition 2 1)
       (map distance-between)
       (map-indexed (fn [index distance] [:li {:key index} distance]))
       (into [:ul])))

(defn view [address {:keys [path]}]
  (s/html
   [:div
    [:div.page-header
     [:h1 "New Waypoint"
      [:p.pull-right.btn-toolbar
       (upload-button #(address '(:create path)))]]]
    [:div.col-md-8 (map/WaypointCreationMap address)]
    [:div.col-md-2
     [:h1 (str "Count: " (count (path :points)))]
     (segment-distance-list path)]]))
