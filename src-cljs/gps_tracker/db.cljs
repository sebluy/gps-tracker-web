(ns gps-tracker.db
  (:require [sigsub.core :as sigsub]))

(defonce db (atom {}))

(sigsub/register-default-signal-fn
  (sigsub/get-in-atom-signal-fn db))

(defn query
  ([] (sigsub/query nil))
  ([path] (sigsub/query path)))

(defn transition [transition-fn]
  (swap! db #(transition-fn %)))

