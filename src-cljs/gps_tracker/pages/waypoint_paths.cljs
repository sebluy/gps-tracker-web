(ns gps-tracker.pages.waypoint-paths)

(defn show [address {:keys [id]}]
  [:li
   {:key id}
   [:a.btn.btn-primary
    {:onClick #(address `(:navigate {:id :waypoint-path
                                     :path-id ~id}))}
    (.toLocaleString id)]])

(defn waypoint-path-list [address paths]
  (condp = paths
    :pending
    [:div.jumbotron [:h1.text-center "Pending..."]]

    []
    [:div.jumbotron [:h1.text-center "No waypoint paths"]]

    [:ul (map (partial show address) paths)]))

(defn new-button [address]
  [:a.btn.btn-primary
   {:onClick #(address '(:navigate {:id :new-waypoint-path}))}
   "Create Waypoint Path"])

(defn view [address paths]
  [:div
   [:div.page-header
    [:h1 "Waypoint Paths"
     [:p.pull-right.btn-toolbar
      (new-button address)]]]
   (waypoint-path-list address paths)])
