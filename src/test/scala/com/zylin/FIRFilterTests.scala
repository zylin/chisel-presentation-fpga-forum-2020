// See LICENSE for license details.

package chiseltest.integeration

import org.scalatest._

import chisel3._
import chiseltest._
import chiseltest.internal.WriteVcdAnnotation
import chiseltest.internal.{VerilatorBackendAnnotation}

import chiseltest.experimental.TestOptionBuilder._
import chiseltest.experimental.UncheckedClockPoke._
import chiseltest.internal.{TreadleBackendAnnotation, VerilatorBackendAnnotation}
import chisel3.util._
import org.scalatest._

class FirFilter(bitWidth: Int, coeffs: Seq[Int], multiplierPipelineStages: Int = 0, addPipelineStages: Int = 0)
    extends Module {
  val io = IO(new Bundle {
    val in = Input(UInt(bitWidth.W))
    val out = Output(UInt(bitWidth.W))
  })

  // Create the serial-in, parallel-out shift register
  val zs = Reg(Vec(coeffs.length, UInt(bitWidth.W)))

  zs := zs.drop(1) ++ Seq(io.in)

  io.out := (coeffs.map(_.U) zip zs)
    .map { case (a, b) => ShiftRegister(a * b, multiplierPipelineStages) }
    .reduce((a, b) => ShiftRegister(a + b, addPipelineStages))
}

class FirFilterTest extends FlatSpec with ChiselScalatestTester with Matchers {
  behavior of "FIR filter unit-test"

  it should "run FIR filter without pipelining" in {
    test(new FirFilter(16, Seq(1, 2, 3, 4))).withAnnotations(Seq(VerilatorBackendAnnotation, WriteVcdAnnotation)) {
      dut =>
        dut.io.in.poke(0.U)
        dut.clock.step(dut.zs.length)
        (Seq(7, 2, 3, 4, 5, 6, 7, 8)
          .map { in =>
            dut.io.in.poke(in.U)
            dut.clock.step(1)
            dut.io.out.peek().litValue
          }) should equal(Seq(28, 29, 32, 36, 40, 50, 60, 70).map(BigInt(_)))
    }
  }

  it should "run FIR filter with pipeline stages" in {
    test(new FirFilter(16, Seq(1, 2, 3, 4), multiplierPipelineStages=3, addPipelineStages=1))
       .withAnnotations(Seq(VerilatorBackendAnnotation, WriteVcdAnnotation)) {
      dut =>
        dut.io.in.poke(0.U)
        dut.clock.step(dut.zs.length)
        (Seq(7, 2, 3, 4, 5, 6, 7, 8, 7, 2, 3, 4, 5, 6, 7, 8)
          .map { in =>
            dut.io.in.poke(in.U)
            dut.clock.step(1)
            dut.io.out.peek().litValue
          }) should equal(Seq(0, 0, 0, 0, 28, 8, 33, 22, 43, 47, 51, 61, 63, 49, 53, 45).map(BigInt(_)))
    }
  }
}
