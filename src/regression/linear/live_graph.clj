(ns regression.linear.live-graph
  (:require [quil.core :as q]
            [quil.middleware :as m]

            [regression.linear.helpers :as rh]
            [regression.linear.least-squares :as ls]

            [helpers.quil-helpers :as qh]
            [regression.linear.line :as l]))

(def width 1500)
(def height 1500)

(def point-weight 10)
(def line-weight (* point-weight 0.5))

(def font-type "Arial")
(def font-color [0 0 0])

(def pointer-font-size 30)
(def line-data-font-size 40)

(def pointer-font (delay (q/create-font font-type pointer-font-size)))
(def line-data-font (delay (q/create-font font-type line-data-font-size)))

(def line-data-coord [line-data-font-size (- height (* line-data-font-size 3))])

(def error-coord [line-data-font-size (- height line-data-font-size)])

(def coord-text-offset 10)

(def test-points
  [[50 100]
   [180 230]
   [400 500]
   [700 800]])

(defrecord Graph-State [points line])

(defmacro with-font [font & body]
  `(q/with-fill ~font-color
     (qh/with-font ~font
        ~@body)))

(defn coord-text-pos [[x y]]
  [(+ x coord-text-offset)
   (- y coord-text-offset)])

(defn flip-y-axis [[x y]]
  [x (- height y)])

(defn screen-to-graph-point [point]
  (-> point
      (flip-y-axis)))

(defn draw-text-for-point
  "Draws text representing point at the draw-point coordinate."
  [font draw-point point]
  (with-font font
    (let [[cx cy] (coord-text-pos draw-point)
          [x y] point]
      (q/text (str "[" x ", " y "]") cx cy))))

(defn draw-point [[x y :as point]]
  (let [[x' y' :as point'] (flip-y-axis point)]
    (q/point x' y')
    (draw-text-for-point @pointer-font point' point)))

(defn draw-line [line min-x max-x]
  (let [{m :m b :b} line
        [min-x' min-y] (screen-to-graph-point [min-x (l/y= line min-x)])
        [max-x' max-y]  (screen-to-graph-point [max-x (l/y= line max-x)])]
    (q/line min-x' min-y max-x' max-y)))

(defn draw-line-data [line [x y]]
  (with-font @line-data-font
    (q/text (str line) x y)))

(defn draw-error [line points [x y]]
  (with-font @line-data-font
    (q/text (str "Error/point: " (ls/error-per-point line points))
            x y)))

(defn new-state []
  (->Graph-State [] nil))

(defn setup-state []
  (q/text-font @pointer-font)

  (new-state))

(defn update-state [state]
  state)

(defn draw-state [state]
  (q/background 200 200 200)
  (let [{points :points line :line} state
        mouse-point [(q/mouse-x) (q/mouse-y)]]

    (qh/with-weight point-weight
      (doseq [[x y :as point] points]
        (draw-point point)))

    (when line
      (qh/with-weight line-weight
         (draw-line line 0 width)))

    (with-font @pointer-font
      (draw-text-for-point @pointer-font mouse-point (flip-y-axis mouse-point)))

    (draw-line-data line line-data-coord)

    (draw-error line points error-coord)))

(defn mouse-click-handler [state {x :x y :y b :button}]
  (case b
    :left (let [{points :points} state
                point (flip-y-axis [x y])
                points' (conj points point)
                line' (ls/calc-line points')]
            (assoc state :points points'
                         :line line'))

    :right (new-state)

    state))

(defn -main []
  (q/defsketch Live-Linear-Regression-Graph
    :size [width height]

    :setup setup-state
    :update update-state
    :draw draw-state

    :middleware [m/fun-mode]

    :mouse-clicked mouse-click-handler))