(ns gps-tracker.pages.waypoint-paths
  (:require [sablono.core :as s]
            [gps-tracker.routing :as routing]))

(defn show [{:keys [id]}]
  [:li
   {:key id}
   [:a.btn.btn-primary
    (.toLocaleString id)]])

(defn waypoint-path-list [paths]
  (condp = paths
    :pending
    [:div.jumbotron [:h1.text-center "Pending..."]]

    []
    [:div.jumbotron [:h1.text-center "No waypoint paths"]]

    [:ul (map show paths)]))

(defn new-button [address]
  [:a.btn.btn-primary
   {:onClick #(address '(:navigate {:id :new-waypoint-path}))}
   "New Waypoint"])

(defn view [address paths]
  (s/html
   [:div
    [:div.page-header
     [:h1 "Waypoint Paths"
      [:p.pull-right.btn-toolbar
       (new-button address)]]]
    (waypoint-path-list paths)]))
