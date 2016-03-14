(ns gps-tracker.pages.navbar
  (:require [gps-tracker.routing :as r]
            [sablono.core :as s]
            [quiescent.core :as q]
            cljsjs.spin))

(def spinner-options #js {:color "#FFF"
                          :position "absolute"
                          :scale 0.80
                          :top "50%"
                          :left "90%"})

(defn spin [c options]
  (doto (js/Spinner. options)
    (.spin c)))

(q/defcomponent Spinner
  :on-mount
  (fn [node options]
    (spin node options))
  [address]
  (s/html [:div]))

(defn view [address remote?]
  [:div.navbar.navbar-inverse.navbar-fixed-top
   [:div.container
    [:div.navbar-header
     [:a.navbar-brand "GPS Tracker"]]
    [:ul.nav.navbar-nav
     [:li
      [:a
       (r/attrs
        {:id :waypoint-paths-index}
        (fn [page] (address `(:page :navigate ~page))))
       "Waypoint Paths"]]]
      (when remote?
        (Spinner spinner-options))]])
