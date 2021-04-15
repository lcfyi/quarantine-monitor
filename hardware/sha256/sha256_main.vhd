library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

use work.sha256_helpers.all;
use work.sha256_constants.all;

entity sha256_core is
  generic (message_length : integer);
  port(
      clk : in std_logic;
      rst : in std_logic;
      enable : in std_logic;
      message : in std_logic_vector(511 downto 0);

      digest : out std_logic_vector(255 downto 0)
  );
end entity;

architecture behaviour of sha256_core is
  signal Hashes : std_logic_vector(255 downto 0) := (others => '0');
  signal W : const_array;
  signal a, b, c, d, e, f, g, h : std_logic_vector(31 downto 0);

  -- A N x 512-bit array which holds every block of the padded message
  signal M : message_block((k_calculator(message_length) + message_length + 1 + 64)/ 512 - 1 downto 0) := ((others => (others => '0')));
  signal init, done, padded, scheduled, hashed : boolean := false;

begin
  sha256_hash: process(clk, rst, enable)
    variable i, t, num_iterations : integer := 0;
    variable N : integer := 0;
    variable T1, T2 : std_logic_vector(31 downto 0);
    begin
      -- Reset scenario, initialize the hash array to the initial values and set the appropriate flags
      if  (rst = '1') then
        initialize_hashes(Hashes, hash_constants);
        digest <= (others =>'0');
        done <= false;
        init <= true;
      elsif (rising_edge (clk) and enable = '1') then
        -- If we're not done and the input message is not padded yet
        if (not(done) and not(padded)) then
          N := (k_calculator(message_length) + message_length + 1 + 64 )/512;
          M <= message_padder (message, message_length);
          i := 0;
          padded <= true;
        elsif (not(done) and (padded) and not(scheduled)) then
          -- Messaging scheduling
          if (t >= 0 and t <= 15) then
            W(15 - t) <= M(i) (((32*(t + 1)) - 1) downto (32*t));
          elsif  (t >= 16 and t <= 63) then
            W(t) <= std_logic_vector(unsigned(sigma1(W(t - 2))) + unsigned(W(t - 7)) + unsigned(sigma0(W(t - 15))) + unsigned(W(t - 16)));
          else
            scheduled <= true;
            initialize_vars(a, b, c ,d, e, f, g, h, Hashes);
            num_iterations := 0;
            hashed <= false;
          end if;
          t := t + 1;
        elsif (not(done) and (padded) and (scheduled) and not(hashed)) then
          if num_iterations < 64 then
            T1 := std_logic_vector (unsigned (h) + unsigned (sum1(e)) + unsigned(Ch(e, f, g)) + unsigned(round_constants(num_iterations)) + unsigned(W(num_iterations)));
            T2 := std_logic_vector (unsigned(Maj(a, b, c)) + unsigned(sum0(a)));
            h <= g;
            g <= f;
            f <= e;
            e <= std_logic_vector(unsigned(d) + unsigned(T1));
            d <= c;
            c <= b;
            b <= a;
            a <= std_logic_vector(unsigned(T1) + unsigned(T2));
            num_iterations := num_iterations + 1;
          else
            hashed <= true;
          end if;
        elsif (not(done) and (padded) and (scheduled) and (hashed)) then
          update_hashes(Hashes, a, b, c ,d, e, f, g, h);
          if ( i + 1 < N ) then
            i := i + 1;
            t := 0;
            scheduled <= false;
          else
            done <= true;
          end if;

        else
          digest <= update_digest(Hashes);
        end if;
      end if;
  end process sha256_hash;

end architecture behaviour;
