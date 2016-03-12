(ns gps-tracker.address)

(defn tag [tag]
  (fn [val]
    (cond
      (and (seq? val) (seq? tag))
      (concat tag val)

      (seq? val)
      (cons tag val)

      (seq? tag)
      (concat tag (list val))

      :else
      (list tag val))))

(defn forward [address f]
  (fn [action] (address (f action))))
