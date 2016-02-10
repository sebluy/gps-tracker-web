(ns gps-tracker.core
  (:require [om.next :as om]
            [ajax.core :as ajax]
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

(defn send
  [query callback]
  (let [{:keys [remote]} query]
    (let [ast (om/query->ast query)
          sub-query (into #{} (-> ast :children first :key second))
          {:keys [children]} (om/query->ast sub-query)]
      (doseq [child children]
        (case (child :dispatch-key)
          :waypoint-paths (remote-waypoint-paths callback)
          add-waypoint-path (remote-add-waypoint-path (child :params))
          delete-waypoint-path (remote-delete-waypoint-path (child :params))
          (println "Bad remote query"))))))

(def parser (om/parser {:read read :mutate mutate}))

(def reconciler (om/reconciler {:state initial-state
                                :send send
                                :parser parser}))

(defn mount-root []
  (om/add-root! reconciler pages/View (.getElementById js/document "app")))

;(om/remove-root! reconciler)

(defn init! []
  (mount-root))
