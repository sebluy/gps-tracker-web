(ns gps-tracker.remote
  (:require [ajax.core :as ajax]))

(defn on-error
  [_]
  (js/alert "Remote error... You may need to refresh the page."))

(defn post-actions
  [actions callback]
  (ajax/POST
   "/api"
    {:params          actions
     :handler         #(callback %)
     :error-handler   on-error
     :format          :edn
     :response-format :edn}))

(defn get-waypoint-paths [callback]
  (post-actions [{:action :get-paths
                  :path-type :waypoint}]
                (comp callback first)))

;; (defmulti read om/dispatch)                                                               ;;
;;                                                                                           ;;
;; (defmethod read :waypoint-paths                                                           ;;
;;   [{:keys [callback]} key params]                                                         ;;
;;   (post-actions [{:action :get-paths                                                      ;;
;;                   :path-type :waypoint}]                                                  ;;
;;                 (fn [results]                                                             ;;
;;                   (callback {:waypoint-paths (first results)}))))                         ;;
;;                                                                                           ;;
;; (defmethod read :default                                                                  ;;
;;   [{:keys [callback]} key params]                                                         ;;
;;   (callback nil)                                                                          ;;
;;   (println "Bad remote read"))                                                            ;;
;;                                                                                           ;;
;; (defmulti mutate om/dispatch)                                                             ;;
;;                                                                                           ;;
;; (defmethod mutate 'add-waypoint-path                                                      ;;
;;   [{:keys [callback]} key {:keys [path]}]                                                 ;;
;;   {:action (fn [] (post-actions [{:action :add-path                                       ;;
;;                                   :path-type :waypoint                                    ;;
;;                                   :path path}]                                            ;;
;;                                 #(callback nil)))})                                       ;;
;;                                                                                           ;;
;; (defmethod mutate 'delete-waypoint-path                                                   ;;
;;   [{:keys [callback]} key {:keys [path-id]}]                                              ;;
;;   {:action (fn [] (post-actions [{:action :delete-path                                    ;;
;;                                   :path-type :waypoint                                    ;;
;;                                   :path-id path-id}]                                      ;;
;;                                 #(callback nil)))})                                       ;;
;;                                                                                           ;;
;; (defmethod mutate :default                                                                ;;
;;   [{:keys [callback]} key params]                                                         ;;
;;   {:action (fn []                                                                         ;;
;;              (callback nil)                                                               ;;
;;              (println "Bad mutation" key params))})                                       ;;
;;                                                                                           ;;
;; (def parser (om/parser {:read read :mutate mutate}))                                      ;;
;;                                                                                           ;;
;; (defn make-callback [om-callback state]                                                   ;;
;;   (fn [status response]                                                                   ;;
;;     (let [new-remotes {:remotes (dec (@state :remotes))}]                                 ;;
;;       (om-callback (if (= status :success)                                                ;;
;;                      (merge new-remotes response)                                         ;;
;;                      ;; add error condition                                               ;;
;;                      new-remotes)))))                                                     ;;
;;                                                                                           ;;
;; (defn send                                                                                ;;
;;   [query om-callback]                                                                     ;;
;;   (let [reconciler gps-tracker.core.reconciler                                            ;;
;;         state (om/app-state reconciler)]                                                  ;;
;;     (om/transact! reconciler `[(~'inc-remotes) :remotes])                                 ;;
;;     (parser {:callback (make-callback om-callback state)} (-> (query :remote) set vec)))) ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
