(ns gps-tracker.remote
  (:require [ajax.core :as ajax]
            [gps-tracker.address :as a]
            [schema.core :as s]
            [gps-tracker-common.schema :as cs]
            [gps-tracker.schema-helpers :as sh]))

;;;; HELPERS

(defn post-actions
  [actions on-success on-error]
  (ajax/POST
   "/api"
   {:params          actions
    :handler         on-success
    :error-handler   on-error
    :format          :edn
    :response-format :edn}))

(defn get-tracking-paths [on-success on-error]
  (post-actions [{:action :get-paths
                  :path-type :tracking}]
                (comp on-success first)
                on-error))

(defn get-waypoint-paths [on-success on-error]
  (post-actions [{:action :get-paths
                  :path-type :waypoint}]
                (comp on-success first)
                on-error))

(defn create-waypoint-path [path on-success on-error]
  (post-actions [{:action :add-path
                  :path-type :waypoint
                  :path path}]
                (comp on-success first)
                on-error))

(defn delete-waypoint-path [path-id on-success on-error]
  (post-actions [{:action :delete-path
                  :path-type :waypoint
                  :path-id path-id}]
                (comp on-success first)
                on-error))

;;;; STATE

(s/defschema Remote {:next-id s/Int
                     :pending (sh/set s/Int)})

(defn init []
  {:next-id 0
   :pending #{}})

;;;; ACTIONS

(s/defschema SendAction
  (s/either
   (s/eq '(:get-waypoint-paths))
   (s/eq '(:get-tracking-paths))
   (sh/list :delete-waypoint-path (sh/singleton cs/PathID))
   (sh/list :create-waypoint-path (sh/singleton cs/WaypointPath))))

(s/defschema SuccessReceiveAction
  (sh/list
   :success
   (s/either
    (sh/list :get-waypoint-paths (sh/singleton [cs/WaypointPath]))
    (sh/list :get-tracking-paths (sh/singleton [cs/TrackingPath]))
    (sh/list :delete-waypoint-path (sh/singleton cs/PathID))
    (sh/list :create-waypoint-path (sh/singleton cs/WaypointPath)))))

(s/defschema ErrorReceiveAction
  (sh/list
   :error
   SendAction))

(s/defschema SendActionWithID
  (sh/list s/Int SendAction))

(s/defschema ReceiveActionWithID
  (sh/list s/Int (s/either SuccessReceiveAction ErrorReceiveAction)))

(s/defschema Action
  (s/either
   (sh/list :send SendActionWithID)
   (sh/list :receive ReceiveActionWithID)))

(s/defn handle-send [address action :- SendActionWithID]
  (let [id (first action)
        type (second action)
        address2 (a/forward address (a/tag `(:receive ~id)))]
    (case type
      :get-waypoint-paths
      (get-waypoint-paths
       (fn [paths]
         (address2 `(:success :get-waypoint-paths ~paths)))
       (fn [_]
         (address2 `(:error :get-waypoint-paths))))

      :get-tracking-paths
      (get-tracking-paths
       (fn [paths]
         (address2 `(:success :get-tracking-paths ~paths)))
       (fn [_]
         (address2 `(:error :get-tracking-paths))))

      :delete-waypoint-path
      (let [path-id (last action)]
        (delete-waypoint-path
         path-id
         (fn [_]
           (address2 `(:success :delete-waypoint-path ~path-id)))
         (fn [_]
           (address2 `(:error :delete-waypoint-path ~path-id)))))

      :create-waypoint-path
      (let [path (last action)]
        (create-waypoint-path
         path
         (fn [_]
           (address2 `(:success :create-waypoint-path ~path)))
         (fn [_]
           (address2 `(:error :create-waypoint-path ~path)))))

      nil)))

(s/defn handle :- Remote [address action :- Action remote :- Remote]
  (case (first action)

    :send
    (do (handle-send address (rest action))
        (let [{:keys [next-id pending]} remote]
          {:next-id (inc next-id)
           :pending (conj pending next-id)}))

    :receive
    (let [id (second action)]
      (update remote :pending disj id))))
