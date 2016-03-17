(ns gps-tracker.waypoint-paths
  (:require [schema.core :as s]
            [gps-tracker.schema-helpers :as sh]
            [gps-tracker-common.schema :as cs]))

(s/defschema Action
  (s/either
   (sh/list :create (sh/singleton cs/WaypointPath))
   (sh/list :refresh (sh/singleton [cs/WaypointPath]))
   (sh/list :delete (sh/singleton cs/PathID))))

(s/defn handle :- [cs/WaypointPath] [action :- Action paths :- [cs/WaypointPath]]
  (case (first action)
    :create
    (let [path (last action)]
      (conj paths path))

    :delete
    (let [id (last action)]
      (filterv (fn [path] (not= id (path :id))) paths))

    :refresh
    (let [paths (last action)]
      paths)

    paths))

(defn point->latlng [point]
  (js/google.maps.LatLng. (point :latitude) (point :longitude)))

(defn distance-between [[a b]]
  (js/google.maps.geometry.spherical.computeDistanceBetween
   (point->latlng a)
   (point->latlng b)))

(defn segment-distances [{:keys [points]}]
  (->> points
       (partition 2 1)
       (map distance-between)))

(defn distance [path]
  (reduce + 0 (segment-distances path)))

(defn valid? [path]
  (> (count (path :points)) 0))

(defn find [paths id]
  (->> paths
       (filter (fn [path] (= (path :id) id)))
       first))
