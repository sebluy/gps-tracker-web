(ns gps-tracker.db
  (:require [sigsub.core :as sigsub]
            [cljs.pprint :as pp]
            [gps-tracker.schema :as schema]))

(defonce db (atom {} :validator schema/validator))

(sigsub/register-default-signal-skeleton
  (sigsub/get-in-atom-run-fn db))

(defn query
  ([] (sigsub/query nil))
  ([path] (sigsub/query path)))

(defn transition [transition-fn]
  (try
    (swap! db transition-fn)
    (catch js/Object e
      (pp/pprint (:value (ex-data e)))
      (js/alert "Something went wrong..."))))
