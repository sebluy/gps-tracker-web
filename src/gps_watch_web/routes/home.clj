(ns gps-watch-web.routes.home
  (:require [gps-watch-web.layout :as layout]
            [gps-watch-web.db.core :as db]
            [compojure.core :refer [defroutes GET]]
            [ring.util.response :refer [response]]))

(defn home-page []
  (layout/render "home.html"))

(defn clean-coordinates [{:keys [latitude longitude]}]
  {:lat latitude :lng longitude})

(defn coordinates-response []
  (response {:coordinates (map clean-coordinates (db/get-coordinates))}))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/coordinates" [] (coordinates-response)))
