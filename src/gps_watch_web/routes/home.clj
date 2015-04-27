(ns gps-watch-web.routes.home
  (:require [gps-watch-web.layout :as layout]
            [gps-watch-web.db.core :as db]
            [compojure.core :refer [defroutes GET]]
            [clojure.java.io :as io]
            [clojure.data.json :as json]))

(defn home-page []
  (layout/render
    "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn about-page []
  (layout/render "about.html"))

(defn clean-coordinates [{:keys [latitude longitude]}]
  {:lat latitude :lng longitude})

(defn map-page []
  (layout/render "map.html"
                 {:coordinates (json/write-str
                                 (map clean-coordinates
                                      (db/get-coordinates)))}))
(defn coordinates-page []
  (layout/render "coordinates.html"
                 {:coordinates (db/get-coordinates)}))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page))
  (GET "/map" [] (map-page))
  (GET "/coordinates" [] (coordinates-page)))
