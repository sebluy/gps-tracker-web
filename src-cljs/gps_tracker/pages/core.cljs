(ns gps-tracker.pages.core
  (:require [schema.core :as s]
            [gps-tracker.schema-helpers :as sh]
            [gps-tracker.address :as a]
            [gps-tracker.waypoint-paths :as wp]
            [gps-tracker.history :as h]
            [gps-tracker.pages.waypoint-paths.index :as wp-index]
            [gps-tracker.pages.waypoint-paths.new :as wp-new]
            [gps-tracker.pages.waypoint-paths.show :as wp-show]))

(s/defschema NotFoundPage
  {:id (s/eq :not-found)})

(s/defschema Page (s/either wp-index/Page
                            wp-new/Page
                            wp-show/Page
                            NotFoundPage))

(s/defschema PageID (s/either wp-index/Page
                              wp-new/PageID
                              wp-show/Page
                              NotFoundPage))

(s/defschema Action
  (s/either
   (sh/action :navigate (sh/singleton PageID))
   (sh/action :waypoint-paths-new wp-new/Action)
   (sh/action :waypoint-paths-show wp-show/Action)))

(s/defn init :- Page [page :- PageID]
  (case (page :id)
    :waypoint-paths-new
    (assoc page :path {:id (js/Date.) :points []})

    page))

(s/defn handle :- Page [action :- Action page :- Page]
  (case (first action)
    :navigate
    (let [new-page (second action)]
      (do (h/set-page new-page)
          (init new-page)))

    :waypoint-paths-new
    (wp-new/handle (rest action) page)

    page))

(defn not-found-page []
  [:div.col-md-8.col-md-offset-2
   [:div.jumbotron [:h1.text-center "Page not found"]]])

(defn view [address {:keys [waypoint-paths page]}]
  (case (page :id)
    :waypoint-paths-index
    (wp-index/view address waypoint-paths)

    :waypoint-paths-show
    (let [path (wp/find waypoint-paths (page :path-id))]
      (wp-show/view (a/forward address (a/tag :waypoint-paths-show)) path))

    :waypoint-paths-new
    (wp-new/view (a/forward address (a/tag :waypoint-paths-new)) page)

    (not-found-page)))
