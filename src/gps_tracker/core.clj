(ns gps-tracker.core
  (:require [gps-tracker.page :as page]
            [gps-tracker.db :as db]
            [environ.core :as environ]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.defaults :as middleware]
            [ring.middleware.reload :as reload]
            [ring.middleware.format :as format]
            [ring.util.response :as response]
            [hiccup.core :as hiccup]
            [compojure.core :as compojure]
            [compojure.route :as route])
  (:gen-class))

(defmulti api-action (comp keyword first))

(defmethod api-action :delete-path [[_ path-id]]
  (db/delete-path! path-id))

(defmethod api-action :delete-waypoint-path [[_ path-id]]
  (db/delete-waypoint-path! path-id))

(defmethod api-action :add-path [[_ path]]
  (db/add-path! path))

(defmethod api-action :add-waypoint-path [[_ path]]
  (db/add-waypoint-path! path))

(defmethod api-action :get-path [[_ path-id]]
  (db/get-path path-id))

(defmethod api-action :get-waypoint-path [[_ path-id]]
  (db/get-waypoint-path path-id))

(defmethod api-action :get-path-ids [_]
  (db/get-path-ids))

(defmethod api-action :get-waypoint-paths [_]
  (db/get-waypoint-paths))

(defmethod api-action :get-waypoint-path-ids [_]
  (db/get-waypoint-path-ids))

(defn api [actions]
  (response/response
    (map api-action actions)))

(compojure/defroutes
  routes
  (route/resources "/")
  (compojure/ANY "/api" {actions :body-params} (api actions))
  (compojure/GET "/" [] (hiccup/html (page/page))))

(defn parse-port [port]
  (Integer/parseInt (or port (System/getenv "PORT") "3000")))

(def handler
  (let [base-handler (-> #'routes
                         (format/wrap-restful-format :formats [:edn :json-kw])
                         (middleware/wrap-defaults middleware/api-defaults))]
    (if (environ/env :dev)
      (reload/wrap-reload base-handler)
      base-handler)))

(defn -main [& [port]]
  (let [port (parse-port port)]
    (jetty/run-jetty handler {:port port :join? false})))
