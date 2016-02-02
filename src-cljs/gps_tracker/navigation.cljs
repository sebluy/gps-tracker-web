(ns gps-tracker.navigation
  (:require [gps-tracker.routing :as routing]
            [gps-tracker.history :as history]
            [goog.events :as events]
            [clojure.string :as string]
            [gps-tracker.db :as db])
  (:import [goog.history EventType]))

(def default-page {:id :waypoint-paths})

(defmulti seed-page
  "Seeds page with data on navigate."
  (fn [page _] (page :id)))

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
      (history/replace-token default-page)
      (navigate (routing/route->page history-token)))))

(defn hook-browser
  "Navigates to the page given by the url bar if available,
  and attaches the event handler to watch for url bar changes."
  []
  (initialize-route)
  (doto history/history
    (events/listen
      EventType.NAVIGATE
      (fn [event]
        (navigate (routing/route->page (.-token event)))
        (.preventDefault event)))
    (.setEnabled true)))
