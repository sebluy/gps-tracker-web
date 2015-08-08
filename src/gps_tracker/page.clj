(ns gps-tracker.page)

(defn stylesheet [href]
  [:link {:rel  "stylesheet" :href href}])

(defn javascript [src]
  [:script {:type "text/javascript" :src src}])

(defn page []
  [:html
   [:head [:title "GPS Tracker"]]
   [:body [:div#app]]
   (stylesheet "//maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap.min.css")
   (stylesheet "//maxcdn.bootstrapcdn.com/bootstrap/3.2.0/css/bootstrap-theme.min.css")
   (stylesheet "/css/screen.css")
   (javascript "https://maps.googleapis.com/maps/api/js")
   (javascript "/js/app.js")])
