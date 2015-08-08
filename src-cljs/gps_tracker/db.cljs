(ns gps-tracker.db
  (:require [sigsub.core :as sigsub]))

(defonce db (atom {}))

(sigsub/register-default-signal-skeleton
  (sigsub/get-in-atom-run-fn db))

(defn query
  ([] (sigsub/query nil))
  ([path] (sigsub/query path)))

(defn transition [transition-fn]
  (swap! db #(transition-fn %)))


