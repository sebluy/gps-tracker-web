(ns gps-tracker.pages.tracking-paths.index
  (:require [schema.core :as s]
            [gps-tracker.schema-helpers :as sh]
            [gps-tracker-common.schema :as cs]
            [gps-tracker.waypoint-paths :as wp]
            [gps-tracker.address :as a]
            [gps-tracker.routing :as r]
            [gps-tracker.util :as util]))

(s/defschema Page
  {:id (s/eq :tracking-paths-index)})

(s/defschema Action
  (s/either
   (sh/list :show (sh/singleton cs/PathID))
   (s/eq '(:refresh))))

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
    [:div.jumbotron [:h1.text-center "No tracking paths"]]

    [:table.table
     [:thead [:tr [:td "Created"] [:td "Count"] [:td "Distance"]]]
     [:tbody
      (map (partial show address) (reverse (sort-by :id paths)))]]))

(defn refresh-button [address]
  [:button.btn.btn-primary
   {:onClick #(address :refresh)}
   "Refresh"])

(defn view [address paths]
  [:div.col-md-8.col-md-offset-2
   [:div.page-header
    [:h1 "Tracking Paths"
     [:p.pull-right.btn-toolbar
      (refresh-button address)]]]
   (table address paths)])
