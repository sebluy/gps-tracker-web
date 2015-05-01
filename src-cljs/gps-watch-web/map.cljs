(ns gps-watch-web.map
  (:require [goog.events :as events]
            [goog.dom :as dom]
            [ajax.core :refer [GET]]))

(def zoom 16)
(def canvas-id "map-canvas")
(def coordinates-id "coordinates")
(def center (google.maps.LatLng. 44.9 -68.7))

(defn get-canvas []
  (dom/getElement canvas-id))

(defn add-point [last-point new-point google-map]
  (.setMap (google.maps.Polyline.
             (clj->js {:path [last-point new-point]
                       :geodesic true
                       :strokeColor "#FF0000"
                       :strokeOpacity 1.0
                       :strokeWeight 2}))
           google-map))

(defn draw-coordinates [coordinates]
  (let [map-options {:center (first coordinates) :zoom zoom}
        google-map (google.maps.Map. (get-canvas) (clj->js map-options))]
    (reduce (fn [last-point new-point]
              (do
                (add-point last-point new-point google-map)
                new-point))
            coordinates)))

(defn handle-coordinates [response]
  (draw-coordinates (response :coordinates)))

(defn get-coordinates []
  (GET "/coordinates" {:handler handle-coordinates
                       :response-format :edn}))

(defn load []
  (get-coordinates))
  
