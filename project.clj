(defproject gps-watch-web "0.1.0-SNAPSHOT"

  :description "FIXME: write description"
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
  :uberjar-name "gps-watch-web.jar"
  :repl-options {:init-ns gps-watch-web.handler}
  :jvm-opts ["-server"]

  :env {:repl-port 7001}

  :main gps-watch-web.core

  :plugins [[lein-ring "0.9.1"]
            [lein-environ "1.0.0"]
            [lein-ancient "0.6.5"]
            [lein-cljsbuild "1.0.4"]
            [ragtime/ragtime.lein "0.3.8"]]

  :ragtime
    {:migrations ragtime.sql.files/migrations
     :database
     "jdbc:postgresql://localhost/gpswatch?user=admin&password=admin"}

  :cljsbuild
  {:builds {:app {:source-paths ["src-cljs"]
                  :compiler {:output-to "resources/public/js/app.js"
                             :output-dir "resources/public/js/out"
                             :externs ["react/externs/react.js"]
                             :optimizations :none
                             :pretty-print true}}}}

  :ring {:handler gps-watch-web.handler/app
         :init    gps-watch-web.handler/init
         :destroy gps-watch-web.handler/destroy
         :uberwar-name "gps-watch-web.war"}

  :clean-targets ^{:protect false} ["resources/public/js"]

  :profiles
  {:uberjar {:omit-source true
             :env {:production true}
             :hooks [leiningen.cljsbuild]
             :cljsbuild
             {:jar true
              :builds
              {:app
               {:source-paths ["env/prod/cljs"]
                :compiler {:optimizations :advanced :pretty-print false}}}} 
             
             :aot :all}
   :dev {:dependencies [[ring-mock "0.1.5"]
                        [ring/ring-devel "1.3.2"]
                        [pjstadig/humane-test-output "0.7.0"]
                        [leiningen "2.5.1"]
                        [figwheel "0.2.6"]
                        [weasel "0.6.0"]
                        [com.cemerick/piggieback "0.2.1"]
                        [org.clojure/tools.nrepl "0.2.10"]]
         :source-paths ["env/dev/clj"]

         :plugins [[lein-figwheel "0.2.5"]]

         :cljsbuild
         {:builds
          {:app
           {:source-paths ["env/dev/cljs"] :compiler {:source-map true}}}}

         :figwheel
         {:http-server-root "public"
          :nrepl-port 7888
          :server-port 3449
          :css-dirs ["resources/public/css"]
          :ring-handler gps-watch-web.handler/app}

         :repl-options {:init-ns gps-watch-web.repl}
         :injections [(require 'pjstadig.humane-test-output)
                      (pjstadig.humane-test-output/activate!)]
         :env {:dev true}}})
