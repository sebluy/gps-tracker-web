(ns gps-tracker.pages.waypoint-paths
  (:require [gps-tracker.handlers :as handlers]
            [sigsub.core :as sigsub :include-macros true]
            [gps-tracker.routing :as routing]))

(defn show [id]
  ^{:key id}
  [:li
   [:p id]
   [:a.btn.btn-primary
    {:href (routing/page->href {:handler :show-waypoint-path :route-params {:id id}})}
    "Show"]
   #_[:input.btn.btn-danger
    {:value "Delete"
     :type "button"
     :on-click #(handlers/delete-waypoint-path id)}]])

(defn waypoint-list []
  (sigsub/with-reagent-subs
    [ids [:waypoint-path-ids]]
    (fn []
      (if-not (= @ids :pending)
        [:ul
         (map show @ids)]))))

(defn new-button []
  [:a.btn.btn-primary
   {:href (routing/page->href {:handler :new-waypoint-path})}
   "New Waypoint"])

(defn page []
  [:div
   [:div.page-header
    [:h1 "Waypoint Paths"
     [:p.pull-right.btn-toolbar
      [new-button]]]]
   [waypoint-list]])
