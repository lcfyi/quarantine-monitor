	component Qsys is
		port (
			clk_clk                            : in  std_logic := 'X'; -- clk
			hc_05_uart_external_connection_rxd : in  std_logic := 'X'; -- rxd
			hc_05_uart_external_connection_txd : out std_logic;        -- txd
			pio_key_external_connection_export : in  std_logic := 'X'; -- export
			reset_reset_n                      : in  std_logic := 'X'  -- reset_n
		);
	end component Qsys;

	u0 : component Qsys
		port map (
			clk_clk                            => CONNECTED_TO_clk_clk,                            --                            clk.clk
			hc_05_uart_external_connection_rxd => CONNECTED_TO_hc_05_uart_external_connection_rxd, -- hc_05_uart_external_connection.rxd
			hc_05_uart_external_connection_txd => CONNECTED_TO_hc_05_uart_external_connection_txd, --                               .txd
			pio_key_external_connection_export => CONNECTED_TO_pio_key_external_connection_export, --    pio_key_external_connection.export
			reset_reset_n                      => CONNECTED_TO_reset_reset_n                       --                          reset.reset_n
		);

