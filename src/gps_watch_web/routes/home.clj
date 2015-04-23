(ns gps-watch-web.routes.home
  (:require [gps-watch-web.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [clojure.java.io :as io]))

(defn home-page []
  (layout/render
    "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn about-page []
  (layout/render "about.html"))

(defn map-page []
  (layout/render "map.html"))

(defn mod-map-page []
  (layout/render "mod_map.html"))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/about" [] (about-page))
  (GET "/map" [] (map-page))
  (GET "/mod-map" [] (mod-map-page)))
