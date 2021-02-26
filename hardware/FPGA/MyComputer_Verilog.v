
//////////////////////////////////////////////////////////////////////////////////
// Top level WHOLE system not just Qsys generated HPS system
//////////////////////////////////////////////////////////////////////////////////

module MyComputer_Verilog (
		/////////////////////////////////////////////
		// FPGA Pins
		/////////////////////////////////////////////
	
		// Clock pins
		input CLOCK_50,CLOCK2_50,CLOCK3_50,CLOCK4_50	, 
		
		// Seven Segment Displays
		// These are the names of the 6 seven segment display on the DE1 and those in the PIN Planner,
		//	so stick to these names.
		output unsigned [6:0] HEX0,HEX1,HEX2,HEX3,HEX4,HEX5,
	
		// Pushbuttons
		input unsigned [3:0] KEY,
	
		// LEDs
		output unsigned [9:0] LEDR,
	
		// Slider Switches
		input unsigned [9:0] SW,
		
		// VGA/Graphics Signals
		output VGA_BLANK_N,
		output VGA_SYNC_N,
		output VGA_CLK,
		output VGA_HS,
		output VGA_VS,
		
		output unsigned [7:0] VGA_B,
		output unsigned [7:0] VGA_G,
		output unsigned [7:0] VGA_R,
	
		// SDRAM on FPGA Side
		output unsigned [12:0] DRAM_ADDR,
		output unsigned [1:0] DRAM_BA,
		output DRAM_CAS_N,
		output DRAM_CKE,
		output DRAM_CLK,
		output DRAM_CS_N,
		inout unsigned [15:0] DRAM_DQ,
		output DRAM_LDQM,
		output DRAM_RAS_N,
		output DRAM_UDQM,
		output DRAM_WE_N,
		
		// 40-Pin Headers
		inout unsigned [35:0] GPIO_0,
		inout unsigned [35:0] GPIO_1,
		

		//////////////////////////////////////////////////////
		// HPS Pins
		//////////////////////////////////////////////////////
		
		// DDR3 SDRAM
		output unsigned [14:0] HPS_DDR3_ADDR,
		output unsigned [2:0] HPS_DDR3_BA,
		output HPS_DDR3_CAS_N,
		output HPS_DDR3_CKE,
		output HPS_DDR3_CK_N,
		output HPS_DDR3_CK_P,
		output HPS_DDR3_CS_N,
		output unsigned [3:0] HPS_DDR3_DM,
		inout unsigned [31:0] HPS_DDR3_DQ,
		inout unsigned [3:0] HPS_DDR3_DQS_N,
		inout unsigned [3:0] HPS_DDR3_DQS_P,
		output HPS_DDR3_ODT,
		output HPS_DDR3_RAS_N,
		output HPS_DDR3_RESET_N	,
		input HPS_DDR3_RZQ,
		output HPS_DDR3_WE_N,
		
		// Ethernet
		output HPS_ENET_GTX_CLK,
		inout HPS_ENET_INT_N,
		output HPS_ENET_MDC,
		inout HPS_ENET_MDIO,
		input HPS_ENET_RX_CLK,
		input unsigned [3:0] HPS_ENET_RX_DATA,
		input HPS_ENET_RX_DV,
		output unsigned [3:0] HPS_ENET_TX_DATA,
		output HPS_ENET_TX_EN,
	
		// Flash
		inout unsigned [3:0] HPS_FLASH_DATA,
		output HPS_FLASH_DCLK,
		output HPS_FLASH_NCSO,
	
		// Accelerometer
		inout HPS_GSENSOR_INT,
			
		// General Purpose I/O
		inout unsigned [1:0] HPS_GPIO,
		
		// I2C
		inout HPS_I2C_CONTROL,
		inout HPS_I2C1_SCLK,
		inout HPS_I2C1_SDAT,
		inout HPS_I2C2_SCLK,
		inout HPS_I2C2_SDAT,
	
		// Pushbutton
		inout HPS_KEY,
	
		// LED
		inout HPS_LED,
			
		// SD Card
		output HPS_SD_CLK,
		inout HPS_SD_CMD,
		inout unsigned [3:0] HPS_SD_DATA,
	
		// SPI
		output HPS_SPIM_CLK,
		input HPS_SPIM_MISO,
		output HPS_SPIM_MOSI,
		inout HPS_SPIM_SS,
	
		// UART
		input HPS_UART_RX,
		output HPS_UART_TX,
	
		// USB
		inout HPS_CONV_USB_N,
		input HPS_USB_CLKOUT,
		inout unsigned [7:0] HPS_USB_DATA,
		input HPS_USB_DIR,
		input HPS_USB_NXT,
		output HPS_USB_STP 
	 );

