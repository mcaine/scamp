import org.scalactic.TypeCheckedTripleEquals
import org.scalatest._
import matchers.should._
import wordspec.AnyWordSpec


class MainSpec extends AnyWordSpec with Matchers with TypeCheckedTripleEquals {
	"tests" should {
		"work" in {
			55 should === (55)
		}
	}
}