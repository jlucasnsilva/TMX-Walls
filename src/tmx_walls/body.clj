(ns tmx-walls.body
  (:require [play-clj.g2d-physics :refer [add-body! body-def
                                          body! fixture-def
                                          chain-shape]]
            [tmx-walls.utils :as u])
  (:import  [com.badlogic.gdx.math Circle Ellipse Polygon
             Polyline Rectangle]
            [com.badlogic.gdx.graphics.g2d TextureRegion]))

(def ^:private default-fixture
  {:density  1
   :friction 0
   :restitution 0})

(defn- create-body!
  [screen vertices fixture]
  (let [fixture (merge default-fixture fixture)
        body (add-body! screen (body-def :static))]
    (->> vertices
         (chain-shape :create-chain)
         (fixture-def :density  (:density fixture)
                      :friction (:friction fixture)
                      :restitution (:restitution fixture)
                      :shape ,,,)
         (body! body :create-fixture ,,,))
    body))

(defmulti wall-body!
  "Takes a (libGDX) MapObject and returns a map with
  information needed to build a (Box2D) body."
  (fn [screen map-wall]
    (:shape map-wall)))

(defmethod wall-body! :circle
  [screen map-wall]
  (throw
   (Exception. "TODO: tmx-walls.core/wall-body! :circle")))

(defmethod wall-body! :ellipse
  [screen map-wall]
  (throw
   (Exception. "TODO: tmx-walls.core/wall-body! :ellipse")))

(defmethod wall-body! :polygon
  [screen map-wall]
  (create-body! screen
                (:vertices (:data map-wall))
                (:properties map-wall)))

(defmethod wall-body! :polyline
  [screen map-wall]
  (create-body! screen
                (:vertices (:data map-wall))
                (:properties map-wall)))

(defmethod wall-body! :rectangle
  [screen map-wall]
  (let [r (:data map-wall)
        x (:x r)
        y (:y r)
        w (:width  r)
        h (:height r)]
    (create-body! screen
                  (u/rectangle-vertices x y w h)
                  (:properties map-wall))))
