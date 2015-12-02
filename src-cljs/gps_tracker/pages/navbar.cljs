(ns gps-tracker.pages.navbar
  (:require [gps-tracker.routing :as routing]))

(defn navbar []
  [:div.navbar.navbar-inverse.navbar-fixed-top
   [:div.container
    [:div.navbar-header
     [:a.navbar-brand "GPS Tracker"]]
    [:ul.nav.navbar-nav
     [:li
      [:a
       {:href (routing/page->href {:id :waypoint-paths})}
       "Waypoint Paths"]]]]])
