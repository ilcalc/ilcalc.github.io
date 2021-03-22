(ns il.calc)

(defn- change->variation [change]
  (/ (+ 100.0 change)
     100.0))

(defn hodl-value [change-1 change-2 weight]
  (+
   (/ (* (change->variation change-1) weight)
      100.0)
   (/ (* (change->variation change-2) (- 100.0 weight))
      100.0)))

(defn pool-value [change-1 change-2 weight]
  (*
   (Math/pow (change->variation change-1) (/ weight 100.0))
   (Math/pow (change->variation change-2) (/ (- 100.0 weight) 100.0))))

(defn il [change-1 change-2 weight]
  (->
   (- (/ (pool-value change-1 change-2 weight)
         (hodl-value change-1 change-2 weight))
      1)
   (* 100.0)
   Math/abs))
