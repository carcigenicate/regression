(ns regression.linear.least-squares
  (:require [regression.linear.line :as l]
            [regression.linear.helpers :as rh]))

; TODO: Calculate averages once then pass in?

(defn sum [nums]
  (reduce + 0 nums))

(defn average [nums]
  (/ (sum nums)
     (count nums)))

(defn x-y-averages [points]
  [(average (rh/xs points))
   (average (rh/ys points))])

(defn calc-m
  "Calculates the slope for the given points. Defaults to 1 if only a single point is given."
  [points]
  (if (= (count points) 1)
    1
    (let [[x-avg y-avg] (x-y-averages points)

          numer-nums (map (fn [[x y]]
                            (* (- x x-avg) (- y y-avg)))
                          points)

          denom-nums (map (fn [[x _]]
                            (* (- x x-avg) (- x x-avg)))
                          points)]

      (/ (sum numer-nums)
         (sum denom-nums)))))

(defn calc-b [points m]
  (let [[x-avg y-avg] (x-y-averages points)]
    (- y-avg (* m x-avg))))

(defn calc-line
  "Returns a line of best fit for the points, or nil if points is empty."
  [points]
  (when-not (empty? points)
    (let [m (calc-m points)
          b (calc-b points m)]
      (l/->Line m b))))

(defn error-per-point [line points]
  (if (empty? points)
    0
    (let [abs #(Math/abs ^double %)]
      (/
        (reduce
          (fn [err [x y]]
            (+ err
               (abs (- y (l/y= line x)))))
          0
          points)

        (count points)))))