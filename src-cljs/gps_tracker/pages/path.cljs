(ns gps-tracker.pages.path
  (:require [gps-tracker.map :as map]
            [sigsub.core :as sigsub :include-macros true]
            [reagent.core :as reagent]))

(defn map-slot []
  (sigsub/with-reagent-subs
    [path-id [:page :path-id]
     path [:path @path-id]]
    (fn []
      (when (not= @path :pending)
        [map/viewing-map @path]))))

(defn show-point [index point]
  [:tr
   [:td (point :latitude)]
   [:td (point :longitude)]
   [:td (point :speed)]
   [:td (point :accuracy)]])

(defn path-table []
  (sigsub/with-reagent-subs
    [path-id [:page :path-id]
     path [:path @path-id]]
    (fn []
      (if-not (= @path :pending)
        [:table.table
         [:thead
          [:td "Latitude"]
          [:td "Longitude"]
          [:td "Speed (m/s)"]
          [:td "Accuracy (m)"]]
         [:tbody
          (doall
            (map-indexed
              (fn [index point]
                ^{:key index}
                [show-point index point])
              @path))]]))))

(defn page []
  (sigsub/with-reagent-subs
    [path-id [:page :path-id]]
    (fn []
      (when @path-id
        [:div
         [:div.col-md-6 [path-table]]
         [:div.col-md-6 [map-slot]]]))))

