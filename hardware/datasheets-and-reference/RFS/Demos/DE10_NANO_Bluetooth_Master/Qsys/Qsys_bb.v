
module Qsys (
	clk_clk,
	hc_05_uart_external_connection_rxd,
	hc_05_uart_external_connection_txd,
	mode_control_external_connection_export,
	pio_key_external_connection_export,
	reset_reset_n);	

	input		clk_clk;
	input		hc_05_uart_external_connection_rxd;
	output		hc_05_uart_external_connection_txd;
	output		mode_control_external_connection_export;
	input		pio_key_external_connection_export;
	input		reset_reset_n;
endmodule
