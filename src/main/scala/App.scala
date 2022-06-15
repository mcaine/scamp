import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.vdom.html_<^.*
import components.{CallbackOptionDemo, CanvasDemo, OpenLayersDemo, StateSnapshotDemo, ThreeJSDemo}

object App {
  val Component =
    ScalaComponent.builder[Unit]
      .renderStatic(
        <.div(
//          ThreeJSDemo(),
          OpenLayersDemo()
//          CanvasDemo(),
//          StateSnapshotDemo(),
//          CallbackOptionDemo(),
        )
      )
      .build

  def apply() = Component()
}

