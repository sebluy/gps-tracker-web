(ns gps-tracker.pages.waypoint-paths
  (:require [gps-tracker.handlers :as handlers]
            [sablono.core :as sablono]
            [om.next :as om]
            [gps-tracker.routing :as routing]))

(defn show [{:keys [id]}]
  [:li
   [:a.btn.btn-primary
    {:href (routing/page->href {:id :waypoint-path :params {:path-id id}})
     :key id}
    (.toLocaleString id)]])

(defn waypoint-path-list [paths]
  (if-not (= paths :pending)
    [:ul (map show paths)]
    [:div.jumbotron [:h1.text-center "Pending..."]]))

(defn new-button []
  [:input.btn.btn-primary
   {:type     "button"
    :value    "New Waypoint"
    #_:on-click #_handlers/create-waypoint-path}])

(om/defui View
  static om/IQuery
  (query
   [this]
   [:waypoint-paths])
  Object
  (render
   [this]
   (sablono/html
    [:div
     [:div.page-header
      [:h1 "Waypoint Paths"
       [:p.pull-right.btn-toolbar
        (new-button)]]]
     (waypoint-path-list (-> this om/props :waypoint-paths))])))

(def view (om/factory View))
