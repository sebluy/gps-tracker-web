(ns gps-tracker.history
  (:require [gps-tracker.routing :as routing]
            [goog.events :as events])
  (:import goog.History
           [goog.History EventType]))

(defonce history (History.))

(defn without-navigation-events
  "Execute a function without having any history events fired."
  [fn]
  (if-let [listener (first (events/getListeners history EventType.NAVIGATE false))]
    (let [listener-fn (.-listener listener)]
      (do (events/unlisten history EventType.NAVIGATE listener-fn)
          (fn)
          (events/listen history EventType.NAVIGATE listener-fn)))
    (fn)))

(defn replace-page [page]
  (without-navigation-events
   #(.replaceToken history (routing/page->route page))))

(defn set-page [page]
  (without-navigation-events
   #(.setToken history (routing/page->route page))))

(defn get-page []
  (-> history (.getToken) (routing/route->page)))

(defn unhook-browser []
  (events/removeAll history EventType.NAVIGATE))

(defn hook-browser
  "Navigates to the page given by the url bar if available,
  and attaches the event handler to watch for url bar changes."
  [on-navigate initial-page]
  ;; for some reason enabling the history avoids causing an event
  ;; when the page is replaced
  (.setEnabled history true)
  (replace-page initial-page)
  (events/listen history
                 EventType.NAVIGATE
                 (fn [event]
                   (on-navigate (routing/route->page (.-token event)))
                   (.preventDefault event))))
