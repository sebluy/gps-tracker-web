(ns gps-tracker.core
  (:require [gps-tracker.page :as page]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.defaults :as middleware]
            [hiccup.core :as hiccup]
            [compojure.core :as compojure])
  (:gen-class))

(compojure/defroutes
  routes
  (compojure/GET "/" [] (hiccup/html (page/page))))

(defn parse-port [port]
  (Integer/parseInt (or port (System/getenv "PORT") "3000")))

(def handler (middleware/wrap-defaults routes middleware/site-defaults))

(defn -main [& [port]]
  (let [port (parse-port port)]
    (jetty/run-jetty handler {:port port :join? false})))



