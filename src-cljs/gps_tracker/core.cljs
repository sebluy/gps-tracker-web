(ns gps-tracker.core
  (:require [reagent.core :as reagent]
            [gps-tracker.subs]
            [sigsub.core :as sigsub :include-macros true]
    #_[gps-tracker.map :as map]
            [gps-tracker.db :as db]))

#_(defn map-div []
    [:div#map-canvas.col-md-12])

#_(defn google-map []
    (reagent/create-class
      {:reagent-render      map-div
       :component-did-mount map/load}))

#_(defn map-page []
    [google-map])

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

(defn path-table-slot []
  (sigsub/with-reagent-subs
    [path-id [:page :path-id]]
    (fn []
      (if @path-id
        [path-table]))))

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

(defn page []
  [:div.container
   [:div.row
    [:div.span12
     [:div.page-header
      [:h1 "Paths"]]
     [path-id-list]
     [path-table-slot]]]])

(defn mount-components []
  (reagent/render-component [page] (.getElementById js/document "app")))

(defn init! []
  (mount-components))

