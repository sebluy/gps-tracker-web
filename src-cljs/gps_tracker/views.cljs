(ns gps-tracker.views
  (:require [gps-tracker.db :as db]
            [gps-tracker.subs]
            [gps-tracker.map :as map]
            [sigsub.core :as sigsub :include-macros true]
            [reagent.core :as reagent]))

(defn map-div []
    [:div#map-canvas.col-md-12])

(defn google-map [path]
  (reagent/create-class
    {:reagent-render      map-div
     :component-did-mount #(map/draw-path path)}))

(defn google-map-slot []
  (sigsub/with-reagent-subs
    [path-id [:page :path-id]
     path [:path @path-id]]
    (fn []
      (when (not= @path :pending)
        [google-map @path]))))

(defn show-point [point]
  [:tr
   [:td (point :latitude)]
   [:td (point :longitude)]])

(defn path-table []
  (sigsub/with-reagent-subs
    [path-id [:page :path-id]
     path [:path @path-id]]
    (fn []
      (if-not (= @path :pending)
        [:table.table
         [:thead [:td "Latitude"] [:td "Longitude"]]
         [:tbody
          (doall
            (map-indexed
              (fn [index point]
                ^{:key index}
                [show-point point])
              @path))]]))))

(defn show-path []
  (sigsub/with-reagent-subs
    [path-id [:page :path-id]]
    (fn []
      (when @path-id
        [:div
         [path-table]
         [google-map-slot]]))))

(defn path-id-list []
  (sigsub/with-reagent-subs
    [ids [:path-ids]]
    (fn []
      (if-not (= @ids :pending)
        [:ul
         (map (fn [id]
                ^{:key id}
                [:li
                 {:on-click
                  #(db/transition (fn [db] (assoc-in db [:page :path-id] id)))}
                 id])
              @ids)]))))

(defn navbar []
  [:div.navbar.navbar-inverse.navbar-fixed-top
   [:div.container
    [:div.navbar-header
     [:a.navbar-brand "GPS Tracker"]]]])

(defn page []
  [:div.container
   [:div.row
    [:div.span12
     [navbar]
     [:div.page-header
      [:h1 "Paths"]]
     [path-id-list]
     [show-path]]]])

