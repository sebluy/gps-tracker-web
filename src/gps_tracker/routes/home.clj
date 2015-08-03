(ns gps-tracker.routes.home
  (:require [gps-tracker.layout :as layout]
            [gps-tracker.db.core :as db]
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
