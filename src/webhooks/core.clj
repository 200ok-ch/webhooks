(ns webhooks.core
  "webhooks --port 9000 --config webhooks.edn"
  (:gen-class)
  (:require [org.httpkit.server :refer [run-server]]
            [clojure.java.shell :refer [sh]]
            [clojure.string :refer [split]]
            [clojure.pprint :refer [pprint]]
            [clojure.tools.cli :refer [parse-opts]]))

(defonce options (atom {:options {:port 9000
                                  :config "./webhook.edn"
                                  :help false}}))

(defn load-config []
  (read-string (slurp (:config (:options @options)))))

(defn- sh* [cmd in]
  (:out (apply sh (concat (split cmd #" ") [:in in]))))

(defn handler [req]
  (let [config (load-config)
        endpoints (filter #(= (:path %) (:uri req)) (:endpoints config))]
    (if-not (empty? endpoints)
      (let [out (map #(sh* (:exec %) (:body req)) endpoints)]
        {:status 200
         :headers {"Content-Type" "text/plain"}
         :body (with-out-str
                 (println "200 OK")
                 (pprint out))})
      {:status 404
       :headers {"Content-Type" "text/plain"}
       :body (with-out-str
               (println "404 Not found (unknown endpoint)")
               (pprint req)
               (pprint @options))})))

(defonce server (atom nil))

(defn stop-server []
  (when-not (nil? @server)
    (@server :timeout 100)
    (reset! server nil)))

(defn start-server []
  (let [port (:port (:options @options))]
    (reset! server (run-server #'handler {:port port}))
    (println (str "Accepting connections on port " port "."))))

(def cli-options
  [["-p" "--port PORT" "Port number"
    :default 9000
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 0x10000) "Must be a number between 0 and 65536"]]
   ["-c" "--config" "Config file"
    :default "./webhooks.edn"]
   ["-h" "--help"]])

(defn -main [& args]
  (reset! options (parse-opts args cli-options))
  (if (:help (:options @options))
    (println (:summary @options))
    (start-server)))


(comment
  (start-server)
  (stop-server)
  )