///////////////////////////////////////////////////////////////////////////////////////////
//  signal declarations for temporary signals/wires to connect sub-systems together
///////////////////////////////////////////////////////////////////////////////////////////

	 // temp wires carrying 2 sets of 4 bit data to each pair of Hex displays
	 // The 3 pairs of 8 bit ports generated by QSYS will drive these wires
	 // and they will be connected to the 7-Segment decoders created in VHDL 
	 // and they will drive the real HEX display on the DE1
	 
	 wire unsigned [7:0] Temp_hex0_1;
	 wire unsigned [7:0] Temp_hex2_3;
	 wire unsigned [7:0] Temp_hex4_5;

	 wire unsigned [1:0] Temp_SDRAM_DQM;
	 
	 
	// TEMP Wires  TO CONNECT IO BRIDGE from QSYS generated IOBridge TO SUBSYSTEMS INCLUDING GRAPHICS AND IO DEVICE (RS232'S)
   
	reg IO_ACK_WIRE;									// reg because driven by always@ block
	wire IO_IRQ_WIRE;
	wire unsigned [15:0] IO_Address_WIRE;
	wire IO_Bus_Enable_WIRE;
	wire unsigned [1:0] IO_Byte_Enable_WIRE;
	wire IO_RW_WIRE;
	wire unsigned [15:0] IO_Write_Data_WIRE;
	wire unsigned [15:0] IO_Read_Data_WIRE;
	
	// Other temporary wires 
	wire RESET_L_WIRE;
	wire IO_Enable_L_WIRE;
	wire IO_UpperByte_Select_L_WIRE;
	wire IO_LowerByte_Select_L_WIRE;
	
	// for LCD display connected to PIO port, 11 bits used
	wire unsigned [15:0] LCD_WIRE;
	
 
	 ///////////////////////////////////////////////////////////////////////////////////////
	 // u0 is an instanace of the QSYS generated computer
	 // map its IO ports as described below
	 ///////////////////////////////////////////////////////////////////////////////////////
	 
		 CPEN391_Computer u0 (
			.hex0_1_export                   (Temp_hex0_1),                   //               hex0_1.export
			.hex2_3_export                   (Temp_hex2_3),                   //               hex2_3.export
			.hex4_5_export                   (Temp_hex4_5),                   //               hex4_5.export
			.hps_io_hps_io_emac1_inst_TX_CLK (HPS_ENET_GTX_CLK), 					//               hps_io.hps_io_emac1_inst_TX_CLK
			.hps_io_hps_io_emac1_inst_TXD0   (HPS_ENET_TX_DATA[0]),   			//                     .hps_io_emac1_inst_TXD0
			.hps_io_hps_io_emac1_inst_TXD1   (HPS_ENET_TX_DATA[1]),   			//                     .hps_io_emac1_inst_TXD1
			.hps_io_hps_io_emac1_inst_TXD2   (HPS_ENET_TX_DATA[2]),   			//                     .hps_io_emac1_inst_TXD2
			.hps_io_hps_io_emac1_inst_TXD3   (HPS_ENET_TX_DATA[3]),   			//                     .hps_io_emac1_inst_TXD3
			.hps_io_hps_io_emac1_inst_RXD0   (HPS_ENET_RX_DATA[0]),   			//                     .hps_io_emac1_inst_RXD0
			.hps_io_hps_io_emac1_inst_MDIO   (HPS_ENET_MDIO),   					//                     .hps_io_emac1_inst_MDIO
			.hps_io_hps_io_emac1_inst_MDC    (HPS_ENET_MDC),    					//                     .hps_io_emac1_inst_MDC
			.hps_io_hps_io_emac1_inst_RX_CTL (HPS_ENET_RX_DV), 					//                     .hps_io_emac1_inst_RX_CTL
			.hps_io_hps_io_emac1_inst_TX_CTL (HPS_ENET_TX_EN), 					//                     .hps_io_emac1_inst_TX_CTL
			.hps_io_hps_io_emac1_inst_RX_CLK (HPS_ENET_RX_CLK), 					//                     .hps_io_emac1_inst_RX_CLK
			.hps_io_hps_io_emac1_inst_RXD1   (HPS_ENET_RX_DATA[1]),   			//                     .hps_io_emac1_inst_RXD1
			.hps_io_hps_io_emac1_inst_RXD2   (HPS_ENET_RX_DATA[2]),   			//                     .hps_io_emac1_inst_RXD2
			.hps_io_hps_io_emac1_inst_RXD3   (HPS_ENET_RX_DATA[3]),   			//                     .hps_io_emac1_inst_RXD3
			.hps_io_hps_io_qspi_inst_IO0     (HPS_FLASH_DATA[0]),     			//                     .hps_io_qspi_inst_IO0
			.hps_io_hps_io_qspi_inst_IO1     (HPS_FLASH_DATA[1]),     			//                     .hps_io_qspi_inst_IO1
			.hps_io_hps_io_qspi_inst_IO2     (HPS_FLASH_DATA[2]),     			//                     .hps_io_qspi_inst_IO2
			.hps_io_hps_io_qspi_inst_IO3     (HPS_FLASH_DATA[3]),     			//                     .hps_io_qspi_inst_IO3
			.hps_io_hps_io_qspi_inst_SS0     (HPS_FLASH_NCSO),     				//                     .hps_io_qspi_inst_SS0
			.hps_io_hps_io_qspi_inst_CLK     (HPS_FLASH_DCLK),     				//                     .hps_io_qspi_inst_CLK
			.hps_io_hps_io_sdio_inst_CMD     (HPS_SD_CMD),     					//                     .hps_io_sdio_inst_CMD
			.hps_io_hps_io_sdio_inst_D0      (HPS_SD_DATA[0]),      				//                     .hps_io_sdio_inst_D0
			.hps_io_hps_io_sdio_inst_D1      (HPS_SD_DATA[1]),      				//                     .hps_io_sdio_inst_D1
			.hps_io_hps_io_sdio_inst_CLK     (HPS_SD_CLK),     					//                     .hps_io_sdio_inst_CLK
			.hps_io_hps_io_sdio_inst_D2      (HPS_SD_DATA[2]),      				//                     .hps_io_sdio_inst_D2
			.hps_io_hps_io_sdio_inst_D3      (HPS_SD_DATA[3]),      				//                     .hps_io_sdio_inst_D3
			.hps_io_hps_io_usb1_inst_D0      (HPS_USB_DATA[0]),      			//                     .hps_io_usb1_inst_D0
			.hps_io_hps_io_usb1_inst_D1      (HPS_USB_DATA[1]),      			//                     .hps_io_usb1_inst_D1
			.hps_io_hps_io_usb1_inst_D2      (HPS_USB_DATA[2]),      			//                     .hps_io_usb1_inst_D2
			.hps_io_hps_io_usb1_inst_D3      (HPS_USB_DATA[3]),      			//                     .hps_io_usb1_inst_D3
			.hps_io_hps_io_usb1_inst_D4      (HPS_USB_DATA[4]),      			//                     .hps_io_usb1_inst_D4
			.hps_io_hps_io_usb1_inst_D5      (HPS_USB_DATA[5]),      			//                     .hps_io_usb1_inst_D5
			.hps_io_hps_io_usb1_inst_D6      (HPS_USB_DATA[6]),      			//                     .hps_io_usb1_inst_D6
			.hps_io_hps_io_usb1_inst_D7      (HPS_USB_DATA[7]),      			//                     .hps_io_usb1_inst_D7
			.hps_io_hps_io_usb1_inst_CLK     (HPS_USB_CLKOUT),     				//                     .hps_io_usb1_inst_CLK
			.hps_io_hps_io_usb1_inst_STP     (HPS_USB_STP),     					//                     .hps_io_usb1_inst_STP
			.hps_io_hps_io_usb1_inst_DIR     (HPS_USB_DIR),     					//                     .hps_io_usb1_inst_DIR
			.hps_io_hps_io_usb1_inst_NXT     (HPS_USB_NXT),     					//                     .hps_io_usb1_inst_NXT
			.hps_io_hps_io_spim1_inst_CLK    (HPS_SPIM_CLK),    					//                     .hps_io_spim1_inst_CLK
			.hps_io_hps_io_spim1_inst_MOSI   (HPS_SPIM_MOSI),   					//                     .hps_io_spim1_inst_MOSI
			.hps_io_hps_io_spim1_inst_MISO   (HPS_SPIM_MISO),   					//                     .hps_io_spim1_inst_MISO
			.hps_io_hps_io_spim1_inst_SS0    (HPS_SPIM_SS),    					//                     .hps_io_spim1_inst_SS0
			.hps_io_hps_io_uart0_inst_RX     (HPS_UART_RX),     					//                     .hps_io_uart0_inst_RX
			.hps_io_hps_io_uart0_inst_TX     (HPS_UART_TX),     					//                     .hps_io_uart0_inst_TX
			.hps_io_hps_io_i2c0_inst_SDA     (HPS_I2C1_SDAT),     				//                     .hps_io_i2c0_inst_SDA
			.hps_io_hps_io_i2c0_inst_SCL     (HPS_I2C1_SCLK),     				//                     .hps_io_i2c0_inst_SCL
			.hps_io_hps_io_i2c1_inst_SDA     (HPS_I2C2_SDAT),     				//                     .hps_io_i2c1_inst_SDA
			.hps_io_hps_io_i2c1_inst_SCL     (HPS_I2C2_SCLK),     				//                     .hps_io_i2c1_inst_SCL
			.hps_io_hps_io_gpio_inst_GPIO09  (HPS_CONV_USB_N),  					//                     .hps_io_gpio_inst_GPIO09
			.hps_io_hps_io_gpio_inst_GPIO35  (HPS_ENET_INT_N),  					//                     .hps_io_gpio_inst_GPIO35
			.hps_io_hps_io_gpio_inst_GPIO40  (HPS_GPIO[0]),  						//                     .hps_io_gpio_inst_GPIO40
			.hps_io_hps_io_gpio_inst_GPIO41  (HPS_GPIO[1]),  						//                     .hps_io_gpio_inst_GPIO41
			.hps_io_hps_io_gpio_inst_GPIO48  (HPS_I2C_CONTROL),  					//                     .hps_io_gpio_inst_GPIO48
			.hps_io_hps_io_gpio_inst_GPIO53  (HPS_LED),  							//                     .hps_io_gpio_inst_GPIO53
			.hps_io_hps_io_gpio_inst_GPIO54  (HPS_KEY),  							//                     .hps_io_gpio_inst_GPIO54
			.hps_io_hps_io_gpio_inst_GPIO61  (HPS_GSENSOR_INT),  					//                     .hps_io_gpio_inst_GPIO61

			// IO Bridge Connections to wires
			.io_acknowledge  						(IO_ACK_WIRE),	
			.io_irq          						(IO_IRQ_WIRE),
			.io_address      						(IO_Address_WIRE),
			.io_bus_enable  						(IO_Bus_Enable_WIRE),
			.io_byte_enable  						(IO_Byte_Enable_WIRE),
			.io_rw           						(IO_RW_WIRE),  
			.io_write_data   						(IO_Write_Data_WIRE),                    
			.io_read_data    						(IO_Read_Data_WIRE),

			// 2x24 LCD Display Connections to 16 bit PIO port
			.lcd_export								(LCD_WIRE),

			
			// Red LED connections
			.leds_export                     (LEDR),                     		//                 leds.export
			
			// SDRAM connections on the FPGA side
			.memory_mem_a                    (HPS_DDR3_ADDR),                 //               memory.mem_a
			.memory_mem_ba                   (HPS_DDR3_BA),                   //                     .mem_ba
			.memory_mem_ck                   (HPS_DDR3_CK_P),                 //                     .mem_ck
			.memory_mem_ck_n                 (HPS_DDR3_CK_N),                 //                     .mem_ck_n
			.memory_mem_cke                  (HPS_DDR3_CKE),                  //                     .mem_cke
			.memory_mem_cs_n                 (HPS_DDR3_CS_N),                 //                     .mem_cs_n
			.memory_mem_ras_n                (HPS_DDR3_RAS_N),                //                     .mem_ras_n
			.memory_mem_cas_n                (HPS_DDR3_CAS_N),                //                     .mem_cas_n
			.memory_mem_we_n                 (HPS_DDR3_WE_N),                 //                     .mem_we_n
			.memory_mem_reset_n              (HPS_DDR3_RESET_N),              //                     .mem_reset_n
			.memory_mem_dq                   (HPS_DDR3_DQ),                   //                     .mem_dq
			.memory_mem_dqs                  (HPS_DDR3_DQS_P),                //                     .mem_dqs
			.memory_mem_dqs_n                (HPS_DDR3_DQS_N),                //                     .mem_dqs_n
			.memory_mem_odt                  (HPS_DDR3_ODT),                  //                     .mem_odt
			.memory_mem_dm                   (HPS_DDR3_DM),                   //                     .mem_dm
			.memory_oct_rzqin                (HPS_DDR3_RZQ),                	//                     .oct_rzqin
			.pushbuttons_export              (KEY),              					//          pushbuttons.export
			.sdram_addr                      (DRAM_ADDR),                     //                sdram.addr
			.sdram_ba                        (DRAM_BA),                       //                     .ba
			.sdram_cas_n                     (DRAM_CAS_N),                    //                     .cas_n
			.sdram_cke                       (DRAM_CKE),                      //                     .cke
			.sdram_cs_n                      (DRAM_CS_N),                     //                     .cs_n
			.sdram_dq                        (DRAM_DQ),                       //                     .dq
			.sdram_dqm 		                  (Temp_SDRAM_DQM),                //                     .dqm
			.sdram_ras_n                     (DRAM_RAS_N),                    //                     .ras_n
			.sdram_we_n                      (DRAM_WE_N),                     //                     .we_n
			.sdram_clk_clk                   (DRAM_CLK),                   	//            sdram_clk.clk
			.slider_switches_export          (SW),          						//      slider_switches.export
			.system_pll_ref_clk_clk          (CLOCK_50),          				//   system_pll_ref_clk.clk
			.system_pll_ref_reset_reset      (0)       								// system_pll_ref_reset.reset
		);
		
 
	  ///////////////////////////////////////////////////////////////////////////////////////////////
	  // Instantiate 3 instances of the seven seg decoders
	  // one for hex display 0 and 1
	  // one for hex display 2 and 3
	  // one for hex display 4 and 5
	  // Connect their inputs to the temporary wires/signals being driven by the ports
	  // exported in Qsys and connect their outputs to the real 7-Segment displays on the DE1
	  ///////////////////////////////////////////////////////////////////////////////////////////////
	  
	  HexTo7SegmentDisplay    HEXDisplay0_1 (				// HEXDisplay0_1 is an instance of pair of 7 segment decoder
			// inputs
			.Input1(Temp_hex0_1),								// Connect input1 of the HexDisplay circuit to temporary signal/wire

			// outputs: Mapping important
			.Display0(HEX0),		// output of the component connect to HEX displays 0 and 1 on the DE1
			.Display1(HEX1)		// output of the component connect to HEX displays 0 and 1 on the DE1
		);
			
		HexTo7SegmentDisplay    HEXDisplay2_3 (			// HEXDisplay2_3 is an instance of pair of 7 segment decoder
			// inputs
			.Input1(Temp_hex2_3),								// Connect input1 of the HexDisplay circuit to temporary signal/wire

			// outputs: Mapping important
			.Display0(HEX2),		// output of the component connect to HEX displays 2 and 3 on the DE1
			.Display1(HEX3)		// output of the component connect to HEX displays 2 and 3 on the DE1
		);
			
		HexTo7SegmentDisplay    HEXDisplay4_5 (			// HEXDisplay4_5 is an instance of pair of 7 segment decoder
			// inputs
			.Input1(Temp_hex4_5),								// Connect input1 of the HexDisplay circuit to temporary signal/wire

			// outputs: Mapping important
			.Display0(HEX4),		// output of the component connect to HEX displays 4 and 5 on the DE1
			.Display1(HEX5)		// output of the component connect to HEX displays 4 and 5 on the DE1
		);	

	  ///////////////////////////////////////////////////////////////////////////////////////////////
	  // Instantiate an instance of the graphics and video controller circuit drawn as a schematic
	  ///////////////////////////////////////////////////////////////////////////////////////////////
			
		Graphics_and_Video_Controller		GraphicsController1 ( 
				.Reset_L							(RESET_L_WIRE),
				.Clock_50Mhz 					(CLOCK_50),
				.Address 						(IO_Address_WIRE),
				.DataIn 							(IO_Write_Data_WIRE),
				.DataOut 						(IO_Read_Data_WIRE),
				.IOEnable_L 					(IO_Enable_L_WIRE),
				.UpperByteSelect_L 			(IO_UpperByte_Select_L_WIRE),
				.LowerByteSelect_L 			(IO_LowerByte_Select_L_WIRE),
				.WriteEnable_L 				(IO_RW_WIRE),
				.GraphicsCS_L 					(IO_Enable_L_WIRE),
				
				.VGA_Clock						(VGA_CLK),
				.VGA_Blue 						(VGA_B),
				.VGA_Green 						(VGA_G),
				.VGA_Red							(VGA_R),
				.VGA_HSync 						(VGA_HS),
				.VGA_VSync						(VGA_VS),
				.VGA_Blanking 					(VGA_BLANK_N),
				.VGA_SYNC						(VGA_SYNC_N)
		 );
		
	
		///////////////////////////////////////////////////////////////////////////////////////////////
		// create an instance of the IO port with serial ports
		///////////////////////////////////////////////////////////////////////////////////////////////
		
		 OnChipSerialIO     SerialIOPorts (
				 
				 // Bridge Signals connecting to this component
				 
				 .Reset_L 						(RESET_L_WIRE),
				 .Clock_50Mhz 					(CLOCK_50),
				 .Address 						(IO_Address_WIRE),
				 .DataIn 						(IO_Write_Data_WIRE[7:0]),
				 .DataOut 						(IO_Read_Data_WIRE[7:0]),
				 .IOSelect_H 					(IO_Bus_Enable_WIRE),
				 .ByteSelect_L 				(IO_LowerByte_Select_L_WIRE),
				 .WE_L 							(IO_RW_WIRE),
				 .IRQ_H 							(IO_IRQ_WIRE),
				 
				 // Real World Signals brought out to Header connections
				 
				 .WiFi_RxData					(GPIO_1[14]), // Maps to 17 (which is WIFI_UART0_RX)
				 .WiFi_TxData					(GPIO_1[15]), // Maps to 18 (which is WIFI_UART0_TX)
				 .WiFi_RST_n					(0),

				 .BlueTooth_RxData 			(GPIO_1[18]), // 18 [21] is BT_UART_RX
				 .BlueTooth_TxData 			(GPIO_1[19])  // 19 [22] is BT_UART_TX
		);
		
		// Map 16 bit memory upper and lower data byte strobes to individual wires
		
		assign DRAM_UDQM = Temp_SDRAM_DQM[1];
		assign DRAM_LDQM = Temp_SDRAM_DQM[0];
	
		assign RESET_L_WIRE 	= 1'b1;
		
		// connections to wires on the top level to invert signals coming from the bridge
		
		assign IO_Enable_L_WIRE 				= ~IO_Bus_Enable_WIRE;
		assign IO_UpperByte_Select_L_WIRE 	= ~IO_Byte_Enable_WIRE[1];		
		assign IO_LowerByte_Select_L_WIRE 	= ~IO_Byte_Enable_WIRE[0];	
	
		// wire the LCD wires to the GPIO Header pins
		// data bits 0 - 7
		assign GPIO_1[1] = KEY[0];
		
		// Set WIFI_EN to high
		assign GPIO_1[10] = 1'b1;

		// Set WIFI_UART0_CST to low
		assign GPIO_1[20] = 1'b0;
		
		// Set WIFI_UART0_RST to low
		assign GPIO_1[21] = 1'b0;

		// process to generate an acknowledge for the IO Bridge 1 clock cycle after bridge IO BUS ENABLE and then remove it 
		always@(posedge CLOCK_50)
		begin
			IO_ACK_WIRE <= IO_Bus_Enable_WIRE;
		end
endmodule
