(ns gps-tracker.pages.waypoint-paths.index
  (:require [schema.core :as s]
            [gps-tracker.schema-helpers :as sh]
            [gps-tracker-common.schema :as cs]
            [gps-tracker.waypoint-paths :as wp]
            [gps-tracker.address :as a]
            [gps-tracker.routing :as r]
            [gps-tracker.util :as util]))

(s/defschema Page
  {:id (s/eq :waypoint-paths-index)})

(s/defschema Action
  (s/either
   (sh/action :show (sh/singleton cs/PathID))
   (s/eq '(:refresh))
   (s/eq '(:new))))

;;;; VIEW

(defn show [address {:keys [id points] :as path}]
  [:tr
   {:key id}
   [:td
    [:button.btn.btn-primary
     {:onClick #(address `(:show ~id))}
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
      (map (partial show address) (reverse (sort-by :id paths)))]]))

(defn new-button [address]
  [:button.btn.btn-primary
   {:onClick #(address :new)}
   "Create Waypoint Path"])

(defn refresh-button [address]
  [:button.btn.btn-primary
   {:onClick #(address :refresh)}
   "Refresh"])

(defn view [address paths]
  [:div.col-md-8.col-md-offset-2
   [:div.page-header
    [:h1 "Waypoint Paths"
     [:p.pull-right.btn-toolbar
      (refresh-button address)
      (new-button address)]]]
   (table address paths)])
