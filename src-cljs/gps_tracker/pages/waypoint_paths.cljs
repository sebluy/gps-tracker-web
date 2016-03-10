(ns gps-tracker.pages.waypoint-paths
  (:require [sablono.core :as s]
            [gps-tracker.routing :as routing]))

(defn show [{:keys [id]}]
  [:li
   {:key id}
   [:a.btn.btn-primary
    {:href (routing/page->href {:id :waypoint-path :path-id id})}
    (.toLocaleString id)]])

(defn waypoint-path-list [paths]
  (case paths
    :pending
    [:div.jumbotron [:h1.text-center "Pending..."]]

    nil
    [:div.jumbotron [:h1.text-center "No waypoint paths"]]

    [:ul (map show paths)]))

(defn new-button [address state]
  [:a.btn.btn-primary
   {:onClick #(address '(:navigate {:id :new-waypoint-path}))}
   "New Waypoint"])

(defn view [address state]
  (s/html
   [:div
    [:div.page-header
     [:h1 "Waypoint Paths"
      [:p.pull-right.btn-toolbar
       (new-button address)]]]
    (waypoint-path-list (state :waypoint-paths))]))
