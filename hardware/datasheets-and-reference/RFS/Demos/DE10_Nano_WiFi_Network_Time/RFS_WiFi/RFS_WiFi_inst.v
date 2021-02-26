	RFS_WiFi u0 (
		.clk_clk                                   (<connected-to-clk_clk>),                                   //                                clk.clk
		.pio_key_external_connection_export        (<connected-to-pio_key_external_connection_export>),        //        pio_key_external_connection.export
		.pio_led_external_connection_export        (<connected-to-pio_led_external_connection_export>),        //        pio_led_external_connection.export
		.pio_wifi_reset_external_connection_export (<connected-to-pio_wifi_reset_external_connection_export>), // pio_wifi_reset_external_connection.export
		.reset_reset_n                             (<connected-to-reset_reset_n>),                             //                              reset.reset_n
		.seg7_if_0_conduit_end_export              (<connected-to-seg7_if_0_conduit_end_export>),              //              seg7_if_0_conduit_end.export
		.wifi_uart0_external_connection_rxd        (<connected-to-wifi_uart0_external_connection_rxd>),        //     wifi_uart0_external_connection.rxd
		.wifi_uart0_external_connection_txd        (<connected-to-wifi_uart0_external_connection_txd>),        //                                   .txd
		.wifi_uart0_external_connection_cts_n      (<connected-to-wifi_uart0_external_connection_cts_n>),      //                                   .cts_n
		.wifi_uart0_external_connection_rts_n      (<connected-to-wifi_uart0_external_connection_rts_n>)       //                                   .rts_n
	);

