package components

import japgolly.scalajs.react.{Callback, Ref, ScalaComponent}
import japgolly.scalajs.react.component.Scala.BackendScope
import japgolly.scalajs.react.vdom.html_<^.*
import org.scalajs.dom.*
import typings.ol.{olMapMod, olTileMod, pluggableMapMod, sourceMod, tileLayerMod, tileMod, viewMod}
import scala.scalajs.js

import scala.scalajs.js.UndefOr

object OpenLayersDemo {

  case class State(x: Int, y: Int)

  class Backend($: BackendScope[Unit, State]) {

    val ref = Ref[html.Div]

    def render(s: State) =
      <.div(
        ^.className := "map-container"
      ).withRef(ref)

    def init: Callback = ref.foreach(r => {

      val opts = new pluggableMapMod.MapOptions {
        target = r

        layers = js.Array(
          new tileMod.default(new tileMod.Options {
            source = new sourceMod.OSM()
          })
        )

        view = new viewMod.default(new viewMod.ViewOptions {
          center = js.Array(0, 6706150)
          zoom = 18
        })
      }

      val map =  new olMapMod.default(opts)
    })
  }

  val Component =
    ScalaComponent.builder[Unit]
      .initialState(State(420, 69))
      .renderBackend[Backend]
      .componentDidMount(_.backend.init)
      .build

  def apply() = Component()
}
