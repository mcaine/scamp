package components

import japgolly.scalajs.react.ScalaComponent
import japgolly.scalajs.react.extra.*
import japgolly.scalajs.react.vdom.html_<^.*
import japgolly.scalajs.react.ReactEventFromInput
import japgolly.scalajs.react.ReactMonocle.*
import monocle.*

object StateSnapshotDemo {

  case class Name(firstName: String, surname: String)

  object Name {
    val firstName = Lens[Name, String](_.firstName)(x => _.copy(firstName = x))
    val surname   = Lens[Name, String](_.surname  )(x => _.copy(surname   = x))
  }

  val NameChanger = ScalaComponent.builder[StateSnapshot[String]]
    .render_P { stateSnapshot =>
      <.input.text(
        ^.value     := stateSnapshot.value,
        ^.onChange ==> ((e: ReactEventFromInput) => stateSnapshot.setState(e.target.value)))
    }
    .build

  val Main = ScalaComponent.builder[Unit]
    .initialState[Name](Name("James", "Bond"))
    .render { $ =>
      val name       = $.state
      val firstNameV = StateSnapshot.zoomL(Name.firstName).of($)
      val surnameV   = StateSnapshot.zoomL(Name.surname).of($)
      <.div(
        <.label("First name:", NameChanger(firstNameV)),
        <.label("Surname:",    NameChanger(surnameV  )),
        <.p(s"My name is ${name.surname}, ${name.firstName} ${name.surname}."))
    }
    .build

  def apply() = Main()
}
