(ns gps-tracker.core
  (:require [reagent.core :as reagent]
            [gps-tracker.subs]
            [gps-tracker.db :as db]
            [gps-tracker.pages.core :as pages]
            [gps-tracker.navigation :as navigation]))

(defn mount-components []
  (reagent/render-component [pages/view] (.getElementById js/document "app")))

(def initial-state {:page {:id :waypoint-paths}
                    :remote {:action-queue []}})

(defn init! []
  (db/transition (fn [_] initial-state))
  (navigation/hook-browser)
  (mount-components))
