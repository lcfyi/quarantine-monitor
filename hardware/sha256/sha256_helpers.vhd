library ieee;
use ieee.std_logic_1164.all;
use ieee.numeric_std.all;

use work.sha256_constants.all;

package sha256_helpers is

  -- Mathematical functions used in SHA256:
  function Ch (x, y, z : std_logic_vector) return std_logic_vector;
  function Maj (x, y, z : std_logic_vector) return std_logic_vector;

  -- Sum and Sigma functions
  function sum0 (x : std_logic_vector) return std_logic_vector;
  function sum1 (x : std_logic_vector) return std_logic_vector;
  function sigma0 (x : std_logic_vector) return std_logic_vector;
  function sigma1 (x : std_logic_vector) return std_logic_vector;

  -- Message padding functions
  function k_calculator (l : integer) return integer;
  function message_padder (message : std_logic_vector; l : integer) return message_block;

  -- Hashing functions
  function update_digest (H : std_logic_vector( 255 downto 0 )) return std_logic_vector;

  -- Initialization procedures
  procedure initialize_hashes (
    signal H : inout std_logic_vector(255 downto 0);
    constant hash_constants : in hash_array
  );
  procedure initialize_vars (
    signal a, b, c, d, e, f, g, h_reg : inout std_logic_vector(31 downto 0);
    signal H : in std_logic_vector( 255 downto 0 )
  );

  -- Hashing procedures
  procedure update_hashes (
    signal H : inout std_logic_vector(255 downto 0);
    signal a, b, c, d, e, f, g, h_reg : in std_logic_vector(31 downto 0)
  );

end package;

