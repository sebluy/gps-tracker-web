(ns gps-tracker.map
  (:require [goog.dom :as dom]
            [gps-tracker.db :as db]
            [clojure.set :as set]))

(def canvas-id "map-canvas")

(def markers (atom {}))
(def current-map (atom nil))

(def path-options
  {:geodesic true
   :strokeColor "#FF0000"
   :strokeOpacity 1.0
   :strokeWeight 2})

(defn cleanup []
  (reset! markers {})
  (reset! current-map nil))

(defn get-canvas []
  (dom/getElement canvas-id))

(defn make-polyline [coordinates]
  (google.maps.Polyline.
    (clj->js (merge {:path coordinates} path-options))))

(defn make-google-map [bounds]
  (let [map-options {:center (.getCenter bounds)}
        map (doto (google.maps.Map. (get-canvas) (clj->js map-options))
               (.fitBounds bounds)
               (.panToBounds bounds))]
    (reset! current-map map)))

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

(defn add-marker [point]
  (let [options (clj->js {:position (point->latlng point)})
        marker (google.maps.Marker. options)]
    (.setMap marker @current-map)
    (swap! markers assoc point marker)))

(defn remove-marker [point]
  (.setMap (@markers point) nil)
  (swap! markers dissoc point))

(defn toggle-marker [point]
  (if (@markers point)
    (remove-marker point)
    (add-marker point)))
