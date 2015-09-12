(ns gps-tracker.pages.show-waypoint-path
  (:require [gps-tracker.map :as map]
            [sigsub.core :as sigsub :include-macros true]
            [gps-tracker.handlers :as handlers]))

(defn delete-button [id]
  [:input.btn.btn-danger
   {:value "Delete"
    :type "button"
    :on-click #(handlers/delete-waypoint-path id)}])

(defn page []
  (sigsub/with-reagent-subs
    [id [:page :path-id]
     path [:waypoint-path @id]]
    (fn []
      [:div
       [:div.page-header
        [:h1 (str "Path " @id)
         [:p.pull-right.btn-toolbar
          [delete-button @id]]]]
       (when (not= @path :pending)
         [map/viewing-map @path])])))
