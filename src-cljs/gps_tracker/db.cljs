(ns gps-tracker.db
  (:require [sigsub.core :as sigsub]
            [cljs.pprint :as pp]
            [gps-tracker.schema :as schema]))

(defonce db (atom {} :validator schema/validator))

(sigsub/register-default-signal-skeleton
  (sigsub/get-in-atom-run-fn db))

(defn base-query
  ([] @db)
  ([path] (get-in @db path)))

(defn query
  ([] (sigsub/query nil))
  ([path] (sigsub/query path)))

(defn transition [transition-fn]
  (try
    (swap! db transition-fn)
    (catch js/Object e
      (.log js/console e)
      (pp/pprint (:value (ex-data e)))
      (js/alert "Something went wrong..."))))
