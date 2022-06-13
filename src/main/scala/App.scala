import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.vdom.html_<^.*
import components.{CallbackOptionDemo, CanvasDemo, StateSnapshotDemo}

object App {
  val Component =
    ScalaComponent.builder[Unit]
      .renderStatic(
        <.div(
          CanvasDemo(),
          StateSnapshotDemo(),
          CallbackOptionDemo(),
        )
      )
      .build

  def apply() = Component()
}

