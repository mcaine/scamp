package components

import japgolly.scalajs.react.{Callback, ReactEvent, ReactMouseEventFromHtml, Ref, ScalaComponent}
import japgolly.scalajs.react.component.Scala.BackendScope
import japgolly.scalajs.react.vdom.html_<^.*
import japgolly.scalajs.react.vdom.html_<^.^.onClick
import org.scalablytyped.runtime.StringDictionary
import org.scalajs.dom.*
import typings.ol.coordinateMod.Coordinate
import typings.ol.geometryTypeMod.GeometryType
import typings.ol.olMapMod.Map as OLMap
import typings.ol.vectorMod.VectorLayer
import typings.ol.pluggableMapMod.PluggableMap
import typings.ol.mapEventMod.MapEvent
import typings.ol.mapBrowserEventMod.MapBrowserEvent
import typings.ol.{circleMod, eventMod, geometryMod, geometryTypeMod, olFeatureMod, olMapMod, olTileMod, pixelMod, pluggableMapMod, sourceMod, tileLayerMod, tileMod, vectorLayerMod, vectorMod, viewMod}
import typings.ol.projMod.transform
import scala.scalajs.js
import scala.scalajs.js.UndefOr

object OpenLayersDemo {

  //type OLMap = olMapMod.default
  //type VectorLayer = typings.ol.vectorMod.VectorLayer

  //case class State(theMap: Option[OLMap], vectorLayer: Option[VectorLayer])

  class Backend($: BackendScope[Unit, Unit]) {

    private val divRef = Ref[html.Div]

    def render =
      <.div(
        ^.className := "map-container",
      ).withRef(divRef)

    def init: Callback = divRef.foreach(containerDiv => {

      val feature = olFeatureMod.default(new circleMod.default(js.Array(0, 6706150), 100))
      val feature2 = olFeatureMod.default(new circleMod.default(js.Array(0, 6706150), 150))

      val vectorLayer: VectorLayer = typings.ol.vectorMod.default(new vectorMod.Options {
        source = new typings.ol.sourceVectorMod.default(new typings.ol.sourceVectorMod.Options {
          features = js.Array(feature, feature2)
        })
      })

      val opts = new pluggableMapMod.MapOptions {
        target = containerDiv

        layers = js.Array(

          new tileMod.default(new tileMod.Options {
            source = new sourceMod.OSM()
          }),
          vectorLayer
        )

        view = new viewMod.default(new viewMod.ViewOptions {
          center = js.Array(0, 6706150)
          zoom = 18
        })
      }

      val theMap = new olMapMod.default(opts)

      theMap.on("click", (event) => {
        val mapBrowserEvent: MapBrowserEvent = event.asInstanceOf[MapBrowserEvent]
        val pluggableMap: PluggableMap = mapBrowserEvent.map
        val coordinates: Coordinate = pluggableMap.getCoordinateFromPixel(mapBrowserEvent.pixel)
        val transformedCoord = transform(coordinates, "EPSG:3857", "EPSG:4326")
        println(s"Clicked at: ${transformedCoord(0)} ${transformedCoord(1)}")
      })
    })
  }

  val Component =
    ScalaComponent.builder[Unit]
      //.initialState(State(None, None))
      .renderBackend[Backend]
      .componentDidMount(_.backend.init)
      .build

  def apply() = Component()
}
