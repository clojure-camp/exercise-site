(ns exercise-ui.server.routes
  (:require
    [clojure.java.io :as io]
    [exercise-ui.server.db :as db]))

(def routes
  [[[:get "/css/codemirror.css"]
    (fn [_request]
      {:headers {"Content-Type" "text/css"}
       :body (slurp (io/resource "cljsjs/codemirror/production/codemirror.min.css"))})]

   [[:get "/css/railscasts.css"]
    (fn [_request]
      {:headers {"Content-Type" "text/css"}
       :body (slurp (io/resource "cljsjs/codemirror/common/theme/railscasts.css"))})]

   [[:get "/api/exercises"]
    (fn [_request]
      {:status 200
       :body {:exercises (db/get-exercises)
              :order (db/get-exercise-order)}})]])
