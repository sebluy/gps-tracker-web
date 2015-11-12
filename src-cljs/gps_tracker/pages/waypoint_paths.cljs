(ns gps-tracker.pages.waypoint-paths
  (:require [gps-tracker.handlers :as handlers]
            [sigsub.core :as sigsub :include-macros true]
            [gps-tracker.routing :as routing]))

(defn show [{:keys [id]}]
  ^{:key id}
  [:li
   [:a.btn.btn-primary
    {:href (routing/page->href {:id :waypoint-path :params {:path-id id}})}
    (.toLocaleString id)]])

(defn waypoint-list []
  (sigsub/with-reagent-subs
    [paths [:waypoint-paths]]
    (fn []
      (if-not (= @paths :pending)
        [:ul
         (map show @paths)]
        [:div.jumbotron [:h1.text-center "Pending..."]]))))

(defn new-button []
  [:input.btn.btn-primary
   {:type     "button"
    :value    "New Waypoint"
    :on-click handlers/create-waypoint-path}])

(defn view []
  [:div
   [:div.page-header
    [:h1 "Waypoint Paths"
     [:p.pull-right.btn-toolbar
      [new-button]]]]
   [waypoint-list]])
