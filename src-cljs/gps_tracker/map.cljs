(ns gps-tracker.map
  (:require [goog.dom :as dom]
            [reagent.core :as reagent]))

(def canvas-id "map-canvas")

(defn div []
  [:div#map-canvas])

(def path-options
  {:geodesic      true
   :strokeColor   "#FF0000"
   :strokeOpacity 1.0
   :strokeWeight  2})

(defn get-canvas []
  (dom/getElement canvas-id))

(defn draw-marker [map latlng]
  (google.maps.Marker. (clj->js {:position latlng :map map})))

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

(defn latlng->point [latlng]
  {:latitude (.lat latlng) :longitude (.lng latlng)})

(defn make-bounds [latlngs]
  (let [bounds (google.maps.LatLngBounds.)]
    (doseq [latlng latlngs]
      (.extend bounds latlng))
    bounds))

(defn path->latlngs [path]
  (map point->latlng path))

(defn add-markers [map path]
  (doseq [point path]
    (draw-marker map (point->latlng point))))

(defn draw-map-with-path [path]
  (let [latlngs (path->latlngs path)
        bounds (make-bounds latlngs)
        map (make-google-map bounds)
        poly (make-polyline latlngs)]
    (add-markers map path)
    (.setMap poly map)))

(defn viewing-map [path]
  (reagent/create-class
    {:reagent-render         div
     :component-did-mount    #(draw-map-with-path path)}))

#_(defn drawing-map [on-new-point]
  (reagent/create-class
    {:reagent-render         div
     :component-did-mount    #(draw-path initial-path)
     :component-will-unmount cleanup}))


