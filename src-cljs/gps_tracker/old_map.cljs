(ns gps-tracker.map
  (:require [goog.dom :as dom]
            [gps-tracker.db :as db]
            [clojure.set :as set]))

(def canvas-id "map-canvas")

(def markers (atom {}))
(def current-map (atom nil))

(def path-options
  {:geodesic      true
   :strokeColor   "#FF0000"
   :strokeOpacity 1.0
   :strokeWeight  2})

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

(defn color [point]
  (let [accuracy (point :accuracy)
        redness (if (nil? accuracy)
                  255
                  (.round js/Math (min (* accuracy 10.0) 255.0)))]
    (str "#" (.toString redness 16) "0000")))

(defn add-marker [point]
  (let [options (clj->js {:position (point->latlng point)
                          :icon     {:path  google.maps.SymbolPath.CIRCLE
                                     :strokeColor (color point)
                                     :scale 10}
                          :map      @current-map})
        marker (google.maps.Marker. options)]
    (swap! markers assoc point marker)))

(defn remove-marker [point]
  (.setMap (@markers point) nil)
  (swap! markers dissoc point))

(defn toggle-marker [point]
  (if (@markers point)
    (remove-marker point)
    (add-marker point)))
