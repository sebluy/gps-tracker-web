(defproject gps-tracker "0.1.0-SNAPSHOT"

  :description "The web interface for a GPS tracker application"
  :url "http://example.com/FIXME"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-3211"]
                 [org.clojure/tools.nrepl "0.2.8"]
                 [org.clojure/tools.reader "0.9.1"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [cljs-ajax "0.3.11"]
                 [cljsjs/google-maps "3.18-1"]
                 [secretary "1.2.3"]
                 [reagent "0.5.0"]
                 [reagent-forms "0.5.0"]
                 [reagent-utils "0.1.4"]
                 [ring-server "0.4.0"]
                 [selmer "0.8.2"]
                 [com.taoensso/timbre "3.4.0"]
                 [com.taoensso/tower "3.0.2"]
                 [markdown-clj "0.9.65"]
                 [environ "1.0.0"]
                 [im.chit/cronj "1.4.3"]
                 [compojure "1.3.3"]
                 [ring/ring-defaults "0.1.4"]
                 [ring/ring-session-timeout "0.1.0"]
                 [ring-middleware-format "0.5.0"]
                 [noir-exception "0.2.3"]
                 [bouncer "0.3.2"]
                 [prone "0.8.1"]
                 [ragtime "0.3.8"]
                 [yesql "0.5.0-rc1"]
                 [org.postgresql/postgresql "9.3-1102-jdbc41"]]

  :min-lein-version "2.0.0"
  :uberjar-name "gps-tracker.jar"
  :jvm-opts ["-server"]

  :env {:repl-port 7001}

  :main gps-tracker.core

  :plugins [[lein-environ "1.0.0"]
            [lein-ancient "0.6.5"]
            [lein-figwheel "0.3.3"]
            [lein-cljsbuild "1.0.5"]
            [ragtime/ragtime.lein "0.3.8"]]

  :ragtime
  {:migrations ragtime.sql.files/migrations
   :database
               "jdbc:postgresql://localhost/gpstracker?user=admin&password=admin"}

  :clean-targets ^{:protect false} ["resources/public/js"]
  :cljsbuild
  {:builds {:app {:source-paths ["src-cljs"]
                  :compiler     {:output-to     "resources/public/js/app.js"
                                 :main          "gps-tracker.env"
                                 :output-dir    "resources/public/js/out"
                                 :asset-path    "js/out"
                                 :externs       ["react/externs/react.js"]
                                 :optimizations :none
                                 :pretty-print  true}}}}

  :profiles
  {:uberjar {:omit-source  true
             :source-paths ["env/prod/clj"]
             :env          {:production true}
             :hooks        [leiningen.cljsbuild]
             :cljsbuild    {:jar true
                            :builds
                                 {:app
                                  {:source-paths ["env/prod/cljs"]
                                   :compiler     {:optimizations :advanced
                                                  :elide-asserts :true
                                                  :pretty-print  false}}}}

             :aot          :all}
   :dev     {:dependencies [[ring-mock "0.1.5"]
                            [ring/ring-devel "1.3.2"]
                            [pjstadig/humane-test-output "0.7.0"]
                            [figwheel "0.3.3"]]

             :source-paths ["env/dev/clj"]
             :cljsbuild    {:builds
                            {:app
                             {:source-paths ["env/dev/cljs"] :compiler {:source-map true}}}}

             :figwheel     {:http-server-root "public"
                            :server-port      3449
                            :nrepl-port       7888
                            :css-dirs         ["resources/public/css"]
                            :ring-handler     gps-tracker.handler/app}

             :repl-options {:init-ns gps-tracker.dev}
             :injections   [(require 'pjstadig.humane-test-output)
                            (pjstadig.humane-test-output/activate!)]
             :env          {:dev true}}})
