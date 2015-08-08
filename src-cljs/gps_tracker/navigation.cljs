(ns gps-tracker.navigation
  (:require [gps-tracker.routing :as routing]
            [gps-tracker.history :as history]
            [goog.events :as events]
            [clojure.string :as string]
            [gps-tracker.db :as db])
  (:import goog.history.EventType))

(defn navigate [page]
  (db/transition (fn [db] (assoc db :page page))))

(defn- initialize-route []
  (let [history-token (history/get-token)]
    (if (string/blank? history-token)
      (let [page {:handler :activities}]
        (history/replace-token page)
        (navigate page))
      (navigate (routing/route->page history-token)))))

(defn hook-browser []
  (doto history/history
    (events/listen
      EventType.NAVIGATE
      (fn [event]
        (navigate (routing/route->page (.-token event)))
        (.preventDefault event)))
    (.setEnabled true))
  (initialize-route))
