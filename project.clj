(defproject vvvvalvalval/scope-capture-nrepl "0.3.0-SNAPSHOT"
  :description "nREPL middleware for scope-capture"
  :url "https://github.com/vvvvalvalval/scope-capture-nrepl"
  :license {:name "MIT"
            :url "https://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [vvvvalvalval/scope-capture "0.3.2"]]

  :profiles {:provided {:dependencies [[org.clojure/tools.nrepl "0.2.12"]
                                       [nrepl/nrepl "0.5.3"]]}
             :dev {:repl-options
                   {:nrepl-middleware
                    [sc.nrepl.middleware/wrap-letsc]}}})
