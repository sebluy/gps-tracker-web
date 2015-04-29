(ns gps-watch-web.core
  (:require [goog.events :as events]
            [goog.dom :as dom]))

(def zoom 16)
(def canvas-id "map-canvas")
(def coordinates-id "coordinates")

(defn get-canvas []
  (dom/getElement canvas-id))

(defn parse-coordinates []
  (->> (dom/getElement coordinates-id)
       (dom/getTextContent)
       (.parse js/JSON)
       (js->clj)))

(defn add-point [last-point new-point google-map]
  (.setMap (google.maps.Polyline.
             (clj->js {:path [last-point new-point]
                        :geodesic true
                        :strokeColor "#FF0000"
                        :strokeOpacity 1.0
                        :strokeWeight 2}))
           google-map))

(defn map-load []
  (let [coordinates (parse-coordinates)
        map-options {:center (coordinates 0) :zoom zoom}
        google-map (google.maps.Map. (get-canvas) (clj->js map-options))]
    (reduce (fn [last-point new-point]
              (do (add-point last-point new-point google-map)
                  new-point))
            coordinates)))

(defn init! []
  (map-load))
