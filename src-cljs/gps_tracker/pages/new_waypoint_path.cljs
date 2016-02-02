(ns gps-tracker.pages.new-waypoint-path
  (:require [gps-tracker.map :as map]
            [sigsub.core :as sigsub :include-macros true]
            [gps-tracker.handlers :as handlers]))

;; this distance code could probably live somewhere else
(defn point->latlng [point]
  (google.maps.LatLng. (point :latitude) (point :longitude)))

(defn distance-between [[a b]]
  (js/google.maps.geometry.spherical.computeDistanceBetween
   (point->latlng a)
   (point->latlng b)))

(defn segment-distance-list [{:keys [points]}]
  (->> points
       (partition 2 1)
       (map distance-between)
       (map (fn [distance] [:li distance]))
       (into [:ul])))

(defn upload-button []
  [:input.btn.btn-primary
   {:type     "button"
    :value    "Upload"
    :on-click handlers/upload-waypoint-path}])

(defn view []
  (sigsub/with-reagent-subs
    [path [:page :waypoint-path]]
    [:div
     [:div.page-header
      [:h1 "New Waypoint"
       [:p.pull-right.btn-toolbar
        [upload-button]]]]
     [:div.col-md-8 [map/waypoint-creation-map]]
     [:div.col-md-2
      [:h1 (str "Count: " (count (@path :points)))]
      (segment-distance-list @path)]]))
