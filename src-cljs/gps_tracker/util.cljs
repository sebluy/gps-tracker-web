(ns gps-tracker.util)

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

