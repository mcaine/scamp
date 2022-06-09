import org.scalajs.dom
import dom.{document, window}
import scala.scalajs.js.annotation.{JSExportTopLevel, JSImport}

object Main {

  @JSExportTopLevel("main")
  def main(): Unit = {
    println("Starting 'SCAMP' app...")
//
//    val p = document.createElement("p")
//    val text = document.createTextNode("Hello! this was added by Scala JS and should reload")
//    p.appendChild(text)
//    document.body.appendChild(p)

    val rootElement = Option(dom.document.getElementById("root")).getOrElse {
      val elem = dom.document.createElement("div")
      elem.id = "root"
      dom.document.body.appendChild(elem)
      elem
    }

    App().renderIntoDOM(rootElement)
  }



}
