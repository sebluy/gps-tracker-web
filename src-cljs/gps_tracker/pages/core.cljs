(ns gps-tracker.pages.core
  (:require [om.next :as om]
            [om.dom :as dom]
            [sablono.core :as html]
            [gps-tracker.pages.navbar :as navbar]
            [gps-tracker.pages.waypoint-paths :as waypoint-paths]
            [gps-tracker.pages.new-waypoint-path :as new-waypoint-path]
            [gps-tracker.pages.waypoint-path :as waypoint-path]
            [sigsub.core :as sigsub :include-macros true]))

;;Todo: reorganize pages more restful like rails

;;;; map from page-ids to views (rename ui to view)
(def view-map {:waypoint-paths waypoint-paths/view
               :waypoint-path waypoint-path/view
               :new-waypoint-path new-waypoint-path/view})

(defn current-view [props]
  (if-let [view (view-map :waypoint-paths)]
    (view props)
    (html/html [:div "Page not found"])))

(om/defui View
  static om/IQuery
  (query
   [this]
   [{:waypoint-paths (om/get-query waypoint-paths/View)}])
  Object
  (render
   [this]
   (html/html
    [:div
     [:div.container
      [:div.row
       [:div.span12
        (navbar/navbar)
        (current-view (om/props this))]]]])))

;(-> (om/class->any gps-tracker.core/reconciler waypoint-paths/View) om/props keys)
