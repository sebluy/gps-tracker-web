(ns gps-tracker.waypoint-paths)

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

(defn handle [action paths]
  (case (first action)
    :create
    (let [path (second action)]
      (conj paths path))

    :delete
    (let [id (second action)]
      (filterv (fn [path] (not= id (path :id))) paths))

    paths))
