(ns gps-watch-web.app
  (:require [gps-watch-web.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
