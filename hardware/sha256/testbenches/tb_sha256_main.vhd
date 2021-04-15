library ieee;
use ieee.std_logic_1164.all;


use work.sha256_main.all;


entity tb_sha256_main is
end entity tb_sha256_main;

architecture testbench of tb_sha256_main is
	-- Input signals:
	signal rst : std_logic := '0';
	signal enable : std_logic := '0';
  signal message : std_logic_vector(511 downto 0);

	-- Output signals:
	signal digest : std_logic_vector(255 downto 0);

	-- Clock signal:
	signal clk : std_logic;
	constant clk_period : time := 10 ns;

  -- internal signals
  signal ready: std_logic;

begin

	uut: entity work.sha256_main
		port map(
			clk => clk,
			rst => rst,
			enable => enable,
			message => message,
			digest => digest,
      ready => ready
		);

	clock: process
	begin
		clk <= '0';
		wait for clk_period / 2;
		clk <= '1';
		wait for clk_period / 2;
	end process clock;

	stimulus: process
	begin
    message <= x"abc";

		-- rst the module:
		rst <= '1';
		wait for clk_period;
		rst <= '0';
		wait for clk_period;
		-- Start hashing the first test data:
		enable <= '0';
		wait for clk_period;
		enable <= '1';

		wait until ready = '1';
		assert digest = x"ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad"
			report "Hash of 'abc' is not ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad!";
		wait for clk_period;

		wait;
	end process stimulus;

end architecture testbench;
