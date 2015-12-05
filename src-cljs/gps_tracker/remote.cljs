(ns gps-tracker.remote
  (:require [cljs.core.async :as async]
            [cljs.pprint :as pp]
            [ajax.core :as ajax]
            [gps-tracker.db :as db]
            [gps-tracker.util :as util]))

; Main purpose is to enforce strict in-order processing of remote api requests.
; If a request is in flight, any further requests will be queued and sent one
; at a time.
; The server will recieve api requests in the order they are put on the queued.
; The same is true for the ordering of the callback calls on request return.

(declare on-success)
(declare on-error)

(defn post-actions [actions]
  (println "Posting: ")
  (pp/pprint actions)
  (ajax/POST
    "/api"
    {:params          actions
     :handler         on-success
     :error-handler   on-error
     :format          :edn
     :response-format :edn}))

(defn queue-action [action-handler]
  (if (empty? (db/base-query [:remote :action-queue]))
    (do (post-actions [(action-handler :action)])
        (db/transition
         (fn [db]
           (assoc-in db [:remote :action-queue] #queue [action-handler]))))
    (db/transition
     (fn [db] (update-in db [:remote :action-queue] conj action-handler)))))

(defn post-next []
  (if-let [next (first (db/base-query [:remote :action-queue]))]
    (post-actions [next])))

(defn on-success [response]
  "Passes the response on to the callback associated to the action handler,
   then pops the current action and starts sending the next action
   if there are any."
  (let [current (first (db/base-query [:remote :action-queue]))]
    (db/transition (fn [db] (update-in db [:remote :action-queue] pop)))
    ((current :callback) (first response)))
  (post-next))

(defn on-error [_]
  "Rolls back to before the action was sent.
   Gets the state from before the remote action was queued,
   wipes out any remote action queue leftover from the existing state,
   and sets as the current state."
  (let [current (first (db/base-query [:remote :action-queue]))
        old-state (assoc-in (current :state) [:remote :action-queue] [])]
    (js/alert "Remote error... Rolling back state")
    (db/transition (fn [_] old-state))))

(defn post-action
  "Takes an action to send and a callback to be called on response.
   Captures the current state to be rolled back to on error."
  ([action] (post-action action identity))
  ([action callback]
   (queue-action {:action action :callback callback :state (db/base-query)})))

;; application specific code

(defn get-waypoint-paths []
  (db/transition
    (fn [db] (assoc-in db [:remote :waypoint-paths] :pending)))
  (post-action
   {:action :get-paths
    :path-type :waypoint}
   (fn [paths]
     (db/transition
      (fn [db]
        (assoc-in db [:remote :waypoint-paths] paths))))))

(defn remove-waypoint-paths []
  (db/transition (fn [db] (util/dissoc-in db [:remote :waypoint-paths]))))

(defn filter-out-path [paths id]
  (filterv
   (fn [path] (not= (path :id) id))
   paths))

(defn remove-waypoint-path [id]
  (db/transition
   (fn [db] (update-in db [:remote :waypoint-paths] filter-out-path id))))

(defn upload-waypoint-path [path]
  (post-action {:action :add-path
                :path-type :waypoint
                :path path}))

(defn delete-path [id]
  (post-action [:delete-path id]))

(defn delete-waypoint-path [id]
  (remove-waypoint-path id)
  (post-action {:action :delete-path
                :path-type :waypoint
                :path-id id}))
