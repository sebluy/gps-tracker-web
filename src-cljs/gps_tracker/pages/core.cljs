(ns gps-tracker.pages.core
  (:require [schema.core :as s]
            [gps-tracker.schema-helpers :as sh]
            [gps-tracker-common.schema :as cs]
            [gps-tracker.address :as a]
            [gps-tracker.waypoint-paths :as wp]
            [gps-tracker.history :as h]
            [gps-tracker.pages.waypoint-paths.index :as wp-index]
            [gps-tracker.pages.waypoint-paths.new :as wp-new]
            [gps-tracker.pages.waypoint-paths.show :as wp-show]
            [gps-tracker.pages.tracking-paths.index :as tp-index]))

(s/defschema NotFoundPage
  {:id (s/eq :not-found)})

(s/defschema TrackingPathShowPage {:id (s/eq :tracking-paths-show)
                                   :path-id cs/Date})

(s/defschema Page (s/either wp-index/Page
                            wp-new/Page
                            wp-show/Page
                            tp-index/Page
                            TrackingPathShowPage
                            NotFoundPage))

(s/defschema PageID (s/either wp-index/Page
                              wp-new/PageID
                              wp-show/Page
                              tp-index/Page
                              TrackingPathShowPage
                              NotFoundPage))

(s/defschema Action
  (s/either
   (sh/list :navigate (sh/singleton PageID))
   (sh/list :tracking-paths-index tp-index/Action)
   (sh/list :waypoint-paths-index wp-index/Action)
   (sh/list :waypoint-paths-new wp-new/Action)
   (sh/list :waypoint-paths-show wp-show/Action)))

(s/defn init :- Page [page :- PageID]
  (case (page :id)
    :waypoint-paths-new
    (assoc page :path {:id (js/Date.) :points []})

    page))

(declare handle)

(s/defn eavesdrop :- Page
  [action :- Action page :- Page]
  (cond
    (= (take 2 action) '(:waypoint-paths-index :show))
    (let [path-id (last action)]
      (handle `(:navigate {:id :waypoint-paths-show
                           :path-id ~path-id})
              page))

    (= (take 2 action) '(:tracking-paths-index :show))
    (let [path-id (last action)]
      (handle `(:navigate {:id :tracking-paths-show
                           :path-id ~path-id})
              page))


    (= action '(:waypoint-paths-index :new))
    (handle `(:navigate {:id :waypoint-paths-new}) page)

    :else
    page))

(s/defn delegate :- Page [action :- Action page :- Page]
  (case (first action)

    :waypoint-paths-new
    (wp-new/handle (rest action) page)

    page))

(s/defn local :- Page [action :- Action page :- Page]
  (case (first action)
    :navigate
    (let [new-page (last action)]
      (do (h/set-page new-page)
          (init new-page)))

    page))

(s/defn handle :- Page [action :- Action page :- Page]
  (->> page
       (delegate action)
       (local action)
       (eavesdrop action)))

;;;; VIEW

(defn not-found-page []
  [:div.col-md-8.col-md-offset-2
   [:div.jumbotron [:h1.text-center "Page not found"]]])

(defn view [address {:keys [waypoint-paths tracking-paths page]}]
  (case (page :id)
    :waypoint-paths-index
    (wp-index/view (a/forward address (a/tag :waypoint-paths-index))
                   waypoint-paths)

    :waypoint-paths-show
    (let [path (wp/find waypoint-paths (page :path-id))]
      (wp-show/view (a/forward address (a/tag :waypoint-paths-show)) path))

    :waypoint-paths-new
    (wp-new/view (a/forward address (a/tag :waypoint-paths-new)) page)

    :tracking-paths-index
    (tp-index/view (a/forward address (a/tag :tracking-paths-index))
                   tracking-paths)

    :tracking-paths-show
    (let [path (wp/find tracking-paths (page :path-id))]
      (wp-show/view (a/forward address (a/tag :tracking-paths-show)) path))

    (not-found-page)))
