(ns gps-tracker.remote
  (:require [cljs.core.async :as async]
            [ajax.core :as ajax]
            [gps-tracker.db :as db]
            [gps-tracker.util :as util])
  (:require-macros [cljs.core.async.macros :as async]))


;Todo: merge "tracking" and "waypoint" paths across platform
; replace this "machine" with a simpler mechanism

(defn post-actions [actions response-chan]
  (ajax/POST
    "/api"
    {:params          actions
     :handler         #(async/put! response-chan %)
     :format          :edn
     :response-format :edn}))

; main purpose is to enforce strict in order processing of remote api requests.
; if a request is in flight, any further requests will be queued and sent in a
; batch when the initial request returns. the server is guaranteed to
; recieve api requests in the order they are put on the chan. the same is true
; for the ordering of the callbacks on request return.
(defn run-remote-action-machine [action-callback-chan]
  (let [response-chan (async/chan)]
    (async/go-loop
      [pending [] queued []]
      (async/alt!
        action-callback-chan
        ([action-callback]
          (if (seq pending)
            (recur pending (conj queued action-callback))
            (do (post-actions [(first action-callback)] response-chan)
                (recur [action-callback] []))))
        response-chan
        ([responses]
          (doall (map (fn [[_ callback] response] (callback response))
                      pending
                      responses))
          (if (seq queued)
            (do (post-actions (into [] (map first queued)) response-chan)
                (recur queued []))
            (recur [] [])))))))

(def action-callback-chan (async/chan))
(run-remote-action-machine action-callback-chan)

(defn post-action
  ([action] (post-action action identity))
  ([action callback] (async/put! action-callback-chan [action callback])))

(defn get-waypoint-paths []
  (db/transition
    (fn [db] (assoc-in db [:remote :waypoint-paths] :pending)))
  (post-action
   {:action :get-paths
    :path-type :waypoint}
    (fn [paths]
      (db/transition
        (fn [db] (assoc-in db [:remote :waypoint-paths] paths))))))

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
