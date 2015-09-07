(ns gps-tracker.pages.path
  (:require [gps-tracker.map :as map]
            [sigsub.core :as sigsub :include-macros true]
            [reagent.core :as reagent]))

(defn map-div []
    [:div#map-canvas])

(defn google-map [path]
  (reagent/create-class
    {:reagent-render      map-div
     :component-did-mount #(map/draw-path path)
     :component-will-unmount map/cleanup}))

(defn google-map-slot []
  (sigsub/with-reagent-subs
    [path-id [:page :path-id]
     path [:path @path-id]]
    (fn []
      (when (not= @path :pending)
        [google-map @path]))))

(defn show-point [index point]
  [:tr
   {:on-click #(map/toggle-marker point)}
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
         [:div.col-md-6 [google-map-slot]]]))))

