(ns gps-tracker.core
  (:require [quiescent.core :as q]
            [quiescent.dom :as d]
            [ajax.core :as ajax]
            [cljs.pprint :as pp]
            [gps-tracker.remote :as remote]
            [gps-tracker.pages.core :as p]
            [gps-tracker.history :as history]))

(def state (atom nil))

(declare handle)

(defn handle-waypoint-paths [action paths]
  (case (first action)
    :create
    (conj paths (second action))

    paths))

(defn intercept-page-actions [action state]
  (cond
    (= (take 2 action) '(:new-waypoint-path :create))
    (let [path (get-in state [:page :path])]
      (->> state
           (handle '(:page :navigate {:id :waypoint-paths}))
           (handle `(:waypoint-paths :create ~path))))

    :else
    state))

(defn handle [action state]
  (case (first action)
    :init
    {:page {:id :waypoint-paths}
     :waypoint-paths []}

    :page
    (-> state
        (update :page (partial p/handle (rest action)))
        (->> (intercept-page-actions (rest action))))

    :waypoint-paths
    (update state :waypoint-paths (partial handle-waypoint-paths (rest action)))

    state))

(declare render)

(defn address [action]
  (pp/pprint action)
  (swap! state (partial handle action))
  (pp/pprint @state)
  (render @state))

(defn render [state]
  (q/render (p/view address state)
            (.getElementById js/document "app")))

(defn init! []
  (address '(:init)))

;; OM Next --------------

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; (defn initial-state []                                                              ;;
;;   {:page (history/get-page)                                                         ;;
;;    :remotes 0})                                                                     ;;
;;                                                                                     ;;
;; (defmulti read om/dispatch)                                                         ;;
;;                                                                                     ;;
;; (defmethod read :waypoint-paths                                                     ;;
;;   [{:keys [state ast]} key params]                                                  ;;
;;   (if-let [paths (@state :waypoint-paths)]                                          ;;
;;     {:value paths}                                                                  ;;
;;     {:value :pending                                                                ;;
;;      :remote true}))                                                                ;;
;;                                                                                     ;;
;; (defmethod read :default                                                            ;;
;;   [{:keys [state]} key params]                                                      ;;
;;   (if-let [value (@state key)]                                                      ;;
;;     {:value value}                                                                  ;;
;;     {:value :not-found}))                                                           ;;
;;                                                                                     ;;
;; (defmulti mutate om/dispatch)                                                       ;;
;;                                                                                     ;;
;; (defmethod mutate 'set-page                                                         ;;
;;   [{:keys [state]} key {:keys [page]}]                                              ;;
;;   {:value {:keys [:page]}                                                           ;;
;;    :action (fn []                                                                   ;;
;;              (history/set-page page)                                                ;;
;;              (swap! state assoc :page page))})                                      ;;
;;                                                                                     ;;
;; (defmethod mutate 'inc-remotes                                                      ;;
;;   [{:keys [state]} key params]                                                      ;;
;;   {:value {:keys [:remotes]}                                                        ;;
;;    :action (fn []                                                                   ;;
;;              (swap! state update :remotes inc))})                                   ;;
;;                                                                                     ;;
;; (defmethod mutate 'add-waypoint-path                                                ;;
;;   [{:keys [state]} key {:keys [path]}]                                              ;;
;;   {:value {:keys [:waypoint-paths]}                                                 ;;
;;    :remote true                                                                     ;;
;;    :action #(swap! state update :waypoint-paths (fn [paths] (into [path] paths)))}) ;;
;;                                                                                     ;;
;; (defn filter-out-path [paths id]                                                    ;;
;;   (filterv                                                                          ;;
;;    (fn [path] (not= (path :id) id))                                                 ;;
;;    paths))                                                                          ;;
;;                                                                                     ;;
;; (defmethod mutate 'delete-waypoint-path                                             ;;
;;   [{:keys [state]} key {:keys [path-id]}]                                           ;;
;;   {:value {:keys [:waypoint-paths]}                                                 ;;
;;    :remote true                                                                     ;;
;;    :action #(swap! state update :waypoint-paths filter-out-path path-id)})          ;;
;;                                                                                     ;;
;; (defmethod mutate :default                                                          ;;
;;   [env key params]                                                                  ;;
;;   {:action #(println "Bad mutation" key params)})                                   ;;
;;                                                                                     ;;
;; (def parser (om/parser {:read read :mutate mutate}))                                ;;
;;                                                                                     ;;
;; (def reconciler                                                                     ;;
;;   (let [state (atom (initial-state))]                                               ;;
;;     (om/reconciler {:state state                                                    ;;
;;                     :send remote/send                                               ;;
;;                     :parser parser})))                                              ;;
;;                                                                                     ;;
;; #_(defn mount-root []                                                               ;;
;;   (om/add-root! reconciler pages/View (.getElementById js/document "app")))         ;;
;;                                                                                     ;;
;; #_(defn init! []                                                                    ;;
;;   (mount-root))                                                                     ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
