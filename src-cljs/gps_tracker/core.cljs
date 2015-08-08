(ns gps-tracker.core
  (:require [reagent.core :as reagent]
            [gps-tracker.subs]
            [gps-tracker.views :as views]))

(defn mount-components []
  (reagent/render-component [views/page] (.getElementById js/document "app")))

(defn init! []
  (mount-components))

