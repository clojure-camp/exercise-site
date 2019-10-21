(ns exercise-ui.server.routes
  (:require
    [exercise-ui.server.db :as db]))

(def routes
  [[[:get "/api/exercises"]
    (fn [request]
      {:status 200
       :body (db/get-exercises)})]])
