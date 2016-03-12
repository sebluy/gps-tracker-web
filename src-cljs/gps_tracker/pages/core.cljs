(ns gps-tracker.pages.core
  (:require [gps-tracker.address :as a]
            [gps-tracker.waypoint-paths :as wp]
            [gps-tracker.history :as h]
            [gps-tracker.pages.navbar :as navbar]
            [gps-tracker.pages.waypoint-paths.index :as wp-index]
            [gps-tracker.pages.waypoint-paths.new :as wp-new]
            [gps-tracker.pages.waypoint-paths.show :as wp-show]))

(defn init [page]
  (case (page :id)
    :waypoint-paths-new
    (assoc page :path {:id (js/Date.) :points []})

    page))

(defn handle [action page]
  (case (first action)
    :navigate
    (let [new-page (second action)]
      (do (h/set-page new-page)
          (init new-page)))

    :waypoint-paths-new
    (wp-new/handle (rest action) page)

    page))

(defn current-page [address {:keys [waypoint-paths page]}]
  (case (page :id)
    :waypoint-paths-index
    (wp-index/view address waypoint-paths)

    :waypoint-paths-show
    (let [path (wp/find waypoint-paths (page :path-id))]
      (wp-show/view (a/forward address (a/tag :waypoint-paths-show)) path))

    :waypoint-paths-new
    (wp-new/view (a/forward address (a/tag :waypoint-paths-new)) page)

    (not-found-page)))

(defn not-found-page []
  [:div.col-md-8.col-md-offset-2
   [:div.jumbotron [:h1.text-center "Page not found"]]])

(defn view [address state]
  [:div
   [:div.container
    [:div.row
     (navbar/view address state)
     [:div
      (current-page (a/forward address (a/tag :page)) state)]]]])
