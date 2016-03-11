(defproject gps-tracker "0.1.0-SNAPSHOT"

  :description "The web interface for a GPS tracker application"
  :dependencies [[bidi "1.20.3"]
                 [cljs-ajax "0.3.14"]
                 [cljsjs/google-maps "3.18-1"]
                 [cljsjs/react-dom-server "0.14.3-0" :exclusions [cljsjs/react]]
                 [cljsjs/spin "2.3.2-0"]
                 [com.cemerick/piggieback "0.2.1"]
                 [compojure "1.4.0"]
                 [environ "1.0.0"]
                 [hiccup "1.0.5"]
                 [org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.228"]
                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [org.clojure/java.jdbc "0.4.2"]
                 [org.clojure/tools.trace "0.7.9"]
                 [org.postgresql/postgresql "9.3-1103-jdbc41"]
                 [prismatic/schema "1.0.1"]
                 [quiescent "0.3.1"]
                 [ring-middleware-format "0.5.0"]
                 [ring/ring-defaults "0.1.5"]
                 [ring/ring-devel "1.4.0"]
                 [ring/ring-jetty-adapter "1.4.0"]
                 [sablono "0.6.2"]
                 [sebluy/gps-tracker-common "0.1.0"]]

  :min-lein-version "2.0.0"
  :uberjar-name "gps-tracker.jar"
  :jvm-opts ["-server"]

  :main gps-tracker.core

  :plugins [[lein-environ "1.0.0"]
            [lein-ancient "0.6.7"]
            [lein-figwheel "0.5.0-4"]
            [lein-cljsbuild "1.0.6"]
            [cider/cider-nrepl "0.11.0-SNAPSHOT"]]

  :clean-targets ^{:protect false} ["resources/public/js"]

  :cljsbuild
  {:builds {:app {:source-paths ["src-cljs"]
                  :compiler     {:output-to "resources/public/js/app.js"
                                 :output-dir "resources/public/js/out"
                                 :main "gps-tracker.env"
                                 :asset-path "js/out"
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
   :dev     {:source-paths ["env/dev/clj"]
             :cljsbuild    {:builds
                            {:app
                             {:source-paths ["env/dev/cljs/"]
                              :compiler {:source-map true}}}}

             :figwheel     {:http-server-root "public"
                            :server-port      3449
                            :nrepl-port       7888
                            :nrepl-middleware ["cider.nrepl/cider-middleware"
                                               "cemerick.piggieback/wrap-cljs-repl"]
                            :css-dirs         ["resources/public/css"]}

             :repl-options {:init-ns gps-tracker.env}
             :env          {:dev true}}})
