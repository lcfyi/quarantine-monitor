
module RFS_WiFi (
	clk_clk,
	pio_key_external_connection_export,
	pio_led_external_connection_export,
	pio_wifi_reset_external_connection_export,
	reset_reset_n,
	seg7_if_0_conduit_end_export,
	wifi_uart0_external_connection_rxd,
	wifi_uart0_external_connection_txd,
	wifi_uart0_external_connection_cts_n,
	wifi_uart0_external_connection_rts_n);	

	input		clk_clk;
	input	[1:0]	pio_key_external_connection_export;
	output	[3:0]	pio_led_external_connection_export;
	output		pio_wifi_reset_external_connection_export;
	input		reset_reset_n;
	output	[47:0]	seg7_if_0_conduit_end_export;
	input		wifi_uart0_external_connection_rxd;
	output		wifi_uart0_external_connection_txd;
	input		wifi_uart0_external_connection_cts_n;
	output		wifi_uart0_external_connection_rts_n;
endmodule
