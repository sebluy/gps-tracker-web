(ns gps-tracker.core
  (:require [reagent.core :as reagent]
            [om.next :as om]
            [om.dom :as dom]
            [gps-tracker.subs]
            [gps-tracker.db :as db]
            [gps-tracker.pages.core :as pages]
            [gps-tracker.navigation :as navigation]))

(def initial-state {:page {:id :waypoint-paths}
                    :remote {:action-queue []}})

(defmulti read om/dispatch)

(defmethod read :default
  [env key params]
  {:value :not-found})

(def parser (om/parser {:read read}))

(def reconciler (om/reconciler {:state initial-state
                                :parser parser}))

(om/defui App
  Object
  (render
   [this]
   (dom/div nil "Hello, World!")))

(defn mount-root []
  (om/add-root! reconciler pages/View (.getElementById js/document "app")))

(defn init! []
  (mount-root))
