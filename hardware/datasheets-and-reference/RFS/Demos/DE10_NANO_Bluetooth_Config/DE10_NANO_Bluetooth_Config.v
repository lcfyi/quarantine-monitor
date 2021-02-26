
//=======================================================
//  This code is generated by Terasic System Builder
//=======================================================

module DE10_NANO_Bluetooth_Config(

	//////////// ADC //////////
	output		          		ADC_CONVST,
	output		          		ADC_SCK,
	output		          		ADC_SDI,
	input 		          		ADC_SDO,

	//////////// ARDUINO //////////
	inout 		    [15:0]		ARDUINO_IO,
	inout 		          		ARDUINO_RESET_N,

	//////////// CLOCK //////////
	input 		          		FPGA_CLK1_50,
	input 		          		FPGA_CLK2_50,
	input 		          		FPGA_CLK3_50,

	//////////// HDMI //////////
	inout 		          		HDMI_I2C_SCL,
	inout 		          		HDMI_I2C_SDA,
	inout 		          		HDMI_I2S,
	inout 		          		HDMI_LRCLK,
	inout 		          		HDMI_MCLK,
	inout 		          		HDMI_SCLK,
	output		          		HDMI_TX_CLK,
	output		          		HDMI_TX_DE,
	output		    [23:0]		HDMI_TX_D,
	output		          		HDMI_TX_HS,
	input 		          		HDMI_TX_INT,
	output		          		HDMI_TX_VS,

	//////////// KEY //////////
	input 		     [1:0]		KEY,

	//////////// LED //////////
	output		     [7:0]		LED,

	//////////// SW //////////
	input 		     [3:0]		SW,

	//////////// GPIO_0, GPIO connect to RFS - RF and Sensor //////////
	inout 		          		BT_KEY,
	input 		          		BT_UART_RX,
	output		          		BT_UART_TX,
	input 		          		LSENSOR_INT,
	inout 		          		LSENSOR_SCL,
	inout 		          		LSENSOR_SDA,
	inout 		          		MPU_AD0_SDO,
	output		          		MPU_CS_n,
	output		          		MPU_FSYNC,
	input 		          		MPU_INT,
	inout 		          		MPU_SCL_SCLK,
	inout 		          		MPU_SDA_SDI,
	input 		          		RH_TEMP_DRDY_n,
	inout 		          		RH_TEMP_I2C_SCL,
	inout 		          		RH_TEMP_I2C_SDA,
	inout 		     [7:0]		TMD_D,
	input 		          		UART2USB_CTS,
	output		          		UART2USB_RTS,
	input 		          		UART2USB_RX,
	output		          		UART2USB_TX,
	output		          		WIFI_EN,
	output		          		WIFI_RST_n,
	input 		          		WIFI_UART0_CTS,
	output		          		WIFI_UART0_RTS,
	input 		          		WIFI_UART0_RX,
	output		          		WIFI_UART0_TX,
	input 		          		WIFI_UART1_RX
);



//=======================================================
//  REG/WIRE declarations
//=======================================================




//=======================================================
//  Structural coding
//=======================================================



    Qsys u0 (
        .clk_clk                            (FPGA_CLK1_50),                            //                            clk.clk
        .hc_05_uart_external_connection_rxd (BT_UART_RX), // hc_05_uart_external_connection.rxd
        .hc_05_uart_external_connection_txd (BT_UART_TX), //                               .txd
        .pio_key_external_connection_export (KEY[1]), //    pio_key_external_connection.export
        .reset_reset_n                      (KEY[0])                       //                          reset.reset_n
    );


endmodule