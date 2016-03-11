(ns gps-tracker.pages.navbar)

(defn view [address state]
   [:div.navbar.navbar-inverse.navbar-fixed-top
    {:key "navbar"}
    [:div.container
     [:div.navbar-header
      [:a.navbar-brand "GPS Tracker"]]
     [:ul.nav.navbar-nav
      [:li
       [:a
        {:onClick #(address '(:page :navigate {:id :waypoint-paths}))}
        "Waypoint Paths"]]]]])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; (def spinner-options #js {:color "#FFF"                                     ;;
;;                           :position "absolute"                              ;;
;;                           :scale 0.80                                       ;;
;;                           :top "50%"                                        ;;
;;                           :left "90%"})                                     ;;
;;                                                                             ;;
;; (defn spin [c options]                                                      ;;
;;   (doto (js/Spinner. options)                                               ;;
;;     (.spin c)))                                                             ;;
;;                                                                             ;;
;; (om/defui Spinner                                                           ;;
;;   Object                                                                    ;;
;;   (componentDidMount                                                        ;;
;;    [this]                                                                   ;;
;;    (spin (om/get-state this) (om/props this)))                              ;;
;;   (render                                                                   ;;
;;    [this]                                                                   ;;
;;    (sablono/html [:div {:ref (fn [target] (om/set-state! this target))}]))) ;;
;;                                                                             ;;
;; (def spinner (om/factory Spinner))                                          ;;
;;                                                                             ;;
;; (defn navbar [state]                                                        ;;
;;   (sablono/html                                                             ;;
;;    [:div.navbar.navbar-inverse.navbar-fixed-top                             ;;
;;     [:div.container                                                         ;;
;;      [:div.navbar-header                                                    ;;
;;       [:a.navbar-brand "GPS Tracker"]]                                      ;;
;;      [:ul.nav.navbar-nav                                                    ;;
;;       [:li                                                                  ;;
;;        [:a                                                                  ;;
;;         {:href (routing/page->href {:id :waypoint-paths})}                  ;;
;;         "Waypoint Paths"]]]                                                 ;;
;;      (when (not= (state :remotes) 0)                                        ;;
;;        (spinner spinner-options))]]))                                       ;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
