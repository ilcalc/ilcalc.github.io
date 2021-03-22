(ns il.core-test
  (:require [il.core :as core]
            [cljs.test :refer [deftest is]]))

(deftest hodl-value-test
  (is (= 3.75 (core/hodl-value 400 150 50 50)))
  (is (= 4.715 (core/hodl-value 200 543 50 50)))
  (is (= 1.5 (core/hodl-value 100 0 50 50)))
  (is (= 1.15 (core/hodl-value 100 0 15 85))))

(deftest pool-value-test
  (is (= 3.535533905932738 (core/pool-value 400 150 50 50)))
  ;;
  )

(deftest il-test
  (is (= 5.719095841793653 (core/il 400 150 50 50))))
