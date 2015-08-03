(ns gps-tracker.map
  #_(:require [goog.events :as events]
            [goog.dom :as dom]
            [ajax.core :refer [GET]]))

;(def zoom 16)
;(def canvas-id "map-canvas")
;(def coordinates-id "coordinates")
;(def center (google.maps.LatLng. 44.9 -68.7))
;
;(def google-map nil)
;(def poly nil)
;
;(def path-options
;  {:geodesic true
;   :strokeColor "#FF0000"
;   :strokeOpacity 1.0
;   :strokeWeight 2})
;
;(defn get-canvas []
;  (dom/getElement canvas-id))
;
;(defn make-polyline [coordinates]
;  (set! poly
;        (google.maps.Polyline.
;          (clj->js (merge {:path coordinates} path-options)))))
;
;(defn make-google-map [center]
;  (let [map-options {:center (clj->js center) :zoom zoom}]
;    (set! google-map (google.maps.Map. (get-canvas) (clj->js map-options)))))
;
;(defn add-point [event]
;  (.push (.getPath poly) (.-latLng event)))
;
;(defn draw-coordinates [coordinates]
;  (do (make-google-map (first coordinates))
;      (make-polyline coordinates)
;      (.setMap poly google-map)
;      (google.maps.event.addListener google-map "click" add-point)))
;
;(defn handle-coordinates [response]
;  (draw-coordinates (response :coordinates)))
;
;(defn get-coordinates []
;  (GET "/coordinates" {:handler handle-coordinates
;                       :response-format :edn}))
;
;(defn load []
;  (get-coordinates))
;
