(ns gps-tracker.remote
  (:require [ajax.core :as ajax]
            [schema.core :as s]
            [gps-tracker-common.schema :as cs]
            [gps-tracker.schema-helpers :as sh]))

(def Remote s/Bool)

(s/defschema SendAction
  (s/either
   (s/eq '(:get-waypoint-paths))
   (sh/action :delete-waypoint-path cs/PathID)
   (sh/action :create-waypoint-path cs/Path)))

(s/defschema ReceiveAction
  (s/either
   (sh/action :get-waypoint-paths [cs/Path])
   (sh/action :delete-waypoint-path cs/PathID)
   (sh/action :create-waypoint-path cs/Path)))

(s/defschema Action
  (s/either
   (sh/action :send SendAction)
   (sh/action :receive ReceiveAction)))

(defn on-error
  [_]
  (js/alert "Remote error... You may need to refresh the page."))

(defn post-actions
  [actions callback]
  (ajax/POST
   "/api"
    {:params          actions
     :handler         callback
     :error-handler   on-error
     :format          :edn
     :response-format :edn}))

(defn get-waypoint-paths [callback]
  (post-actions [{:action :get-paths
                  :path-type :waypoint}]
                (comp callback first)))

(defn create-waypoint-path [path callback]
  (post-actions [{:action :add-path
                  :path-type :waypoint
                  :path path}]
                (comp callback first)))

(defn delete-waypoint-path [path-id callback]
  (post-actions [{:action :delete-path
                  :path-type :waypoint
                  :path-id path-id}]
                (comp callback first)))

(s/defn handle-send [address action :- SendAction]
  (case (first action)
    :get-waypoint-paths
    (get-waypoint-paths
     (fn [paths]
       (address `(:receive :get-waypoint-paths ~paths))))

    :delete-waypoint-path
    (let [path-id (last action)]
      (delete-waypoint-path
       path-id
       (fn [_]
         (address `(:receive :delete-waypoint-path ~path-id)))))

    :create-waypoint-path
    (let [path (last action)]
      (create-waypoint-path
       path
       (fn [_]
         (address `(:receive :create-waypoint-path ~path)))))

    nil))

(s/defn handle :- Remote [address action :- Action remote :- Remote]
  (case (first action)

    :send
    (do (handle-send address (rest action))
        true)

    :receive
    false))
