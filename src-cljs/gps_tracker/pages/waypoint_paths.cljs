(ns gps-tracker.pages.waypoint-paths
  (:require [gps-tracker.handlers :as handlers]
            [sigsub.core :as sigsub :include-macros true]
            [gps-tracker.routing :as routing]))

(defn show [id]
  ^{:key id}
  [:li
   [:a.btn.btn-primary
    {:href (routing/page->href {:handler :show-waypoint-path :route-params {:id id}})}
    id]])

(defn waypoint-list []
  (sigsub/with-reagent-subs
    [ids [:waypoint-path-ids]]
    (fn []
      (if-not (= @ids :pending)
        [:ul
         (map show @ids)]))))

(defn new-button []
  [:input.btn.btn-primary
   {:type     "button"
    :value    "New Waypoint"
    :on-click handlers/create-waypoint-path}])

(defn page []
  [:div
   [:div.page-header
    [:h1 "Waypoint Paths"
     [:p.pull-right.btn-toolbar
      [new-button]]]]
   [waypoint-list]])
