(ns gps-tracker.pages.waypoint-paths.show
  (:require [schema.core :as s]
            [gps-tracker-common.schema :as cs]
            [gps-tracker.schema-helpers :as sh]
            [gps-tracker.map :as map]
            [gps-tracker.util :as util]
            [gps-tracker.waypoint-paths :as wp]))

(s/defschema Action
  (sh/list :delete (sh/singleton cs/PathID)))

(s/defschema Page
  {:id (s/eq :waypoint-paths-show)
   :path-id cs/Date})

;;;; VIEW

(defn delete-button [on-click]
  [:input.btn.btn-danger
   {:value "Delete"
    :type "button"
    :on-click on-click}])

(defn view [address {:keys [points id] :as path}]
  [:div
   [:div.page-header
    [:h1 (.toLocaleString id)
     [:p.pull-right.btn-toolbar
      (delete-button #(address `(:delete ~id)))]]]
   [:h3 (str (count points) " points, " (util/distance->str (wp/distance path)))]
   (map/ViewingMap points)])
