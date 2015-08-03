(ns gps-tracker.core
  (:require [reagent.core :as reagent]
            #_[ajax.core :refer [GET POST]]
            #_[gps-tracker.map :as map]))

#_(defn navbar []
      [:div.navbar.navbar-inverse.navbar-fixed-top
       [:div.container
        [:div.navbar-header
         [:a.navbar-brand {:href "#/"} "GPS Watch"]]
        [:div.navbar-collapse.collapse
         [:ul.nav.navbar-nav
          [:li {:class (when (= :home (session/get :page)) "active")}
           [:a {:on-click #(secretary/dispatch! "#/")} "Home"]]
          [:li {:class (when (= :about (session/get :page)) "active")}
           [:a {:on-click #(secretary/dispatch! "#/about")} "About"]]
          [:li {:class (when (= :map (session/get :page)) "active")}
           [:a {:on-click #(secretary/dispatch! "#/map")} "Map"]]]]]])

#_(defn about-page []
  [:div
   [:div "this is the story of test... work in progress"]
   [:p "penis"]
   [:p "johnny trueman is gay"]
   [:p "johnny sucks dick"]])

#_(defn home-page []
  [:div
   [:h2 "Welcome to ClojureScript"]])

#_(defn map-div []
  [:div#map-canvas.col-md-12])

#_(defn google-map []
  (reagent/create-class
    {:reagent-render map-div
     :component-did-mount map/load}))

#_(defn map-page []
  [google-map])

#_(def pages
  {:home #'home-page
   :about #'about-page
   :map #'map-page})

#_(defn page []
  [(pages (session/get :page))])

;(defroute "/" [] (session/put! :page :home))
;(defroute "/about" [] (session/put! :page :about))
;(defroute "/map" [] (session/put! :page :map))

(defn page []
  [:div "This page is working"])

(defn mount-components []
  (reagent/render-component [page] (.getElementById js/document "app")))

(defn init! []
  (mount-components))

