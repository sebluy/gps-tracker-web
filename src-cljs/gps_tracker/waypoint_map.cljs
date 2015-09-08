(ns gps-tracker.waypoint-map
  (:require [goog.dom :as dom]
            [gps-tracker.db :as db]
            [clojure.set :as set]
            [reagent.core :as reagent]
            [gps-tracker.util :as util]))

; Todo: this mapping is going to need a major refactoring

(def canvas-id "map-canvas")

(defn div []
  [:div#map-canvas])

(def path-options
  {:geodesic      true
   :strokeColor   "#FF0000"
   :strokeOpacity 1.0
   :strokeWeight  2})

(defn cleanup []
  (db/transition (fn [db] (dissoc db :map))))

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
    (db/transition (fn [db] (assoc-in db [:map :map] map)))
    map))

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

(defn add-latlng-to-waypoint-path [latlng]
  (.push (.getPath (db/query [:map :polyline])) latlng)
  (google.maps.Marker.
    (clj->js {:position latlng :map (db/query [:map :map])}))
  (let [point (latlng->point latlng)]
    (db/transition
      (fn [db]
        (update-in db [:page :waypoint-path] (fn [path]
                                               (if (nil? path)
                                                 [point]
                                                 (conj path point))))))))

(defn draw-path [path]
  (let [latlngs (path->latlngs path)
        bounds (make-bounds latlngs)
        map (make-google-map bounds)
        poly (make-polyline latlngs)]
    (.addListener map "click"
                  (fn [event] (add-latlng-to-waypoint-path (.-latLng event))))
    (db/transition (fn [db] (assoc-in db [:map :polyline] poly)))
    (.setMap poly map)))

(defn color [point]
  (let [accuracy (point :accuracy)
        redness (if (nil? accuracy)
                  255
                  (.round js/Math (min (* accuracy 10.0) 255.0)))]
    (str "#" (.toString redness 16) "0000")))

(defn add-marker [point]
  (let [options (clj->js {:position (point->latlng point)
                          :icon     {:path        google.maps.SymbolPath.CIRCLE
                                     :strokeColor (color point)
                                     :scale       10}
                          :map      (db/query [:map :map])})
        marker (google.maps.Marker. options)]
    (db/transition
      (fn [db] (update-in db [:map :markers] assoc point marker)))))

(defn remove-marker [point]
  (.setMap (db/query [:map :markers point]) nil)
  (db/transition
    (fn [db] (util/dissoc-in db [:map :markers point]))))

(defn toggle-marker [point]
  (if (db/query [:map :markers point])
    (remove-marker point)
    (add-marker point)))

(defn google-map [initial-path]
  (reagent/create-class
    {:reagent-render         div
     :component-did-mount    #(draw-path initial-path)
     :component-will-unmount cleanup}))

