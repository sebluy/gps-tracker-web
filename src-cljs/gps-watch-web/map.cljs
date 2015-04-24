(ns gps-watch-web.map)

(def map-opts
  {"zoom" 8
   "center" (google.maps.LatLng. -34.397, 150.644)})

(defn map-load []
  let [elem (goog.dom/getElement "map-canvas")]
  set! *map* (google.maps.Map. elem map-opts))

(events/listen js/window "load"
               map-load)
