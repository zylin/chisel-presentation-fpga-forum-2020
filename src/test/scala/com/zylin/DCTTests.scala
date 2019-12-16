// See LICENSE for license details.
//
// Based on https://www.geeksforgeeks.org/discrete-cosine-transform-algorithm-program/
//
// This integration tests demonstrates how we can use host floating point and
// dependency injection to mock hard floating point units
//
// This is a brute-force DCT, so it uses a ridiculous 4096 multiplications.
//
// There is extensive litterature on more efficient DFT(Discrete Fourier Transform),
// e.g. Cho and Lee's proposed algorithm uses 96 multiplications and 466 additions.

package chisel3.tester.integration

import org.scalatest._

import chisel3._
import chisel3.tester._
import chisel3.tester.internal.WriteVcdAnnotation
import chisel3.tester.internal.{VerilatorBackendAnnotation}

import chisel3.tester.experimental.TestOptionBuilder._
import chisel3.tester.experimental.UncheckedClockPoke._
import chisel3.tester.internal.{
  TreadleBackendAnnotation,
  VerilatorBackendAnnotation
}
import chisel3.util._
import org.scalatest._

class DCTTest extends FlatSpec with ChiselScalatestTester {
  behavior of "DCT integration test"

  class HardDouble extends Bundle {
    val value = UInt(64.W)
  }

  class HardDoubleBinaryOpBundle extends Bundle {
    val a = Input(new HardDouble)
    val b = Input(new HardDouble)
    val out = Output(new HardDouble)
  }

  class HardDoubleUnaryOpBundle extends Bundle {
    val a = Input(new HardDouble)
    val out = Output(new HardDouble)
  }

  class DCT(m: Int, n: Int) extends Module {
    val io = IO(new Bundle {
      val matrix = Input(Vec(n, Vec(m, new HardDouble)))
      val dct = Output(Vec(n, Vec(m, new HardDouble)))
      val multiply =
        Vec(n * m * n * m + n * m, Flipped(new HardDoubleBinaryOpBundle))
      val add = Vec((n * m - 1) * n * m, Flipped(new HardDoubleBinaryOpBundle))
    })

    def toHardDouble(value: Double): HardDouble = {
      val hardDouble = Wire(new HardDouble)
      hardDouble.value := (BigInt(java.lang.Double.doubleToRawLongBits(value)) & ((BigInt(
        1
      ) << 64) - 1)).U
      hardDouble
    }

    printf("First value %x\n", io.dct(0)(0).value)

    def dctCore(
        i: Int,
        j: Int,
        adders: Seq[HardDoubleBinaryOpBundle],
        multipliers: Seq[HardDoubleBinaryOpBundle]
    ): (
        HardDouble,
        Seq[HardDoubleBinaryOpBundle],
        Seq[HardDoubleBinaryOpBundle]
    ) = {
      val factors = (0 until m)
        .map(
          k =>
            (0 until n).map { l =>
              toHardDouble(
                math.cos((2 * k + 1) * i * math.Pi / (2 * m)) *
                  math.cos((2 * l + 1) * j * math.Pi / (2 * n))
              )
            }
        )
        .flatten

      val mults = (io.matrix.toSeq.flatten zip factors zip multipliers).map {
        case ((m, factor), mult) =>
          mult.a := m
          mult.b := factor
          mult.out
      }

      val sum = (adders.zip(mults.drop(1))).foldLeft(mults(0)) {
        case (b, (adder, a)) =>
          adder.a := a
          adder.b := b
          adder.out
      }

      val finalMultiply = multipliers(n * m)

      finalMultiply.a := toHardDouble({
        if (i === 0) {
          1.0 / math.sqrt(m)
        } else {
          math.sqrt(2) / math.sqrt(m)
        }
      } * {
        if (j === 0) {
          1.0 / math.sqrt(n)
        } else {
          math.sqrt(2) / math.sqrt(n)
        }
      })
      finalMultiply.b := sum

      (
        finalMultiply.out,
        adders.drop(mults.length - 1),
        multipliers.drop(n * m + 1)
      )
    }

    io.dct := (0 until m)
      .map(i => (0 until n).map((i, _)))
      .flatten
      .foldLeft((io.add.toSeq, io.multiply.toSeq, Seq.empty[HardDouble])) {
        case ((adders, multipliers, results), (i, j)) =>
          val (value, remainingAdders, remainingMultipliers) =
            dctCore(i, j, adders, multipliers)
          (remainingAdders, remainingMultipliers, results ++ Seq(value))
      }._3.sliding(n, n).map(VecInit(_)).toSeq
  }

  /**
   * 2x2 expected output:
   * 
   * 509.999969      -0.322307
   * -0.322307       0.000206
   */
  it should "do a DCT" in {
    test(new DCT(8, 8))
      .withAnnotations(Seq(VerilatorBackendAnnotation, WriteVcdAnnotation)) {
        dut =>
          var done = false

          def binaryOp(binOp: HardDoubleBinaryOpBundle): Seq[Double] = {
            Seq(binOp.a, binOp.b).map(toDouble(_))
          }

          def toDouble(value: HardDouble): Double = {
            java.lang.Double
              .longBitsToDouble(value.value.peek().litValue.toLong)
          }
          def toHardDouble(value: Double): UInt = {
            (BigInt(java.lang.Double.doubleToRawLongBits(value)) & ((BigInt(1) << 64) - 1)).U
          }

          dut.io.matrix.foreach {
            _.foreach { _.value.poke(toHardDouble(255.0)) }
          }
          dut.clock.step(1)

          for (_ <- 0 until 3) {
            dut.io.add.foreach { add =>
              add.out.value
                .poke(toHardDouble(binaryOp(add).reduce((a, b) => a + b)))
            }
            dut.io.multiply.foreach { mult =>
              mult.out.value
                .poke(toHardDouble(binaryOp(mult).reduce((a, b) => a * b)))
            }

            dut.clock.step(1)

          }

          dut.io.dct.foreach { row =>
            println(row.map(toDouble(_).formatted("%f")).mkString(", "))
          }

      }
  }
}
