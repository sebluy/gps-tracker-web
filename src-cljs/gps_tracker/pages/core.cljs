(ns gps-tracker.pages.core
  (:require [sablono.core :as s]
            [gps-tracker.address :as a]
            [gps-tracker.history :as history]
            [gps-tracker.pages.navbar :as navbar]
            [gps-tracker.pages.waypoint-paths :as waypoint-paths]
            [gps-tracker.pages.new-waypoint-path :as new-waypoint-path]
            [gps-tracker.pages.waypoint-path :as waypoint-path]))

(defn init [page]
  (case (page :id)
    :new-waypoint-path
    (assoc page :path {:id (js/Date.) :points []})

    page))

(defn handle [action page]
  (case (first action)
    :navigate
    (init (second action))

    :new-waypoint-path
    (new-waypoint-path/handle (rest action) page)

    page))

#_(->> {:path {:points []}}
     (handle '(:new-waypoint-path :add-point {:latitude 1 :longitude 2}))
     (handle '(:new-waypoint-path :add-point {:latitude 4 :longitude 3})))

(defn current-page [address page]
  (case (page :id)
    :waypoint-paths
    (waypoint-paths/view address page)

    ;; :waypoint-path (waypoint-path/view state)

    :new-waypoint-path
    (new-waypoint-path/view (a/forward address (a/tag :new-waypoint-path)) page)

    (s/html [:div "Page not found"])))

(defn view [address state]
  (s/html
   [:div
    [:div.container
     [:div.row
      [:div.span12
       (navbar/view address state)
       (current-page (a/forward address (a/tag :page)) (state :page))]]]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; (defn add-path [c path]                                                            ;;
;;   (om/transact! c `[(~'add-waypoint-path {:path ~path})                            ;;
;;                     (~'set-page {:page {:id :waypoint-paths}})]))                  ;;
;;                                                                                    ;;
;; (defn delete-path [c path-id]                                                      ;;
;;   (om/transact! c `[(~'delete-waypoint-path {:path-id ~path-id})                   ;;
;;                     (~'set-page {:page {:id :waypoint-paths}})]))                  ;;
;;                                                                                    ;;
;; (defn current-view [c props]                                                       ;;
;;   (case (get-in props [:page :id])                                                 ;;
;;     :waypoint-paths (waypoint-paths/view props)                                    ;;
;;     :waypoint-path (waypoint-path/view                                             ;;
;;                     (om/computed props {:delete-path-fn (partial delete-path c)})) ;;
;;     :new-waypoint-path (new-waypoint-path/view                                     ;;
;;                         (om/computed props {:add-path-fn (partial add-path c)}))   ;;
;;     (html/html [:div "Page not found"])))                                          ;;
;;                                                                                    ;;
;; (defn set-page [c page]                                                            ;;
;;   (om/transact! c `[(~'set-page {:page ~page}) :page]))                            ;;
;;                                                                                    ;;
;; (om/defui View                                                                     ;;
;;   static om/IQuery                                                                 ;;
;;   (query                                                                           ;;
;;    [this]                                                                          ;;
;;    (into                                                                           ;;
;;     [:page :remotes]                                                               ;;
;;     (concat                                                                        ;;
;;      (om/get-query waypoint-paths/View)                                            ;;
;;      (om/get-query waypoint-path/View))))                                          ;;
;;   Object                                                                           ;;
;;   (componentDidMount                                                               ;;
;;    [this]                                                                          ;;
;;    (history/hook-browser #(set-page this %) ((om/props this) :page)))              ;;
;;   (componentWillUnmount                                                            ;;
;;    [this]                                                                          ;;
;;    (history/unhook-browser))                                                       ;;
;;   (render                                                                          ;;
;;    [this]                                                                          ;;
;;    (println (om/props this))                                                       ;;
;;    (html/html                                                                      ;;
;;     [:div                                                                          ;;
;;      [:div.container                                                               ;;
;;       [:div.row                                                                    ;;
;;        [:div.span12                                                                ;;
;;         (navbar/navbar (om/props this))                                            ;;
;;         (current-view this (om/props this))]]]])))                                 ;;
;;                                                                                    ;;
;; ;(-> (om/class->any gps-tracker.core/reconciler View) (om/props) :page)            ;;
;; ;(-> gps-tracker.core/reconciler :config :state deref :page)                       ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
