(ns gps-tracker.env
  (:require [figwheel-sidecar.repl-api :as repl]
            [gps-tracker.core :as core]))

(defn browser-repl []
  (repl/cljs-repl))

(core/-main)
(browser-repl)
