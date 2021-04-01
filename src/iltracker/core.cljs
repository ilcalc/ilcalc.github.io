(ns iltracker.core
  (:require
   [iltracker.autofarm :as a]
   [iltracker.blockchain :as b]
   [iltracker.market :as m]
   [clojure.string :as str]))

(defn- tx->deposit [transaction]
  (let [contract-address (:to transaction)
        pool-id (a/tx->pool-id transaction)
        timestamp (-> transaction :timeStamp int (* 1000))]
    (-> (a/pool-tokens contract-address pool-id)
        (.then (fn [pool-tokens]
                 {:date (new js/Date timestamp)
                  :contract-address contract-address
                  :pool-id pool-id
                  :tokens pool-tokens})))))

(defn- transactions->deposits [transactions]
  (->> transactions
       (filter a/deposit-transaction?)
       (map tx->deposit)
       js/Promise.all))

(defn- find-coin-by-symbol-in-blockchain [coins symbol blockchain]
  (->> coins
       (filter #(-> % :platforms blockchain))
       (filter #(= (str/lower-case (:symbol %)) (str/lower-case symbol)))
       first
       :id))

(defn- find-coin-by-symbol-any-blockchain [coins symbol]
  (->> coins
       (filter #(= (str/lower-case (:symbol %)) (str/lower-case symbol)))
       first
       :id))

(defn- find-coin-by-symbol [coins symbol]
  (or (find-coin-by-symbol-in-blockchain coins symbol :binance-smart-chain)
      (find-coin-by-symbol-any-blockchain coins symbol)))

(defn add-coin-ids [deposits coins]
  (mapv
   (fn [deposit]
     (assoc deposit :coin-ids (mapv #(find-coin-by-symbol coins %)
                                    (:tokens deposit))))
   deposits))

(defn- add-coin-prices [deposits prices]
  (mapv
   (fn [deposit]
     (assoc deposit :current-prices (mapv #(get-in prices [(keyword %) :usd] 0.0)
                                          (:coin-ids deposit))))
   deposits))

(defn- add-prices [deposits]
  (-> (m/coins)
      (.then (fn [coins]
               (let [deposits (add-coin-ids deposits coins)
                     all-coin-ids (->> deposits
                                       (map :coin-ids)
                                       flatten
                                       (remove nil?)
                                       set)]
                 (-> (m/price all-coin-ids)
                     (.then #(js/Promise.resolve (add-coin-prices deposits %)))))))))

(defn- add-historical-prices [deposits]
  (js/Promise.all
   (mapv
    (fn [deposit]
      (-> (js/Promise.all (mapv #(m/historical-price % (:date deposit))
                                (:coin-ids deposit)))
          (.then (fn [historical-prices]
                   (js/Promise.resolve
                    (assoc deposit :historical-prices historical-prices))))))
    deposits)))

(defn- track-address [address]
  (println "Fetching transactions...")
  (-> (b/get-transactions address)
      (.then transactions->deposits)
      (.then add-prices)
      (.then add-historical-prices)
      (.then (fn [results]
               (println results)
               (js/Promise.resolve
                (clj->js (mapv clj->js results)))))))

(defn main []
  (println "Loaded"))
