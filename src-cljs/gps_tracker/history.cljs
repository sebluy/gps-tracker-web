(ns gps-tracker.history
  (:require [gps-tracker.routing :as routing])
  (:import goog.History))

(defonce history (History.))

(defn replace-page [page]
  (.replaceToken history (routing/page->route page)))

(defn get-token []
  (.getToken history))
