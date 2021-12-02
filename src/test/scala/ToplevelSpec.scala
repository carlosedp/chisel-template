import chisel3._
import chiseltest._
import org.scalatest._

import flatspec._
import matchers._

class ToplevelSpec extends AnyFlatSpec with ChiselScalatestTester with should.Matchers {
  behavior of "Toplevel io sample"

  it should "have the output equal to input" in {
    test(new Toplevel()).withAnnotations(
      Seq(
        WriteVcdAnnotation
        // VerilatorBackendAnnotation // If you want to use Verilator backend
      )
    ) { c =>
      c.io.out.expect(false.B)
      c.clock.step()
      c.io.in.poke(true.B)
      c.clock.step()
      c.io.out.expect(true.B)
    }
  }
}
