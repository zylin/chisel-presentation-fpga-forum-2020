// See LICENSE for license details.

package chisel3.tester.integeration

import org.scalatest._

import chisel3._
import chisel3.tester._
import chisel3.tester.internal.WriteVcdAnnotation
import chisel3.tester.internal.{VerilatorBackendAnnotation}

import chisel3.tester.experimental.TestOptionBuilder._
import chisel3.tester.experimental.UncheckedClockPoke._
import chisel3.tester.internal.{TreadleBackendAnnotation, VerilatorBackendAnnotation}
import chisel3.util._
import org.scalatest._

class FIRFilter(bitWidth: Int, coeffs: Seq[Int], multiplierPipelineStages: Int = 0, addPipelineStages: Int = 0)
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

class FIRFilterTest extends FlatSpec with ChiselScalatestTester with Matchers {
  behavior of "FIR filter unit-test"

  it should "run FIR filter without pipelining" in {
    test(new FIRFilter(16, Seq(1, 2, 3, 4))).withAnnotations(Seq(VerilatorBackendAnnotation, WriteVcdAnnotation)) {
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
    test(new FIRFilter(16, Seq(1, 2, 3, 4), multiplierPipelineStages=3, addPipelineStages=1))
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
