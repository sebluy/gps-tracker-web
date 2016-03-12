(ns gps-tracker.pages.waypoint-paths.index
  (:require [gps-tracker.waypoint-paths :as wp]
            [gps-tracker.address :as a]
            [gps-tracker.routing :as r]
            [gps-tracker.util :as util]))

(defn show [address {:keys [id points] :as path}]
  [:tr
   {:key id}
   [:td
    [:a
     (r/attrs
      {:id :waypoint-paths-show
       :path-id id}
      (fn [page] (address `(:navigate ~page))))
     (.toLocaleString id)]]
   [:td (count points)]
   [:td (util/distance->str (wp/distance path))]])

(defn table [address paths]
  (condp = paths
    []
    [:div.jumbotron [:h1.text-center "No waypoint paths"]]

    [:table.table
     [:thead [:tr [:td "Created"] [:td "Count"] [:td "Distance"]]]
     [:tbody
      (map (partial show address) (reverse (sort :id paths)))]]))

(defn new-button [address]
  [:a.btn.btn-primary
   (r/attrs
    {:id :waypoint-paths-new}
    (fn [page] (address `(:navigate ~page))))
   "Create Waypoint Path"])

(defn view [address paths]
  [:div.col-md-8.col-md-offset-2
   [:div.page-header
    [:h1 "Waypoint Paths"
     [:p.pull-right.btn-toolbar
      (new-button address)]]]
   (table address paths)])
