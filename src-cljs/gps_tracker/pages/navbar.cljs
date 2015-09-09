(ns gps-tracker.pages.navbar
  (:require [gps-tracker.routing :as routing]))

(defn navbar []
  [:div.navbar.navbar-inverse.navbar-fixed-top
   [:div.container
    [:div.navbar-header
     [:a.navbar-brand "GPS Tracker"]]
    [:ul.nav.navbar-nav
     [:li
      [:a {:href (routing/page->href {:handler :paths})} "Paths"]]
     [:li
      [:a
       {:href (routing/page->href {:handler :waypoint-paths})}
       "Waypoint Paths"]]]]])
