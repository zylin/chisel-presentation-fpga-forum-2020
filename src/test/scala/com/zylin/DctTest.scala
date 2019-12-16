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

package chiseltest.integration

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

class DctTest extends FlatSpec with ChiselScalatestTester {
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

    class DCT(m : Int, n : Int) extends Module {
        val io = IO(new Bundle {
            val matrix = Input(Vec(n, Vec(m, new HardDouble)))
            val dct = Output(Vec(n, Vec(m, new HardDouble)))
            val multiply = Vec(n * m * n * m + n * m, Flipped(new HardDoubleBinaryOpBundle))
            val add = Vec((n * m - 1) * n * m, Flipped(new HardDoubleBinaryOpBundle))
        })

        // dct[i][j] = ci * cj (sum(k=0 to m-1) sum(l=0 to n-1) matrix[k][l] * cos((2*k+1) *i*pi/2*m) * cos((2*l+1) *j*pi/2*n)
        // where ci= 1/sqrt(m) if i=0 else ci= sqrt(2)/sqrt(m) and
        // similarly, cj= 1/sqrt(n) if j=0 else cj= sqrt(2)/sqrt(n)

        var adder : Int = 0
        def doubleAdd(a: HardDouble, b:HardDouble) : HardDouble = {
            val add = io.add(adder)
            adder = adder + 1
            add.a := a
            add.b := b
            add.out
        }
        var multiplier : Int = 0
        def doubleMult(a: HardDouble, b:HardDouble) : HardDouble ={
            val mult = io.multiply(multiplier)
            multiplier = multiplier + 1
            mult.a := a
            mult.b := b
            mult.out
        }


          def toHardDouble(value : Double) : HardDouble ={
              val hardDouble = Wire(new HardDouble)
              hardDouble.value :=(BigInt(java.lang.Double.doubleToRawLongBits(value)) & ((BigInt(1) << 64) - 1)).U
              hardDouble
          }

          printf("First value %x\n", io.dct(0)(0).value)


        io.dct := (0 until m).map{i => 
            (0 until n).map{j => 
                doubleMult(
                    io.matrix.zipWithIndex.map{case (row, k) =>
                        
                    row.zipWithIndex.map{case (column, l) => 
                    
                    doubleMult(column,
                    toHardDouble(Math.cos((2 * k + 1) * i * math.Pi / (2 * m)) *  
                math.cos((2 * l + 1) * j * math.Pi / (2 * n))))
            }
            }.flatten.reduce{(a, b) => doubleAdd(a, b)},

            toHardDouble({if (i === 0) {1.0 / math.sqrt(m)} else {math.sqrt(2) / math.sqrt(m)}} *
            {if (i === 0) {1.0 / math.sqrt(n)} else {math.sqrt(2) / math.sqrt(n)}})

            )

            }
        }.map(VecInit(_))
    }

  it should "do a DCT" in {
    test(new DCT(8, 8)).withAnnotations(Seq(
        
    VerilatorBackendAnnotation, 
    
    WriteVcdAnnotation)) { dut =>
      
        var done = false

          def binaryOp(binOp : HardDoubleBinaryOpBundle) : Seq[Double] = {
            Seq(binOp.a, binOp.b).map(toDouble(_))
          }

          def toDouble(value : HardDouble) : Double ={
            java.lang.Double.longBitsToDouble(value.value.peek().litValue.toLong)
          }
          def toHardDouble(value : Double) : UInt ={
              (BigInt(java.lang.Double.doubleToRawLongBits(value)) & ((BigInt(1) << 64) - 1)).U
          }


          dut.io.matrix.foreach{_.foreach{_.value.poke(toHardDouble(255.0))}}
          dut.clock.step(1)

          for (_ <- 0 until 3) 
          {
          dut.io.add.foreach{add => 
            add.out.value.poke( toHardDouble(binaryOp(add).reduce((a, b) => a + b)))           
            }
            dut.io.multiply.foreach{mult => 
                mult.out.value.poke( toHardDouble(binaryOp(mult).reduce((a, b) => a * b)))
            }

            dut.clock.step(1)

          }

            dut.io.dct.foreach{row =>
                println(row.map(toDouble(_).toString()).mkString(", "))           
            }

            fork {

          }.fork {
            done = true
          }.join


    }
  }
}
