(ns gps-watch-web.map
  (:require [goog.events :as events]
            [goog.dom :as dom]))

(def zoom 16)
(def canvas-id "map-canvas")
(def coordinates-id "coordinates")
(def center (google.maps.LatLng. 44.9 -68.7))

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

(comment
  (defn load []
    (let [coordinates (parse-coordinates)
          map-options {:center (coordinates 0) :zoom zoom}
          google-map (google.maps.Map. (get-canvas) (clj->js map-options))]
      (reduce (fn [last-point new-point]
                (do (add-point last-point new-point google-map)
                    new-point))
              coordinates))))

(defn load []
  (let [map-options {:center center :zoom zoom}]
    (google.maps.Map. (get-canvas) (clj->js map-options))))

