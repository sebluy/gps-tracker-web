(ns gps-tracker.page)

(defn stylesheet [href]
  [:link {:rel  "stylesheet" :href href}])

(defn javascript [src]
  [:script {:type "text/javascript" :src src}])

(defn page []
  [:html
   [:head [:title "GPS Tracker"]]
   [:body [:div#app]]
   ; bootstrap
   (stylesheet "//maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css")
   (stylesheet "//maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap-theme.min.css")
   ; minimal custom css
   (stylesheet "/css/screen.css")
   ; google maps api
   (javascript "https://maps.googleapis.com/maps/api/js?libraries=geometry")
   ; js application
   (javascript "/js/app.js")])
