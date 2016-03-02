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
  [{:keys [callback]} key params]
  (callback nil)
  (println "Bad remote read"))

(defmulti mutate om/dispatch)

(defmethod mutate 'add-waypoint-path
  [{:keys [callback]} key {:keys [path]}]
  {:action (fn [] (post-actions [{:action :add-path
                                  :path-type :waypoint
                                  :path path}]
                                #(callback nil)))})

(defmethod mutate 'delete-waypoint-path
  [{:keys [callback]} key {:keys [path-id]}]
  {:action (fn [] (post-actions [{:action :delete-path
                                  :path-type :waypoint
                                  :path-id path-id}]
                                #(callback nil)))})

(defmethod mutate :default
  [{:keys [callback]} key params]
  {:action (fn []
             (callback nil)
             (println "Bad mutation" key params))})

(def parser (om/parser {:read read :mutate mutate}))

(defn send
  [query callback state]
  (om/transact! gps-tracker.core.reconciler `[(~'inc-remotes) :remotes])
  (let [callback2 (fn [result]
                    (callback (merge {:remotes (dec (@state :remotes))}
                                            result)))]
    (parser {:callback callback2} (-> (query :remote) set vec))))
