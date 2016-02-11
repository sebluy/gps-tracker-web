(ns gps-tracker.core
  (:require [om.next :as om]
            [ajax.core :as ajax]
            [gps-tracker.remote-parser :as remote]
            [gps-tracker.pages.core :as pages]
            [gps-tracker.history :as history]))

(def initial-state {:page {:id :waypoint-paths}})

(defmulti read om/dispatch)

(defmethod read :waypoint-paths
  [{:keys [state]} key params]
  (if-let [paths (@state :waypoint-paths)]
    {:value paths}
    {:value :pending
     :remote true}))

(defmethod read :default
  [{:keys [state]} key params]
  (if-let [value (@state key)]
    {:value value}
    {:value :not-found}))

(defmulti mutate om/dispatch)

(defmethod mutate 'set-page
  [{:keys [state]} key {:keys [page]}]
  {:value {:keys [:page]}
   :action (fn []
             (history/replace-page page)
             (swap! state assoc :page page))})

(defmethod mutate 'add-waypoint-path
  [{:keys [state]} key {:keys [path]}]
  {:value {:keys [:waypoint-paths]}
   :remote true
   :action #(swap! state update :waypoint-paths (fn [paths] (into [path] paths)))})

(defn filter-out-path [paths id]
  (filterv
   (fn [path] (not= (path :id) id))
   paths))

(defmethod mutate 'delete-waypoint-path
  [{:keys [state]} key {:keys [path-id]}]
  {:value {:keys [:waypoint-paths]}
   :remote true
   :action #(swap! state update :waypoint-paths filter-out-path path-id)})

(defmethod mutate :default
  [env key params]
  {:action #(println "Bad mutation" key params)})

(defn on-error
  [_]
  (js/alert "Remote error... You may need to refresh the page."))

(defn post-actions
  [actions on-success]
  (ajax/POST
   "/api"
    {:params          actions
     :handler         on-success
     :error-handler   on-error
     :format          :edn
     :response-format :edn}))

(defn remote-waypoint-paths [callback]
  (post-actions [{:action :get-paths
                  :path-type :waypoint}]
                (fn [results]
                  (callback {:waypoint-paths (first results)}))))

(defn remote-add-waypoint-path [params]
  (post-actions [{:action :add-path
                  :path-type :waypoint
                  :path (params :path)}]
                identity))

(defn remote-delete-waypoint-path [params]
  (post-actions [{:action :delete-path
                  :path-type :waypoint
                  :path-id (params :path-id)}]
                identity))

(def parser (om/parser {:read read :mutate mutate}))

(def reconciler (om/reconciler {:state initial-state
                                :send remote/send
                                :parser parser}))

(defn mount-root []
  (om/add-root! reconciler pages/View (.getElementById js/document "app")))

;(om/remove-root! reconciler)

(defn init! []
  (mount-root))
