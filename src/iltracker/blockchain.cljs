(ns iltracker.blockchain
  (:require
   [ajax.core :refer [GET]]
   ["web3-eth-contract" :as Contract]))

(def ^:const api-url "https://api.bscscan.com/api")

(def nodes
  ["https://bsc-dataseed1.binance.org/"
   "https://bsc-dataseed2.binance.org/"
   "https://bsc-dataseed3.binance.org/"
   "https://bsc-dataseed4.binance.org/"
   "https://bsc-dataseed1.defibit.io/"
   "https://bsc-dataseed2.defibit.io/"
   "https://bsc-dataseed3.defibit.io/"
   "https://bsc-dataseed4.defibit.io/"
   "https://bsc-dataseed1.ninicoin.io/"
   "https://bsc-dataseed2.ninicoin.io/"
   "https://bsc-dataseed3.ninicoin.io/"
   "https://bsc-dataseed4.ninicoin.io/"])

(defn new-contract [abi address]
  (new Contract (clj->js abi) address))

(defn get-transactions [address]
  (-> (new js/Promise (fn [resolve reject]
                        (GET api-url {:keywords? true
                                      :params {:module "account"
                                               :action "txlist"
                                               :address address
                                               :startblock 1
                                               :endblock "latest"
                                               :sort "desc"
                                               :apikey "7VPWK3E44VNZAFSVYQHD97DVKPU1Z4FYUW"}
                                      :response-format :json
                                      :handler resolve
                                      :error-handler reject})))
      (.then (fn [response]
               (if (= "1" (:status response))
                 (js/Promise.resolve (:result response))
                 ;; (js/Promise.reject (:message response))
                 (js/Promise.reject response))))
      (.catch (fn [error]
                (js/console.error error)))))

(defn read-contract [contract fn-name & parameters]
  (.setProvider contract (first nodes))
  (-> (.-methods contract)
      (aget fn-name)
      (apply parameters)
      (.call)))
