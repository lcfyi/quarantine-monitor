
module Qsys (
	clk_clk,
	hc_05_uart_external_connection_rxd,
	hc_05_uart_external_connection_txd,
	pio_key_external_connection_export,
	reset_reset_n);	

	input		clk_clk;
	input		hc_05_uart_external_connection_rxd;
	output		hc_05_uart_external_connection_txd;
	input		pio_key_external_connection_export;
	input		reset_reset_n;
endmodule
