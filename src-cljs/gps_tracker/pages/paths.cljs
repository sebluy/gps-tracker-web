(ns gps-tracker.pages.paths
  (:require [gps-tracker.handlers :as handlers]
            [sigsub.core :as sigsub :include-macros true]
            [gps-tracker.routing :as routing]))

(defn show-path-id [id]
  ^{:key id}
  [:li
   [:p id]
   [:a.btn.btn-primary
    {:href (routing/page->href {:handler :path :route-params {:id id}})}
    "Show"]
   [:input.btn.btn-danger
    {:value "Delete"
     :type "button"
     :on-click #(handlers/delete-path id)}]])

(defn path-id-list []
  (sigsub/with-reagent-subs
    [ids [:path-ids]]
    (fn []
      (if-not (= @ids :pending)
        [:ul
         (map show-path-id @ids)]))))

(defn page []
  [:div
   [:div.page-header
    [:h1 "Paths"]]
   [path-id-list]])
