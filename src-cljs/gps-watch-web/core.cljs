(ns gps-watch-web.core
  (:require [reagent.core :as reagent :refer [atom]]
            [secretary.core :as secretary]
            [reagent.session :as session]
            [reagent-forms.core :refer [bind-fields]]
            [ajax.core :refer [GET POST]]
            [gps-watch-web.map :as map])
  (:require-macros [secretary.core :refer [defroute]]))

(defn navbar []
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

(defn about-page []
  [:div
   [:div "this is the story of test... work in progress"]
   [:p "penis"]])

(defn home-page []
  (with-meta
    [:div
     [:h2 "Welcome to ClojureScript"]]
    {:component-did-mount #(println %)}))

(defn map-div []
  [:div#map-canvas.col-md-12])

(defn google-map []
  (reagent/create-class
    {:reagent-render map-div
     :component-did-mount map/load}))

(defn map-page []
  [google-map])

(def pages
  {:home #'home-page
   :about #'about-page
   :map #'map-page})

(defn page []
  [(pages (session/get :page))])

(defroute "/" [] (session/put! :page :home))
(defroute "/about" [] (session/put! :page :about))
(defroute "/map" [] (session/put! :page :map))

(defn mount-components []
  (reagent/render-component [navbar] (.getElementById js/document "navbar"))
  (reagent/render-component [page] (.getElementById js/document "app")))

(defn init! []
  (secretary/set-config! :prefix "#")
  (session/put! :page :home)
  (mount-components))

