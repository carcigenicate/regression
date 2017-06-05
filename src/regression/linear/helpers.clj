(ns regression.linear.helpers)

(defn xs [points]
  (map first points))

(defn ys [points]
  (map second points))

(defn min-x [points]
  (apply min (xs points)))

(defn max-x [points]
  (apply max (xs points)))

(defn min-y [points]
  (apply min (ys points)))

(defn max-y [points]
  (apply max (ys points)))