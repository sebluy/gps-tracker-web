(ns gps-tracker.pages.core
  (:require [gps-tracker.address :as a]
            [gps-tracker.waypoint-paths :as wp]
            [gps-tracker.pages.navbar :as navbar]
            [gps-tracker.pages.waypoint-paths :as waypoint-paths]
            [gps-tracker.pages.new-waypoint-path :as new-waypoint-path]
            [gps-tracker.pages.waypoint-path :as waypoint-path]))

(defn init [page]
  (case (page :id)
    :new-waypoint-path
    (assoc page :path {:id (js/Date.) :points []})

    page))

(defn handle [action page]
  (case (first action)
    :navigate
    (init (second action))

    :new-waypoint-path
    (new-waypoint-path/handle (rest action) page)

    page))

(defn current-page [address {:keys [waypoint-paths page]}]
  (case (page :id)
    :waypoint-paths
    (waypoint-paths/view address waypoint-paths)

    :waypoint-path
    (let [path (wp/find waypoint-paths (page :path-id))]
      (waypoint-path/view (a/forward address (a/tag :waypoint-path)) path))

    :new-waypoint-path
    (new-waypoint-path/view (a/forward address (a/tag :new-waypoint-path)) page)

    [:div "Page not found"]))

(defn view [address state]
  [:div
   [:div.container
    [:div.row
     (navbar/view address state)
     [:div
      {:key "current-page"}
      (current-page (a/forward address (a/tag :page)) state)]]]])
