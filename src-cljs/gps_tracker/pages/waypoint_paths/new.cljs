(ns gps-tracker.pages.waypoint-paths.new
  (:require [schema.core :as s]
            [gps-tracker-common.schema :as cs]
            [gps-tracker.schema-helpers :as sh]
            [gps-tracker.map :as map]
            [gps-tracker.waypoint-paths :as wp]
            [gps-tracker.util :as util]))

(s/defschema PageID
  {:id (s/eq :waypoint-paths-new)})

(s/defschema Page
  {:id (s/eq :waypoint-paths-new)
   :path cs/WaypointPath})

(s/defschema Action
  (s/either
   (sh/list :create (sh/singleton cs/WaypointPath))
   (sh/list :add-point (sh/singleton cs/Waypoint))))

(s/defn handle :- Page [action :- Action page :- Page]
  (case (first action)
    :add-point
    (let [point (second action)]
      (update-in page [:path :points] conj point))

    page))

;;;; VIEW

(defn upload-button [on-click]
  [:input.btn.btn-primary
   {:type "button"
    :value "Create"
    :on-click on-click}])

(defn segment-distance-list [path]
  (->> path
       (wp/segment-distances)
       (map util/distance->str)
       (map-indexed (fn [index distance] [:li {:key index} distance]))
       (into [:ul.list-unstyled])))

(defn view [address {:keys [path]}]
  [:div
   [:div.page-header
    [:h1 "New Waypoint Path"
     [:p.pull-right.btn-toolbar
      (upload-button #(address `(:create ~path)))]]]
   [:div.col-md-2
    [:h3 (util/distance->str (wp/distance path))]
    [:h3 (str (count (path :points)) " points")]
    (segment-distance-list path)]
   [:div.col-md-10 (map/WaypointCreationMap address)]])
