(ns iltracker.market
  (:require
   [ajax.core :refer [GET]]
   [clojure.string :as str]))

(def ^:const api-url "https://api.coingecko.com/api/v3")

(defn- format-date [date]
  (str (.getDate date) "-" (inc (.getMonth date)) "-" (.getFullYear date)))

(defn coins []
  (new js/Promise (fn [resolve _reject]
                    (GET (str api-url "/coins/list") {:params {:include_platform true}
                                                      :response-format :json
                                                      :keywords? true
                                                      :handler resolve}))))

(defn price [coin-ids]
  (new js/Promise (fn [resolve _reject]
                    (GET (str api-url "/simple/price") {:params {:ids (str/join "," coin-ids)
                                                                 :vs_currencies "usd"}
                                                        :response-format :json
                                                        :keywords? true
                                                        :handler resolve}))))

(defn historical-price [id date]
  (if (and id date)
    (-> (new js/Promise (fn [resolve _reject]
                          (GET (str api-url "/coins/" id "/history") {:params {:localization false
                                                                               :date (format-date date)}
                                                                      :response-format :json
                                                                      :keywords? true
                                                                      :handler resolve})))
        (.then (fn [response]
                 (js/Promise.resolve (some-> response :market_data :current_price :usd)))))
    (js/Promise.resolve nil)))
