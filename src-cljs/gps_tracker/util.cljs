(ns gps-tracker.util)

;; maybe move to common
(defn dissoc-in [map path]
  (condp = (count path)
    0 map
    1 (dissoc map (first path))
    (let [sub-path (pop path)
          leaf (get-in map sub-path)
          dissociated (dissoc leaf (peek path))]
      (if (empty? dissociated)
        (dissoc-in map sub-path)
        (assoc-in map sub-path dissociated)))))

(defn distance->str [distance]
  (if (> distance 1000)
    (str (js/Math.floor (/ distance 1000.0)) " km")
    (str (js/Math.floor distance) " m")))
