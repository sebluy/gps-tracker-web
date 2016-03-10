(ns gps-tracker.pages.new-waypoint-path
  (:require [gps-tracker.map :as map]
            [sablono.core :as sablono]))

(defn upload-button [on-click]
  [:input.btn.btn-primary
   {:type "button"
    :value "Upload"
    :on-click on-click}])

(defn view [address state]
  (sablono/html
   [:div
    [:div.page-header
     [:h1 "New Waypoint"
      [:p.pull-right.btn-toolbar
       (upload-button identity)]]]
    [:div.col-md-8 "Map goes here" #_(map/waypoint-creation-map)]
    [:div.col-md-2 "Segment list goes here"
     #_[:h1 (str "Count: " (count (path :points)))]
     #_(segment-distance-list path)]]))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; ;; this distance code could probably live somewhere else               ;;
;; (defn point->latlng [point]                                            ;;
;;   (js/google.maps.LatLng. (point :latitude) (point :longitude)))       ;;
;;                                                                        ;;
;; (defn distance-between [[a b]]                                         ;;
;;   (js/google.maps.geometry.spherical.computeDistanceBetween            ;;
;;    (point->latlng a)                                                   ;;
;;    (point->latlng b)))                                                 ;;
;;                                                                        ;;
;; (defn segment-distance-list [{:keys [points]}]                         ;;
;;   (->> points                                                          ;;
;;        (partition 2 1)                                                 ;;
;;        (map distance-between)                                          ;;
;;        (map-indexed (fn [index distance] [:li {:key index} distance])) ;;
;;        (into [:ul])))                                                  ;;
;;                                                                        ;;
;; ;;                                                                        ;;
;; (om/defui View                                                         ;;
;;   Object                                                               ;;
;;   (componentWillMount                                                  ;;
;;    [this]                                                              ;;
;;    (om/set-state! this {:id (js/Date.) :points []}))                   ;;
;;   (render                                                              ;;
;;    [this]                                                              ;;
;;    (let [path (om/get-state this)                                      ;;
;;          add-path-fn ((om/get-computed this) :add-path-fn)]            ;;
;;      (sablono/html                                                     ;;
;;       [:div                                                            ;;
;;        [:div.page-header                                               ;;
;;         [:h1 "New Waypoint"                                            ;;
;;          [:p.pull-right.btn-toolbar                                    ;;
;;           (upload-button #(add-path-fn path))]]]                       ;;
;;        [:div.col-md-8 (map/waypoint-creation-map this)]                ;;
;;        [:div.col-md-2                                                  ;;
;;         [:h1 (str "Count: " (count (path :points)))]                   ;;
;;         (segment-distance-list path)]]))))                             ;;
;;                                                                        ;;
;; (def view (om/factory View))                                           ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
