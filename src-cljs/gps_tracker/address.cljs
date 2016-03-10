(ns gps-tracker.address)

(defn tag [tag]
  (fn [val]
    (if (seq? val)
      (cons tag val)
      (list tag val))))

(defn forward [address f]
  (fn [action] (address (f action))))
