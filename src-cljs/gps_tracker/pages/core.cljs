(ns gps-tracker.pages.core
  (:require [om.next :as om]
            [om.dom :as dom]
            [sablono.core :as html]
            [gps-tracker.navigation :as navigation]
            [gps-tracker.pages.navbar :as navbar]
            [gps-tracker.pages.waypoint-paths :as waypoint-paths]
            [gps-tracker.pages.new-waypoint-path :as new-waypoint-path]
            [gps-tracker.pages.waypoint-path :as waypoint-path]
            [sigsub.core :as sigsub :include-macros true]))

;;Todo: reorganize pages more restful like rails

;;;; map from page-ids to views
(def view-map {:waypoint-paths waypoint-paths/view
               :waypoint-path waypoint-path/view
               :new-waypoint-path new-waypoint-path/view})

(defn add-path [c path]
  (om/transact! c `[(~'add-waypoint-path {:path ~path})
                    (~'set-page {:page {:id :waypoint-paths}})]))

(defn delete-path [c path-id]
  (om/transact! c `[(~'delete-waypoint-path {:path-id ~path-id})
                    (~'set-page {:page {:id :waypoint-paths}})]))

(defn current-view [c props]
  (case (get-in props [:page :id])
    :waypoint-paths (waypoint-paths/view props)
    :waypoint-path (waypoint-path/view
                    (om/computed props {:delete-path-fn (partial delete-path c)}))
    :new-waypoint-path (new-waypoint-path/view
                        (om/computed props {:add-path-fn (partial add-path c)}))
    (html/html [:div "Page not found"])))

(om/defui View
  static om/IQuery
  (query
   [this]
   (into
    [:page]
    (concat
     (om/get-query waypoint-paths/View)
     (om/get-query waypoint-path/View))))
  Object
  (navigate
   [this page]
   (om/transact! this `[(~'set-page {:page ~page})]))
  (componentDidMount
   [this]
   (navigation/hook-browser #(.navigate this %)))
  (componentWillUnmount
   [this]
   (navigation/unhook-browser))
  (render
   [this]
   (println ((om/props this) :page))
   (html/html
    [:div
     [:div.container
      [:div.row
       [:div.span12
        (navbar/navbar)
        (current-view this (om/props this))]]]])))

;(-> (om/class->any gps-tracker.core/reconciler View) (.navigate :waypoint-path))
