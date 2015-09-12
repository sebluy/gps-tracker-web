(ns gps-tracker.remote
  (:require [cljs.core.async :as async]
            [ajax.core :as ajax]
            [gps-tracker.db :as db]
            [gps-tracker.util :as util])
  (:require-macros [cljs.core.async.macros :as async]))

;Todo: merge "tracking" and "waypoint" paths across platform

(defn post-actions [actions response-chan]
  (println "Posting: " actions)
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

(defn get-path-ids []
  (db/transition (fn [db] (assoc-in db [:remote :path-ids] :pending)))
  (post-action
    [:get-path-ids]
    (fn [ids]
      (db/transition (fn [db] (assoc-in db [:remote :path-ids] ids))))))

(defn remove-path-ids []
  (db/transition (fn [db] (util/dissoc-in db [:remote :path-ids]))))

(defn get-waypoint-path-ids []
  (db/transition
    (fn [db] (assoc-in db [:remote :waypoint-path-ids] :pending)))
  (post-action
    [:get-waypoint-path-ids]
    (fn [ids]
      (db/transition
        (fn [db] (assoc-in db [:remote :waypoint-path-ids] ids))))))

(defn remove-waypoint-path-ids []
  (db/transition (fn [db] (util/dissoc-in db [:remote :waypoint-path-ids]))))

(defn get-path [id]
  (db/transition (fn [db] (assoc-in db [:remote :path id] :pending)))
  (post-action
    [:get-path id]
    (fn [path]
      (db/transition (fn [db] (assoc-in db [:remote :path id] path))))))

(defn remove-path [id]
  (db/transition (fn [db] (util/dissoc-in db [:remote :path id]))))

(defn get-waypoint-path [id]
  (db/transition
    (fn [db] (assoc-in db [:remote :waypoint-path id] :pending)))
  (post-action
    [:get-waypoint-path id]
    (fn [path]
      (db/transition
        (fn [db] (assoc-in db [:remote :waypoint-path id] path))))))

(defn remove-waypoint-path [id]
  (db/transition (fn [db] (util/dissoc-in db [:remote :waypoint-path id]))))

(defn upload-waypoint-path [path]
  (post-action [:add-waypoint-path path]))

(defn delete-path [id]
  (db/transition
    (fn [db]
      (-> db
          (update-in [:remote :path-ids] (fn [ids] (remove #(= % id) ids))))))
  (post-action [:delete-path id]))

; Todo: move delete buttons to more appropiate place and fix bug with
; delete -> get
; hazardous bug
(defn delete-waypoint-path [id]
  (post-action [:delete-waypoint-path id]))

