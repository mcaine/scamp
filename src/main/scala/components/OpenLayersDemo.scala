package components

import japgolly.scalajs.react.{Callback, Ref, ScalaComponent}
import japgolly.scalajs.react.component.Scala.BackendScope
import japgolly.scalajs.react.vdom.html_<^.*
import org.scalablytyped.runtime.StringDictionary
import org.scalajs.dom.*
import typings.ol.geometryTypeMod.GeometryType
import typings.ol.{olFeatureMod, circleMod, geometryMod, geometryTypeMod, olMapMod, olTileMod, pluggableMapMod, sourceMod, tileLayerMod, tileMod, vectorLayerMod, vectorMod, viewMod}

import scala.scalajs.js
import scala.scalajs.js.UndefOr

object OpenLayersDemo {

  type OLMap = olMapMod.default

  case class State(map: Option[OLMap], x: Int, y: Int)

  class Backend($: BackendScope[Unit, State]) {

    private val ref = Ref[html.Div]

    //def render(s: State) =
    def render =
      <.div(
        ^.className := "map-container"
      ).withRef(ref)

    def init: Callback = ref.foreach(r => {

      val feature = olFeatureMod.default(new circleMod.default(js.Array(0, 6706150), 100))
      val feature2 = olFeatureMod.default(new circleMod.default(js.Array(0, 6706150), 150))

      val featureLayer = typings.ol.vectorMod.default(new vectorMod.Options {
        source = new typings.ol.sourceVectorMod.default(new typings.ol.sourceVectorMod.Options {
          features = js.Array(feature, feature2)
        })
      })

      val opts = new pluggableMapMod.MapOptions {
        target = r

        layers = js.Array(

          new tileMod.default(new tileMod.Options {
            source = new sourceMod.OSM()
          }),
          featureLayer
        )

        view = new viewMod.default(new viewMod.ViewOptions {
          center = js.Array(0, 6706150)
          zoom = 18
        })
      }

      val map = new OLMap(opts)

      $.setState(State(Some(map), 1, 2))
    })
  }

  val Component =
    ScalaComponent.builder[Unit]
      .initialState(State(None, 420, 69))
      .renderBackend[Backend]
      .componentDidMount(_.backend.init)
      .build

  def apply() = Component()
}
