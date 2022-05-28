(defproject webhooks "0.1.1-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/tools.cli "0.3.5"]
                 [http-kit "2.2.0"]]
  :main ^:skip-aot webhooks.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
