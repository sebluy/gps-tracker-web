(ns gps-tracker.pages.waypoint-paths
  (:require [gps-tracker.handlers :as handlers]
            [sablono.core :as sablono]
            [sigsub.core :as sigsub :include-macros true]
            [gps-tracker.routing :as routing]))

(defn show [{:keys [id]}]
  ^{:key id}
  [:li
   [:a.btn.btn-primary
    {:href (routing/page->href {:id :waypoint-path :params {:path-id id}})}
    (.toLocaleString id)]])

(defn waypoint-list []
  (if-not (= :pending :pending)
    [:ul
     (map show @paths)]
    [:div.jumbotron [:h1.text-center "Pending..."]]))

(defn new-button []
  [:input.btn.btn-primary
   {:type     "button"
    :value    "New Waypoint"
    #_:on-click #_handlers/create-waypoint-path}])

(defn view []
  (sablono/html
   [:div
    [:div.page-header
     [:h1 "Waypoint Paths"
      [:p.pull-right.btn-toolbar
       (new-button)]]]
    (waypoint-list)]))
