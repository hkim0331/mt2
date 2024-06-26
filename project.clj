(defproject mt2 "v1.5.341"
  :description "micro twitter for hkimura class"
  :url "https://mt.melt.kyutech.ac.jp"
  :min-lein-version "2.0.0"

  :dependencies
  [[buddy/buddy-auth     "3.0.323"]
   [buddy/buddy-hashers  "1.8.158"]
   [clj-time             "0.15.2"]
   [com.fasterxml.jackson.core/jackson-core "2.13.2"]
   [com.taoensso/sente   "1.16.0"]
   [com.taoensso/timbre  "5.2.1"]
   [duct/core            "0.8.0"]
   ;; [duct/handler.sql     "0.4.0"] ;; resumed 1.2.9
   [duct/module.ataraxy  "0.3.0"]
   [duct/module.cljs     "0.4.1"]
   [duct/module.logging  "0.5.0"]
   ;; [duct/module.sql      "0.6.1"] ;; 2022-04-10, no effect.
   [duct/module.web      "0.7.3"] ;; was 0.7.1
   [duct/server.http.http-kit "0.1.4"]
   [http-kit "2.5.3"] ;2.5.0
   [environ "1.2.0"]
   [hato "0.9.0"]
   [hiccup "1.0.5"]
   [org.clojure/clojure "1.11.1"]
   [org.clojure/clojurescript "1.11.4"] ; 1.11.4
   [org.clojure/core.async "1.5.648"] ; 1.5.648
   ;; [org.clojure/java.jdbc "0.7.12"]
   ;; [org.postgresql/postgresql "42.3.3"]
   [ring "1.9.5"]
   [ring/ring-defaults "0.3.2"]
   [cljs-bach "0.3.0"]
   [cljs-http "0.1.46"]]

  :plugins [[duct/lein-duct   "0.12.1"]]
  :main ^:skip-aot mt2.main
  :resource-paths ["resources" "target/resources"]
  :prep-tasks     ["javac" "compile" ["run" ":duct/compiler"]]
  :middleware     [lein-duct.plugin/middleware]
  :profiles
  {:dev  [:project/dev :profiles/dev]
   :repl {:prep-tasks   ^:replace ["javac" "compile"]
          :dependencies [[cider/piggieback "0.5.3"]]
          :repl-options {:init-ns user,
                         :nrepl-middleware [cider.piggieback/wrap-cljs-repl]}}
   :uberjar {:aot :all}
   :profiles/dev {}
   :project/dev  {:source-paths   ["dev/src"]
                  :resource-paths ["dev/resources"]
                  :dependencies   [[integrant/repl "0.3.2"]
                                   [eftest "0.5.9"]
                                   [kerodon "0.9.1"]]}})
