(ns gps-tracker.views
  (:require [gps-tracker.db :as db]
            [gps-tracker.subs]
            [gps-tracker.map :as map]
            [gps-tracker.handlers :as handlers]
            [sigsub.core :as sigsub :include-macros true]
            [reagent.core :as reagent]
            [gps-tracker.routing :as routing]))

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


(defn show-path []
  (sigsub/with-reagent-subs
    [path-id [:page :path-id]]
    (fn []
      (when @path-id
        [:div
         [:div.col-md-6 [path-table]]
         [:div.col-md-6 [google-map-slot]]]))))

(defn show-path-id [id]
  ^{:key id}
  [:li
   [:p id]
   [:a.btn.btn-primary
    {:href (routing/page->href {:handler :path :route-params {:id id}})}
    "Show"]
   [:input.btn.btn-danger
    {:value "Delete"
     :type "button"
     :on-click #(handlers/delete-path id)}]])

@db/db

(defn path-id-list []
  (sigsub/with-reagent-subs
    [ids [:path-ids]]
    (fn []
      (if-not (= @ids :pending)
        [:ul
         (map show-path-id @ids)]))))

(defn navbar []
  [:div.navbar.navbar-inverse.navbar-fixed-top
   [:div.container
    [:div.navbar-header
     [:a.navbar-brand "GPS Tracker"]]
    [:ul.nav.navbar-nav
     [:li [:a {:href (routing/page->href {:handler :paths})} "Paths"]]]]])

(defn paths-page []
  [:div
   [:div.page-header
    [:h1 "Paths"]]
   [path-id-list]])

(defn path-page []
  [show-path])

(def pages {:paths paths-page
            :path path-page})

(defn current-page []
  (sigsub/with-reagent-subs
    [handler [:page :handler]]
    (fn []
      [(or (pages @handler) :div)])))

(defn page []
  [:div.container
   [:div.row
    [:div.span12
     [navbar]
     [current-page]]]])

