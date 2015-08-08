(ns gps-tracker.remote
  (:require [cljs.core.async :as async]
            [ajax.core :as ajax]
            [gps-tracker.db :as db]
            [gps-tracker.util :as util])
  (:require-macros [cljs.core.async.macros :as async]))

(defn post-actions [actions]
  (let [response-chan (async/chan)]
    (ajax/POST
      "/api"
      {:params          actions
       :handler         #(async/put! response-chan %)
       :format          :edn
       :response-format :edn})
    response-chan))

(defn delete-path [id]
  (db/transition
    (fn [db]
      (-> db
          (update-in [:remote :path-ids] (fn [ids] (remove #(= % id) ids))))))
  (post-actions [[:delete-path id]]))

(defn get-path-ids []
  (async/go
    (db/transition (fn [db] (assoc-in db [:remote :path-ids] :pending)))
    (let [ids (first (async/<! (post-actions [[:get-path-ids]])))]
      (db/transition (fn [db] (assoc-in db [:remote :path-ids] ids))))))

(defn remove-path-ids []
  (db/transition (fn [db] (util/dissoc-in db [:remote :path-ids]))))

(defn get-path [id]
  (async/go
    (db/transition (fn [db] (assoc-in db [:remote :path id] :pending)))
    (let [path (first (async/<! (post-actions [[:get-path id]])))]
      (db/transition (fn [db] (assoc-in db [:remote :path id] path))))))

(defn remove-path [id]
  (db/transition (fn [db] (util/dissoc-in db [:remote :path id]))))

