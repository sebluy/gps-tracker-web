(ns gps-tracker.core
  (:require [reagent.core :as reagent]
            [gps-tracker.subs]
            [gps-tracker.pages.core :as pages]
            [gps-tracker.navigation :as navigation]))

(defn mount-components []
  (reagent/render-component [pages/view] (.getElementById js/document "app")))

(defn init! []
  (navigation/hook-browser)
  (mount-components))
