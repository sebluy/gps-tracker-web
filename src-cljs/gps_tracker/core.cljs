(ns gps-tracker.core
  (:require [reagent.core :as reagent]
            [sigsub.core :as sigsub :include-macros true]
            [gps-tracker.db :as db]
            [ajax.core :as ajax]
    #_[gps-tracker.map :as map]))

#_(defn map-div []
    [:div#map-canvas.col-md-12])

#_(defn google-map []
    (reagent/create-class
      {:reagent-render      map-div
       :component-did-mount map/load}))

#_(defn map-page []
    [google-map])

(defn receive-path [response]
  (db/transition (fn [db] (assoc db :path (first response)))))

(defn get-path [id]
  (ajax/POST "/api"
             {:params          [[:get-path id]]
              :handler         receive-path
              :response-format :edn
              :format          :edn}))


(defn show-point [point]
  [:tr
   [:td (point :latitude)]
   [:td (point :longitude)]])

(defn path-table []
  (sigsub/with-reagent-subs
    [path [:path]]
    (fn []
      [:table.table
       [:thead [:td "Latitude"] [:td "Longitude"]]
       [:tbody
        (doall
          (map-indexed
            (fn [index point]
              ^{:key index}
              [show-point point])
            @path))]])))

(defn page []
  [:div.container
   [:div.row
    [:div.span12
     [:div.page-header
      [:h1 "Paths"]]
     [path-table]]]])

(defn mount-components []
  (reagent/render-component [page] (.getElementById js/document "app")))

(defn init! []
  (mount-components))