package body sha256_helpers is

  -- SHA256 functions as defined by FEDERAL INFORMATION PROCESSING STANDARDS
  -- PUBLICATION on 'Secure Hashing Standards'
  --
  -- Note that our project actually doesn't really require SECURE hashing (we easily could)
  -- have used the insecure SHA2 variant. We only use hashing for checksum computation.

  -- Returns result of Ch operations
  function Ch (x, y, z: std_logic_vector) return std_logic_vector is
  begin
      return std_logic_vector((x and y) xor (not(x) and z));
  end function;

  -- Returns result of Maj operations
  function Maj (x, y, z: std_logic_vector) return std_logic_vector is
  begin
      return std_logic_vector((x and y) xor (x and z) xor (y and z));
  end function;

  -- Returns result of ∑_0 operations
  function sum0 (x : std_logic_vector) return std_logic_vector is
  begin
      return std_logic_vector(rotate_right(unsigned(x), 2) xor rotate_right(unsigned(x), 13) xor rotate_right(unsigned(x), 22));
  end function;

  -- Returns result of ∑_1 operations
  function sum1 (x : std_logic_vector) return std_logic_vector is
  begin
      return std_logic_vector(rotate_right(unsigned(x), 6) xor rotate_right(unsigned (x), 11) xor rotate_right(unsigned(x), 25));
  end function;

  -- Calculates the result of σ_0 operations
  function sigma0 (x : std_logic_vector) return std_logic_vector is
  begin
      return std_logic_vector(rotate_right(unsigned(x), 7) xor rotate_right(unsigned(x), 18) xor shift_right(unsigned(x), 3));
  end function;

  -- Calculates the result of σ_1 operations
  function sigma1 (x : std_logic_vector) return std_logic_vector is
  begin
      return std_logic_vector(rotate_right(unsigned(x), 17) xor rotate_right(unsigned(x), 19) xor shift_right(unsigned(x), 10));
  end function;

  -- Finds #0's needed to pad to the input message
  function k_calculator (l : integer) return integer is
  begin
      return (447 - l) mod 512;
  end function;

  -- Padds the input message as defined in §5.1.1.
  function message_padder (message : std_logic_vector; l : integer) return message_block is
    variable padded_message : std_logic_vector((l + k_calculator(l) + 1 + 64) - 1 downto 0) := (others => '0');
    variable k ,padded_message_length , N : integer := 0;
    variable M : message_block ((k_calculator(l) + l + 1 + 64 )/ 512 - 1 downto 0) := ((others => (others => '0')));
    variable i : integer := 0;
    begin
        -- Calculating how much we have to pad the message with
        k := k_calculator(l);
        padded_message_length := l + k + 1 + 64;

        -- Start padding the message
        padded_message ((padded_message_length - 1) downto (padded_message_length - l)) := message((l - 1) downto 0);
        padded_message (padded_message_length - l - 1) := '1';
        padded_message (63 downto 0) := std_logic_vector(to_unsigned(l, 64));

        N := (k_calculator(l) + l + 1 + 64 )/512;
        while (i < N) loop
          M(i) := padded_message((512*( N-i ) -1) downto (512*(N - (i+1))));
          i := i + 1;
        end loop;
        return M;
  end function;

  function update_digest (H : std_logic_vector(255 downto 0)) return std_logic_vector is
  variable digest : std_logic_vector(255 downto 0);
  begin
    digest(31 downto 0) := H(255 downto 224);
    digest(63 downto 32) := H(223 downto 192);
    digest(95 downto 64 ) := H(191 downto 160);
    digest(127 downto 96) := H(159 downto 128);
    digest(159 downto 128) := H(159 downto 128);
    digest(191 downto 160) := H(255 downto 224);
    digest(223 downto 192) := H( 63 downto 32);
    digest(255 downto 224) := H( 31 downto 0);
    return digest;
  end function;

  -- Initializes the hash arrays to the constant hash values in 32 bit increments
  procedure initialize_hashes (signal H : inout std_logic_vector(255 downto 0); constant hash_constants : in hash_array) is
  begin
    H(31 downto 0) <= hash_constants(0);
    H(63 downto 32) <= hash_constants(1);
    H(95 downto 64) <= hash_constants(2);
    H(127 downto 96) <= hash_constants(3);
    H(159 downto 128) <= hash_constants(4);
    H(191 downto 160) <= hash_constants(5);
    H(223 downto 192) <= hash_constants(6);
    H(255 downto 224) <= hash_constants(7);
  end procedure;

  -- initializes variables
  procedure initialize_vars(signal a, b, c, d, e, f, g, h_reg : inout std_logic_vector(31 downto 0); signal H : in std_logic_vector(255 downto 0)) is
  begin
    a <= H(31 downto 0);
    b <= H(63 downto 32);
    c <= H(95 downto 64);
    d <= H(127 downto 96);
    e <= H(159 downto 128);
    f <= H(191 downto 160);
    g <= H(223 downto 192);
    h_reg <= H(255 downto 224);
  end procedure;

  -- Updates the current hashes to the current iteration
  procedure update_hashes (signal H : inout std_logic_vector(255 downto 0); signal a, b, c, d, e, f, g, h_reg : in std_logic_vector(31 downto 0)) is
  begin
    H(31 downto 0) <= std_logic_vector(unsigned (H( 31 downto 0)) + unsigned(a));
    H(63 downto 32) <= std_logic_vector(unsigned (H( 63 downto 32)) + unsigned(b));
    H(95 downto 64) <= std_logic_vector(unsigned (H( 95 downto 64)) + unsigned(c));
    H(127 downto 96) <= std_logic_vector(unsigned (H( 127 downto 96)) + unsigned(d));
    H(159 downto 128) <= std_logic_vector(unsigned(H( 159 downto 128)) + unsigned(e));
    H(191 downto 160) <= std_logic_vector(unsigned(H( 191 downto 160)) + unsigned(f));
    H(223 downto 192) <= std_logic_vector(unsigned(H( 223 downto 192)) + unsigned(g));
    H(255 downto 224) <= std_logic_vector(unsigned(H( 255 downto 224)) + unsigned(h_reg));
  end procedure;
end package body;
