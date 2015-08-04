(ns gps-tracker.core
  (:require [gps-tracker.page :as page]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.defaults :as middleware]
            [ring.util.response :as response]
            [hiccup.core :as hiccup]
            [compojure.core :as compojure])
  (:gen-class))

(defmulti api-action first)

(defmethod api-action :add-path [[_ path]]
  (db/add-path path))

(defn api [actions]
  (doseq [action actions]
    (api-action action))
  (response/response nil))

(compojure/defroutes
  routes
  (compojure/ANY "/api" {actions :body-params} (api actions))
  (compojure/GET "/" [] (hiccup/html (page/page))))

(defn parse-port [port]
  (Integer/parseInt (or port (System/getenv "PORT") "3000")))

(def handler (middleware/wrap-defaults routes middleware/site-defaults))

(defn -main [& [port]]
  (let [port (parse-port port)]
    (jetty/run-jetty handler {:port port :join? false})))



