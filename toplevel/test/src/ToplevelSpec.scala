import chisel3._
import chiseltest._
import org.scalatest._

import flatspec._
import matchers._

class ToplevelSpec extends AnyFlatSpec with ChiselScalatestTester with GivenWhenThen with should.Matchers {
  behavior of "Toplevel io sample"

  it should "have the output equal to input" in {
    test(new Toplevel()).withAnnotations(
      Seq(
        WriteVcdAnnotation,
        // VerilatorBackendAnnotation, // Uncomment to use the Verilator backend
      ),
    ) { c =>
      c.io.out.peekBoolean() should be(false)
      c.clock.step()
      c.io.in.poke(true.B)
      c.clock.step()
      c.io.out.peekBoolean() should be(true)
    }
  }
}
