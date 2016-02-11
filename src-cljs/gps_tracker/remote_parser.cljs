(ns gps-tracker.remote-parser
  (:require [om.next :as om]
            [ajax.core :as ajax]))


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

(defmulti read om/dispatch)

(defmethod read :waypoint-paths
  [{:keys [callback]} key params]
  (post-actions [{:action :get-paths
                  :path-type :waypoint}]
                (fn [results]
                  (callback {:waypoint-paths (first results)}))))

(defmethod read :default
  [env key params]
  (println "Bad remote read"))

(defmulti mutate om/dispatch)

(defmethod mutate 'add-waypoint-path
  [env key {:keys [path]}]
  {:action #(post-actions [{:action :add-path
                           :path-type :waypoint
                           :path path}]
                          identity)})

(defmethod mutate 'delete-waypoint-path
  [env key {:keys [path-id]}]
  {:action #(post-actions [{:action :delete-path
                            :path-type :waypoint
                            :path-id path-id}]
                          identity)})

(defmethod mutate :default
  [env key params]
  {:action #(println "Bad mutation" key params)})

(def parser (om/parser {:read read :mutate mutate}))

(defn send
  [query callback]
  (parser {:callback callback} (-> (query :remote) set vec)))
