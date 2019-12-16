module FIRFilter(
  input         clock,
  input         reset,
  input  [15:0] io_in,
  output [15:0] io_out
);
  reg [15:0] zs_0; // @[FIRFilterTests.scala 26:15]
  reg [31:0] _RAND_0;
  reg [15:0] zs_1; // @[FIRFilterTests.scala 26:15]
  reg [31:0] _RAND_1;
  reg [15:0] zs_2; // @[FIRFilterTests.scala 26:15]
  reg [31:0] _RAND_2;
  reg [15:0] zs_3; // @[FIRFilterTests.scala 26:15]
  reg [31:0] _RAND_3;
  reg [16:0] _T_1; // @[Reg.scala 15:16]
  reg [31:0] _RAND_4;
  reg [16:0] _T_2; // @[Reg.scala 15:16]
  reg [31:0] _RAND_5;
  reg [16:0] _T_3; // @[Reg.scala 15:16]
  reg [31:0] _RAND_6;
  reg [17:0] _T_5; // @[Reg.scala 15:16]
  reg [31:0] _RAND_7;
  reg [17:0] _T_6; // @[Reg.scala 15:16]
  reg [31:0] _RAND_8;
  reg [17:0] _T_7; // @[Reg.scala 15:16]
  reg [31:0] _RAND_9;
  reg [17:0] _T_9; // @[Reg.scala 15:16]
  reg [31:0] _RAND_10;
  reg [17:0] _T_10; // @[Reg.scala 15:16]
  reg [31:0] _RAND_11;
  reg [17:0] _T_11; // @[Reg.scala 15:16]
  reg [31:0] _RAND_12;
  reg [18:0] _T_13; // @[Reg.scala 15:16]
  reg [31:0] _RAND_13;
  reg [18:0] _T_14; // @[Reg.scala 15:16]
  reg [31:0] _RAND_14;
  reg [18:0] _T_15; // @[Reg.scala 15:16]
  reg [31:0] _RAND_15;
  wire [17:0] _GEN_15; // @[FIRFilterTests.scala 32:39]
  reg [17:0] _T_18; // @[Reg.scala 15:16]
  reg [31:0] _RAND_16;
  reg [17:0] _T_21; // @[Reg.scala 15:16]
  reg [31:0] _RAND_17;
  wire [18:0] _GEN_16; // @[FIRFilterTests.scala 32:39]
  reg [18:0] _T_24; // @[Reg.scala 15:16]
  reg [31:0] _RAND_18;
  assign _GEN_15 = {{1'd0}, _T_3}; // @[FIRFilterTests.scala 32:39]
  assign _GEN_16 = {{1'd0}, _T_21}; // @[FIRFilterTests.scala 32:39]
  assign io_out = _T_24[15:0]; // @[FIRFilterTests.scala 30:10]
`ifdef RANDOMIZE_GARBAGE_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_INVALID_ASSIGN
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_REG_INIT
`define RANDOMIZE
`endif
`ifdef RANDOMIZE_MEM_INIT
`define RANDOMIZE
`endif
`ifndef RANDOM
`define RANDOM $random
`endif
`ifdef RANDOMIZE_MEM_INIT
  integer initvar;
`endif
`ifndef SYNTHESIS
initial begin
  `ifdef RANDOMIZE
    `ifdef INIT_RANDOM
      `INIT_RANDOM
    `endif
    `ifndef VERILATOR
      `ifdef RANDOMIZE_DELAY
        #`RANDOMIZE_DELAY begin end
      `else
        #0.002 begin end
      `endif
    `endif
  `ifdef RANDOMIZE_REG_INIT
  _RAND_0 = {1{`RANDOM}};
  zs_0 = _RAND_0[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_1 = {1{`RANDOM}};
  zs_1 = _RAND_1[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_2 = {1{`RANDOM}};
  zs_2 = _RAND_2[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_3 = {1{`RANDOM}};
  zs_3 = _RAND_3[15:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_4 = {1{`RANDOM}};
  _T_1 = _RAND_4[16:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_5 = {1{`RANDOM}};
  _T_2 = _RAND_5[16:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_6 = {1{`RANDOM}};
  _T_3 = _RAND_6[16:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_7 = {1{`RANDOM}};
  _T_5 = _RAND_7[17:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_8 = {1{`RANDOM}};
  _T_6 = _RAND_8[17:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_9 = {1{`RANDOM}};
  _T_7 = _RAND_9[17:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_10 = {1{`RANDOM}};
  _T_9 = _RAND_10[17:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_11 = {1{`RANDOM}};
  _T_10 = _RAND_11[17:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_12 = {1{`RANDOM}};
  _T_11 = _RAND_12[17:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_13 = {1{`RANDOM}};
  _T_13 = _RAND_13[18:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_14 = {1{`RANDOM}};
  _T_14 = _RAND_14[18:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_15 = {1{`RANDOM}};
  _T_15 = _RAND_15[18:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_16 = {1{`RANDOM}};
  _T_18 = _RAND_16[17:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_17 = {1{`RANDOM}};
  _T_21 = _RAND_17[17:0];
  `endif // RANDOMIZE_REG_INIT
  `ifdef RANDOMIZE_REG_INIT
  _RAND_18 = {1{`RANDOM}};
  _T_24 = _RAND_18[18:0];
  `endif // RANDOMIZE_REG_INIT
  `endif // RANDOMIZE
end // initial
`endif // SYNTHESIS
  always @(posedge clock) begin
    zs_0 <= zs_1;
    zs_1 <= zs_2;
    zs_2 <= zs_3;
    zs_3 <= io_in;
    _T_1 <= 16'h1 * zs_0;
    _T_2 <= _T_1;
    _T_3 <= _T_2;
    _T_5 <= 16'h2 * zs_1;
    _T_6 <= _T_5;
    _T_7 <= _T_6;
    _T_9 <= 16'h3 * zs_2;
    _T_10 <= _T_9;
    _T_11 <= _T_10;
    _T_13 <= 16'h4 * zs_3;
    _T_14 <= _T_13;
    _T_15 <= _T_14;
    _T_18 <= _GEN_15 + _T_7;
    _T_21 <= _T_18 + _T_11;
    _T_24 <= _GEN_16 + _T_15;
  end
endmodule
