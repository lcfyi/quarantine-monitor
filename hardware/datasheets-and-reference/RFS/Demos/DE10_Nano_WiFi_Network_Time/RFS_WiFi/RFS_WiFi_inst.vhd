	component RFS_WiFi is
		port (
			clk_clk                                   : in  std_logic                     := 'X';             -- clk
			pio_key_external_connection_export        : in  std_logic_vector(1 downto 0)  := (others => 'X'); -- export
			pio_led_external_connection_export        : out std_logic_vector(3 downto 0);                     -- export
			pio_wifi_reset_external_connection_export : out std_logic;                                        -- export
			reset_reset_n                             : in  std_logic                     := 'X';             -- reset_n
			seg7_if_0_conduit_end_export              : out std_logic_vector(47 downto 0);                    -- export
			wifi_uart0_external_connection_rxd        : in  std_logic                     := 'X';             -- rxd
			wifi_uart0_external_connection_txd        : out std_logic;                                        -- txd
			wifi_uart0_external_connection_cts_n      : in  std_logic                     := 'X';             -- cts_n
			wifi_uart0_external_connection_rts_n      : out std_logic                                         -- rts_n
		);
	end component RFS_WiFi;

	u0 : component RFS_WiFi
		port map (
			clk_clk                                   => CONNECTED_TO_clk_clk,                                   --                                clk.clk
			pio_key_external_connection_export        => CONNECTED_TO_pio_key_external_connection_export,        --        pio_key_external_connection.export
			pio_led_external_connection_export        => CONNECTED_TO_pio_led_external_connection_export,        --        pio_led_external_connection.export
			pio_wifi_reset_external_connection_export => CONNECTED_TO_pio_wifi_reset_external_connection_export, -- pio_wifi_reset_external_connection.export
			reset_reset_n                             => CONNECTED_TO_reset_reset_n,                             --                              reset.reset_n
			seg7_if_0_conduit_end_export              => CONNECTED_TO_seg7_if_0_conduit_end_export,              --              seg7_if_0_conduit_end.export
			wifi_uart0_external_connection_rxd        => CONNECTED_TO_wifi_uart0_external_connection_rxd,        --     wifi_uart0_external_connection.rxd
			wifi_uart0_external_connection_txd        => CONNECTED_TO_wifi_uart0_external_connection_txd,        --                                   .txd
			wifi_uart0_external_connection_cts_n      => CONNECTED_TO_wifi_uart0_external_connection_cts_n,      --                                   .cts_n
			wifi_uart0_external_connection_rts_n      => CONNECTED_TO_wifi_uart0_external_connection_rts_n       --                                   .rts_n
		);

