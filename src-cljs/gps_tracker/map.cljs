(ns gps-tracker.map
  (:require [goog.dom :as dom]
            [gps-tracker.handlers :as handlers]
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

(defn make-google-map [center]
  (google.maps.Map. (get-canvas) (clj->js {:center center
                                           :zoom 1
                                           :mapTypeId google.maps.MapTypeId.HYBRID})))

(defn make-bounded-google-map [bounds]
  (doto (make-google-map (.getCenter bounds))
    (.fitBounds bounds)
    (.panToBounds bounds)))

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
        map (make-bounded-google-map bounds)
        poly (make-polyline latlngs)]
    (add-markers map path)
    (.setMap poly map)))

(defn viewing-map
  "Creates a reagent component using for viewing (not modifying) paths."
  [path]
  (reagent/create-class
    {:reagent-render      div
     :component-did-mount #(draw-map-with-path path)}))

(defn add-latlng-to-waypoint-path [map polyline latlng]
  (.push (.getPath polyline) latlng)
  (google.maps.Marker. (clj->js {:position latlng :map map}))
  (handlers/add-waypoint-to-path (latlng->point latlng)))

(defn draw-waypoint-creation-map
  []
  (let [map (make-google-map (point->latlng {:latitude 0.0 :longtiude 0.0}))
        polyline (make-polyline [])]
    (.setMap polyline map)
    (.addListener map "click"
                  (fn [event]
                    (add-latlng-to-waypoint-path
                      map
                      polyline
                      (.-latLng event))))))

(defn waypoint-creation-map
  "Creates a reagent component using for creating new waypoint paths."
  []
  (reagent/create-class
    {:reagent-render      div
     :component-did-mount draw-waypoint-creation-map}))
