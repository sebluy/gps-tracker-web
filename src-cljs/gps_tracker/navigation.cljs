(ns gps-tracker.navigation
  (:require [gps-tracker.routing :as routing]
            [gps-tracker.history :as history]
            [goog.events :as events]
            [clojure.string :as string]
            [gps-tracker.db :as db])
  (:import [goog.history EventType]))

(def default-page {:id :waypoint-paths})

;;;; seed page with data on navigate
(defmulti seed-page (fn [page _] (page :id)))

(defmethod seed-page :new-waypoint-path [page _]
  (assoc page :waypoint-path {:id (js/Date.)
                              :points []}))

(defmethod seed-page :default [page _]
  page)

(defn navigate [page]
  (db/transition (fn [db] (assoc db :page (seed-page page db)))))

(defn redirect [page]
  (navigate page)
  (history/replace-token page))

(defn- initialize-route []
  (let [history-token (history/get-token)]
    (if (string/blank? history-token)
      (do
        (history/replace-token default-page)
        (navigate default-page))
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
