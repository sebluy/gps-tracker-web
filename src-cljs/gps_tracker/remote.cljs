(ns gps-tracker.remote
  (:require [ajax.core :as ajax]
            [schema.core :as s]
            [gps-tracker.schema-helpers :as sh]))

(def Remote s/Bool)

(s/defschema Action
  (s/either
   (sh/action :get-waypoint-paths s/Any)
   (sh/action :delete-waypoint-path #_path-id s/Any)
   (sh/action :create-waypoint-path #_path s/Any)
   (sh/action :receive )))

(defn on-error
  [_]
  (js/alert "Remote error... You may need to refresh the page."))

(defn post-actions
  [actions callback]
  (ajax/POST
   "/api"
    {:params          actions
     :handler         #(callback %)
     :error-handler   on-error
     :format          :edn
     :response-format :edn}))

(s/defn handle :- Remote [address action :- Action state :- Remote]
  (case (first action)
    :get-waypoint-paths
    (get-waypoint-paths #(address (last action)))
    )
  (->> state
       (delegate action)
       (intercept action)))

(defn get-waypoint-paths [callback]
  (post-actions [{:action :get-paths
                  :path-type :waypoint}]
                (comp callback first)))

(defn create-waypoint-path [path]
  (post-actions [{:action :add-path
                  :path-type :waypoint
                  :path path}]
                identity))

(defn delete-waypoint-path [path-id]
  (post-actions [{:action :delete-path
                  :path-type :waypoint
                  :path-id path-id}]
                identity))
