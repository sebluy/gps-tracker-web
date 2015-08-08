(ns gps-tracker.map
  (:require [goog.dom :as dom]
            [gps-tracker.db :as db]
            [clojure.set :as set]))

(def canvas-id "map-canvas")

(def path-options
  {:geodesic true
   :strokeColor "#FF0000"
   :strokeOpacity 1.0
   :strokeWeight 2})

(defn get-canvas []
  (dom/getElement canvas-id))

(defn make-polyline [coordinates]
  (google.maps.Polyline.
    (clj->js (merge {:path coordinates} path-options))))

(defn make-google-map [bounds]
  (let [map-options {:center (.getCenter bounds)}]
    (doto (google.maps.Map. (get-canvas) (clj->js map-options))
      (.fitBounds bounds)
      (.panToBounds bounds))))

(defn point->latlng [point]
  (google.maps.LatLng. (point :latitude) (point :longitude)))

(defn make-bounds [latlngs]
  (let [bounds (google.maps.LatLngBounds.)]
    (doseq [latlng latlngs]
      (.extend bounds latlng))
    bounds))

(defn path->latlngs [path]
  (map point->latlng path))

(defn draw-path [path]
  (let [latlngs (path->latlngs path)
        bounds (make-bounds latlngs)
        map (make-google-map bounds)
        poly (make-polyline latlngs)]
    (.setMap poly map)))


