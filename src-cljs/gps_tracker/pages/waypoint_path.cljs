(ns gps-tracker.pages.waypoint-path
  (:require [om.next :as om]
            [sablono.core :as sablano]
            [gps-tracker.map :as map]))

(defn delete-button [on-click]
  [:input.btn.btn-danger
   {:value "Delete"
    :type "button"
    :on-click on-click}])

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
         {:keys [delete-path-fn]} (om/get-computed this)
         {:keys [path-id]} page
         path (find-path waypoint-paths path-id)]
     (sablano/html
      (if (nil? path)
        [:div "Path not found"]
        [:div
         [:div.page-header
          [:h1 (.toLocaleString path-id)
           [:p.pull-right.btn-toolbar
            (delete-button #(delete-path-fn path-id))]]]
         (when (not= path :pending)
           (map/viewing-map (path :points)))])))))

(def view (om/factory View))
