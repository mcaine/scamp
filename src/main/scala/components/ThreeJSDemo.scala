package components

import japgolly.scalajs.react.{Callback, Ref, ScalaComponent}
import japgolly.scalajs.react.component.Scala.BackendScope
import japgolly.scalajs.react.vdom.html_<^.*
import org.scalajs.dom.*
import org.scalajs.dom.WebGLRenderingContext.*
import typings.three.ambientLightMod.AmbientLight
import typings.three.colorMod.Color
import typings.three.directionalLightMod.DirectionalLight
import typings.three.fontLoaderMod.{Font, FontLoader}
import typings.three.meshBasicMaterialMod.{MeshBasicMaterial, MeshBasicMaterialParameters}
import typings.three.meshLambertMaterialMod.{MeshLambertMaterial, MeshLambertMaterialParameters}
import typings.three.meshMod.Mesh
import typings.three.perspectiveCameraMod.PerspectiveCamera
import typings.three.sceneMod.Scene
import typings.three.textGeometryMod.{TextGeometry, TextGeometryParameters}
import typings.three.textureLoaderMod.TextureLoader
import typings.three.textureMod.Texture
import typings.three.vector3Mod.Vector3

import scala.scalajs.js

import typings.three.webGLRendererMod.{WebGLRenderer, WebGLRendererParameters}

object ThreeJSDemo {

  val width = 1000
  val height = 500

  class Backend($: BackendScope[Unit, String]) {

    val canvasRef = Ref[html.Canvas]

    def setClicked =
      $.setState("CLICKED")

    def render(state: String) =
      <.div(
        <.button(
          ^.onClick --> setClicked,
          state),
        <.div(<.canvas(
          ^.width := width.toString,
          ^.height := height.toString
        ).withRef(canvasRef))
      )

    def init: Callback =
      canvasRef.foreach(canvas => {

        val scene: Scene = SceneUtils.sceneWithDirectionalLight

        val camera: PerspectiveCamera = CameraUtils.newCamera(width, height)
        //camera.lookAt(new Vector3(0.0, 0.0 , 0.0))

        val webGLRendererParameters = WebGLRendererParameters()
        webGLRendererParameters.canvas = canvas
        val renderer = new WebGLRenderer(webGLRendererParameters)
        renderer.setSize(width, height)

        val fontLoader: FontLoader = new FontLoader()

        fontLoader.load("fonts/Old computer St_Regular.json", (font: Font) => {
          val fontSize = 1.2

          val textGeomParams = TextGeometryParameters(font)
          textGeomParams.size = fontSize
          textGeomParams.height = fontSize / 3

          val textGeometry = new TextGeometry("ThreeJS", textGeomParams)

          val meshLambertMaterialParameters = js.Dynamic.literal(
            "color" -> (0xf00000 + Math.random() * 0x0fffff).toInt
          ).asInstanceOf[MeshLambertMaterialParameters]

          val material = new MeshLambertMaterial(meshLambertMaterialParameters)

          val obj = new Mesh(textGeometry, material)

          obj.position.x = -5.1
          obj.position.y = 0
          obj.position.z = 0

          obj.rotation.x = 0
          obj.rotation.y = 0
          obj.rotation.z = 0

          scene.add(obj)

          renderer.render(scene, camera)
        })
      })
  }

  val Component =
    ScalaComponent.builder[Unit]
      .initialState("Click Me")
      .renderBackend[Backend]
      .componentDidMount(_.backend.init)
      .build

  def apply() = Component()

  object SceneUtils {
    def newScene: Scene = new Scene()

    def sceneWithAmbientLight: Scene = {
      val scene = newScene
      scene add ambientLight
      scene
    }

    def ambientLight: AmbientLight = {
      val ambientLight = new AmbientLight()
      ambientLight.color = new Color(0xffaaff)
      ambientLight
    }

    def directionalLight: DirectionalLight = {
      val light = new DirectionalLight()
      light.color = new Color(0xaaff77)
      light.position.set(10, 10, 10)
      light.lookAt(new Vector3(0,0,-10))
      light
    }

    def sceneWithDirectionalLight: Scene = {
      val scene = newScene
      scene add directionalLight
      scene
    }
  }

  object CameraUtils {
    def newCamera(width: Int, height: Int): PerspectiveCamera = {
      val camera = new PerspectiveCamera(80, width / height, 0.1, 1000)
      camera.position.set(0,1,4)
      camera
    }
  }
}
