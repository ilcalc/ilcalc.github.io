(ns iltracker.autofarm
  (:require
   [clojure.string :as str]
   [iltracker.blockchain :as b]
   ["web3" :as Web3]))

(def abi (.. (new Web3) -eth -abi))

(def ^:const deposit-method-id "0xe2bbb158")

(def ABI-pool-info
  {:inputs [{:internalType "uint256" :name "" :type "uint256"}]
   :name "poolInfo"
   :outputs [{:internalType "contract IERC20" :name "want" :type "address"}
             {:internalType "uint256" :name "allocPoint" :type "uint256"}
             {:internalType "uint256" :name "lastRewardBlock" :type "uint256"}
             {:internalType "uint256" :name "accAUTOPerShare" :type "uint256"}
             #_{:internalType "address" :name "strat" :type "address"}]
   :stateMutability "view"
   :type "function"})

(def ABI-deposit
  {:inputs [{:internalType "uint256" :name "_pid" :type "uint256"}
            {:internalType "uint256" :name "_wantAmt" :type "uint256"}]
   :name "deposit"
   :outputs []
   :type "function"})

(def ABI [ABI-pool-info ABI-deposit])

(def ABI-ERC20
  [{:constant true
    :inputs []
    :name "symbol"
    :outputs [{:internalType "string" :name "" :type "string"}]
    :payable false
    :stateMutability "view"
    :type "function"}])

(def ABI-LP
  [{:constant true
    :inputs []
    :name "token0"
    :outputs [{:internalType "address" :name "" :type "address"}]
    :payable false
    :stateMutability "view"
    :type "function"}
   {:constant true
    :inputs []
    :name "token1"
    :outputs [{:internalType "address" :name "" :type "address"}]
    :payable false
    :stateMutability "view"
    :type "function"}])

(defn- tx->parameters [{:keys [input]}]
  (if (>= (count input) 10)
    (subs input 10)
    input))

(defn deposit-transaction? [transaction]
  (-> transaction
      :input
      str/lower-case
      (str/starts-with?  deposit-method-id)))

(defn tx->pool-id [transaction]
  (some->
   (.decodeParameters abi
                      (clj->js (:inputs ABI-deposit))
                      (tx->parameters transaction))
   (.-_pid)
   (js/parseInt)))

(defn- lp-token? [symbol]
  (str/ends-with? symbol "-LP"))

(defn- lp-tokens [contract-address]
  (let [contract (b/new-contract ABI-LP contract-address)]
    (-> (js/Promise.all [(b/read-contract contract "token0")
                         (b/read-contract contract "token1")])
        (.then (fn [[token0-address token1-address]]
                 (when (and token0-address token1-address)
                   (js/Promise.all
                    [(b/read-contract (b/new-contract ABI-ERC20 token0-address) "symbol")
                     (b/read-contract (b/new-contract ABI-ERC20 token1-address) "symbol")]))))
        (.then js->clj))))

;; TODO: use batch request
(defn ^:export pool-tokens [contract-address pool-id]
  (let [contract (b/new-contract ABI contract-address)
        pool-token-address (atom "")]
    (-> (b/read-contract contract "poolInfo" pool-id)
        (.then (fn [^js result]
                 (when-let [token-address (.-want result)]
                   (reset! pool-token-address token-address)
                   (b/read-contract (b/new-contract ABI-ERC20 token-address) "symbol"))))
        (.then (fn [^string symbol]
                 (if (lp-token? symbol)
                   (lp-tokens @pool-token-address)
                   (js/Promise.resolve [symbol])))))))
