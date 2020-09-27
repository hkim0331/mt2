(defproject mt2 "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies
  [[org.clojure/clojure "1.10.1"]
   [com.taoensso/sente "1.16.0"]
   [com.taoensso/timbre "5.0.1"]
   [duct/core "0.8.0"]
   [duct/module.ataraxy "0.3.0"]
   [duct/module.cljs "0.4.1"]
   [duct/module.logging "0.5.0"]
   [duct/module.web "0.7.1"]
   [hiccup "1.0.5"]]
  :plugins [[duct/lein-duct "0.12.1"]]
  :main ^:skip-aot mt2.main
  :resource-paths ["resources" "target/resources"]
  :prep-tasks     ["javac" "compile" ["run" ":duct/compiler"]]
  :middleware     [lein-duct.plugin/middleware]
  :profiles
  {:dev  [:project/dev :profiles/dev]
   :repl {:prep-tasks   ^:replace ["javac" "compile"]
          :dependencies [[cider/piggieback "0.5.1"]]
          :repl-options {:init-ns user, :nrepl-middleware [cider.piggieback/wrap-cljs-repl]}}
   :uberjar {:aot :all}
   :profiles/dev {}
   :project/dev  {:source-paths   ["dev/src"]
                  :resource-paths ["dev/resources"]
                  :dependencies   [[integrant/repl "0.3.2"]
                                   [eftest "0.5.9"]
                                   [kerodon "0.9.1"]]}})
