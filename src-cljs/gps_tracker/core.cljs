(ns gps-tracker.core
  (:require [reagent.core :as reagent]
            [gps-tracker.subs]
            [gps-tracker.navigation :as navigation]
            [gps-tracker.views :as views]))

(defn mount-components []
  (reagent/render-component [views/page] (.getElementById js/document "app")))

(defn init! []
  (navigation/hook-browser)
  (mount-components))

