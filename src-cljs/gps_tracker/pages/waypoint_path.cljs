(ns gps-tracker.pages.waypoint-path
  (:require [om.next :as om]
            [sablono.core :as sablano]
            [gps-tracker.map :as map]))

(defn delete-button [id]
  [:input.btn.btn-danger
   {:value "Delete"
    :type "button"
    #_:on-click #_(handlers/delete-waypoint-path id)}])

#_(defn view []
    (sigsub/with-reagent-subs
      [id [:page :params :path-id]
       path [:waypoint-path @id]]
      (fn []
        [:div
         [:div.page-header
          [:h1 (.toLocaleString @id)
           [:p.pull-right.btn-toolbar
            [delete-button @id]]]]
         (when (not= @path :pending)
           [map/viewing-map (@path :points)])])))

(defn find-path [paths id]
  (if (= paths :pending)
    :pending
    (->> paths
         (filter (fn [path] (= (path :id) id)))
         first)))

(om/defui View
  static om/IQuery
  (query
   [this]
   [:waypoint-paths :page])
  Object
  (render
   [this]
   (let [{:keys [waypoint-paths page]} (om/props this)
         path-id (get-in page [:params :path-id])
         path (find-path waypoint-paths path-id)]
     (sablano/html
      (if (nil? path)
        [:div "Path not found"]
        [:div
         [:div.page-header
          [:h1 (.toLocaleString path-id)
           [:p.pull-right.btn-toolbar
            (delete-button path-id)]]]
         (when (not= path :pending)
           (map/viewing-map (path :points)))])))))

(def view (om/factory View))
