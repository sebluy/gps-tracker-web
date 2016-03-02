(ns gps-tracker.map
  (:require [goog.dom :as dom]
            [om.next :as om]
            [sablono.core :as sablono]))

(def canvas-id "map-canvas")

(def div [:div#map-canvas])

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

(defn make-google-map [target center]
  (google.maps.Map. target (clj->js {:center center
                                     :zoom 1
                                     :mapTypeId google.maps.MapTypeId.HYBRID})))

(defn make-bounded-google-map [target bounds]
  (doto (make-google-map target (.getCenter bounds))
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

(defn draw-map-with-path [target path]
  (let [latlngs (path->latlngs path)
        bounds (make-bounds latlngs)
        map (make-bounded-google-map target bounds)
        poly (make-polyline latlngs)]
    (add-markers map path)
    (.setMap poly map)))

(defn with-valid-target [target f]
  (when target
    (f target)))

(om/defui ViewingMap
  Object
  (componentDidMount
   [this]
   (draw-map-with-path (om/get-state this) (om/props this)))
  (render
   [this]
   (sablono/html [:div {:style {:height "100%"}
                        :ref (fn [target]
                               (om/set-state! this target))}])))

(def viewing-map (om/factory ViewingMap))

(defn add-latlng-to-waypoint-path [component map polyline latlng]
  (.push (.getPath polyline) latlng)
  (google.maps.Marker. (clj->js {:position latlng :map map}))
  (om/update-state! component update :points conj (latlng->point latlng)))

(defn draw-waypoint-creation-map
  [target component]
  (let [map (make-google-map target (point->latlng {:latitude 0.0 :longtiude 0.0}))
        polyline (make-polyline [])]
    (.setMap polyline map)
    (.addListener map "click"
                  (fn [event]
                    (add-latlng-to-waypoint-path
                     component
                     map
                     polyline
                     (.-latLng event))))))

(om/defui WaypointCreationMap
  Object
  (componentDidMount
   [this]
   (draw-waypoint-creation-map (om/get-state this) (om/props this)))
  (render
   [this]
   (sablono/html [:div {:style {:height "100%"}
                        :ref (fn [target]
                               (om/set-state! this target))}])))

(def waypoint-creation-map (om/factory WaypointCreationMap))
