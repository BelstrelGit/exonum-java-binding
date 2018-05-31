package com.exonum.binding.storage.proofs.map;

import static com.exonum.binding.test.Bytes.bytes;
import static com.exonum.binding.test.Bytes.createPrefixed;
import static com.exonum.binding.test.TestParameters.parameters;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import com.exonum.binding.storage.proofs.map.DbKey.Type;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class DbKeyCommonPrefixParameterizedTest {

  @Parameter(0)
  public DbKey firstKey;

  @Parameter(1)
  public DbKey secondKey;

  @Parameter(2)
  public DbKey expectedResultKey;

  @Parameter(3)
  public String description;

  @Test
  public void commonPrefixTest() {
    DbKey actualCommonPrefixKey = firstKey.commonPrefix(secondKey);
    assertThat(actualCommonPrefixKey.getKeySlice(), equalTo(expectedResultKey.getKeySlice()));
  }

  @Parameters(name = "{index} = {3}")
  public static Collection<Object[]> testData() {
    return Arrays.asList(
        // "A | B -> C" reads "C is a common prefix of A and B"
        parameters(
            DbKey.newBranchKey(createUserKey(0b01), DbKey.KEY_SIZE),
            DbKey.newBranchKey(createUserKey(0b10), DbKey.KEY_SIZE),
            DbKey.newBranchKey(createUserKey(), DbKey.KEY_SIZE),
            "[01] | [10] -> []"),
        parameters(
            DbKey.newLeafKey(createUserKey(0b1011)),
            DbKey.newLeafKey(createUserKey(0b1111)),
            DbKey.newBranchKey(createUserKey(0b11), DbKey.KEY_SIZE),
            "[1011] | [1111] -> [11]"),
        parameters(
            DbKey.newLeafKey(createUserKey(0b00)),
            DbKey.newLeafKey(createUserKey(0b0000)),
            DbKey.newBranchKey(createUserKey(0b0), DbKey.KEY_SIZE),
            "[00] | [0000] -> [0]"),
        parameters(
            DbKey.newBranchKey(createUserKey(0b0101), DbKey.KEY_SIZE),
            DbKey.newBranchKey(createUserKey(0b11101), DbKey.KEY_SIZE),
            DbKey.newBranchKey(createUserKey(0b00101), DbKey.KEY_SIZE),
            "[0101] | [11101] -> [101]"));
  }

  /** Creates a 32-byte long user key with the given byte prefix. */
  private static byte[] createUserKey(int... bytePrefix) {
    return createPrefixed(bytes(bytePrefix), DbKey.KEY_SIZE);
  }
}