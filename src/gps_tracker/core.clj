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

(compojure/defroutes
  routes
  (route/resources "/")
  (compojure/ANY "/api" {actions :body-params}
                 (response/response (db/execute-api-actions actions)))
  (compojure/GET "/" [] (hiccup/html (page/page))))

(defn parse-port [port]
  "Returns the port to use for the web server.
   3000 by default (for development) or use a system provided port (production)."
  (Integer/parseInt (or port (System/getenv "PORT") "3000")))

(def handler
  "A handler with just default middleware and support for edn and json-kw."
  (let [base-handler (-> #'routes
                         (format/wrap-restful-format :formats [:edn :json-kw])
                         (middleware/wrap-defaults middleware/api-defaults))]
    (if (environ/env :dev)
      (reload/wrap-reload base-handler)
      base-handler)))

(defn -main [& [port]]
  (let [port (parse-port port)]
    (jetty/run-jetty handler {:port port :join? false})))
