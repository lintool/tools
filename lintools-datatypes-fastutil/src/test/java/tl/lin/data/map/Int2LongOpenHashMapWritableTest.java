/*
 * Lintools: tools by @lintool
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package tl.lin.data.map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import it.unimi.dsi.fastutil.ints.Int2LongMap;

import java.io.IOException;
import java.util.Random;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

public class Int2LongOpenHashMapWritableTest {

  @Test
  public void testBasic() throws IOException {
    Int2LongOpenHashMapWritable m = new Int2LongOpenHashMapWritable();

    m.put(2, 5L);
    m.put(1, 22L);
    m.put(Integer.MAX_VALUE, Integer.MAX_VALUE + 1L);

    long value;

    assertEquals(3, m.size());

    value = m.get(2);
    assertEquals(5L, value);

    value = m.remove(2);
    assertEquals(2, m.size());

    value = m.get(1);
    assertEquals(22L, value);

    assertTrue(m.get(Integer.MAX_VALUE) > 0);
    assertEquals(Integer.MAX_VALUE + 1L, m.get(Integer.MAX_VALUE));
  }

  @Test
  public void testIncrement() throws IOException {
    Int2LongOpenHashMapWritable m = new Int2LongOpenHashMapWritable();

    m.put(2, 7L);
    m.put(1, 29L);

    assertEquals(7L, m.get(2));
    assertEquals(29L, m.get(1));

    m.increment(2);
    m.increment(1);
    m.increment(3);

    assertEquals(8L, m.get(2));
    assertEquals(30L, m.get(1));
    assertEquals(1L, m.get(3));

    m.increment(1, 3L);
    m.increment(3, 5L);

    assertEquals(8L, m.get(2));
    assertEquals(33L, m.get(1));
    assertEquals(6L, m.get(3));
  }

  @Test
  public void testSerialize1() throws IOException {
    Int2LongOpenHashMapWritable m1 = new Int2LongOpenHashMapWritable();

    m1.put(3, 5L);
    m1.put(4, 22L);

    Int2LongOpenHashMapWritable n2 = Int2LongOpenHashMapWritable.create(m1.serialize());

    long value;

    assertEquals(2, n2.size());

    value = n2.get(3);
    assertEquals(5L, value);

    value = n2.remove(3);
    assertEquals(1, n2.size());

    value = n2.get(4);
    assertEquals(22L, value);
  }

  @Test
  public void testSerializeLazy1() throws IOException {
    Int2LongOpenHashMapWritable.setLazyDecodeFlag(true);
    Int2LongOpenHashMapWritable m1 = new Int2LongOpenHashMapWritable();

    m1.put(3, 5);
    m1.put(4, 22);

    Int2LongOpenHashMapWritable m2 = Int2LongOpenHashMapWritable.create(m1.serialize());

    assertEquals(0, m2.size());
    assertFalse(m2.hasBeenDecoded());

    int[] keys = m2.getKeys();
    long[] values = m2.getValues();

    assertTrue((keys[0] == 3 && values[0] == 5L && keys[1] == 4 && values[1] == 22L) ||
        (keys[1] == 3 && values[1] == 5L && keys[0] == 4 && values[0] == 22L));

    m2.decode();
    assertTrue(m2.hasBeenDecoded());

    long value;
    assertEquals(2, m2.size());

    value = m2.get(3);
    assertEquals(5L, value);

    value = m2.remove(3);
    assertEquals(1, m2.size());

    value = m2.get(4);
    assertEquals(22L, value);
  }

  @Test
  public void testSerializeLazy2() throws IOException {
    Int2LongOpenHashMapWritable.setLazyDecodeFlag(true);
    Int2LongOpenHashMapWritable m1 = new Int2LongOpenHashMapWritable();

    m1.put(3, 5L);
    m1.put(4, 22L);

    // Object m2 should not have been decoded, size lazy decode flag is true.
    Int2LongOpenHashMapWritable m2 = Int2LongOpenHashMapWritable.create(m1.serialize());

    // Even though m2 hasn't be decoded, we should be able to properly serialize it.
    Int2LongOpenHashMapWritable m3 = Int2LongOpenHashMapWritable.create(m2.serialize());

    assertEquals(0, m3.size());
    assertFalse(m3.hasBeenDecoded());

    int[] keys = m3.getKeys();
    long[] values = m3.getValues();

    assertTrue((keys[0] == 3 && values[0] == 5L && keys[1] == 4 && values[1] == 22L) ||
        (keys[1] == 3 && values[1] == 5L && keys[0] == 4 && values[0] == 22L));

    m3.decode();
    assertTrue(m3.hasBeenDecoded());

    long value;
    assertEquals(2, m3.size());

    value = m3.get(3);
    assertEquals(5L, value);

    value = m3.remove(3);
    assertEquals(1, m3.size());

    value = m3.get(4);
    assertEquals(22L, value);
  }

  @Test
  public void testSerializeEmpty() throws IOException {
    Int2LongOpenHashMapWritable m1 = new Int2LongOpenHashMapWritable();

    // make sure this does nothing
    m1.decode();

    assertEquals(0, m1.size());

    Int2LongMap m2 = Int2LongOpenHashMapWritable.create(m1.serialize());

    assertEquals(0, m2.size());
  }

  @Test
  public void testBasic1() {
    int size = 100000;
    Random r = new Random();
    int[] ints = new int[size];

    Int2LongMap map = new Int2LongOpenHashMapWritable();
    for (int i = 0; i < size; i++) {
      int k = r.nextInt(size);
      map.put(i, (long) k * (long) k * (long) k);
      ints[i] = k;
    }

    for (int i = 0; i < size; i++) {
      assertEquals((long) ints[i] * (long) ints[i] * (long) ints[i], map.get(i));
      assertTrue(map.containsKey(i));
    }
  }

  @Test
  public void testUpdate() {
    int size = 100000;
    Random r = new Random();
    int[] ints = new int[size];

    Int2LongMap map = new Int2LongOpenHashMapWritable();
    for (int i = 0; i < size; i++) {
      int k = r.nextInt(size);
      map.put(i, k);
      ints[i] = k;
    }

    assertEquals(size, map.size());

    for (int i = 0; i < size; i++) {
      map.put(i, (long) ints[i] + (long) Integer.MAX_VALUE);
    }

    assertEquals(size, map.size());

    for (int i = 0; i < size; i++) {
      assertEquals((long) ints[i] + (long) Integer.MAX_VALUE, map.get(i));
      assertTrue(map.containsKey(i));
    }
  }

  @Test
  public void testPlus() throws IOException {
    Int2LongOpenHashMapWritable m1 = new Int2LongOpenHashMapWritable();

    m1.put(1, 5L);
    m1.put(2, 22L);

    Int2LongOpenHashMapWritable m2 = new Int2LongOpenHashMapWritable();

    m2.put(1, 4L);
    m2.put(3, 5L);

    m1.plus(m2);

    assertEquals(3L, m1.size());
    assertEquals(9L, m1.get(1));
    assertEquals(22L, m1.get(2));
    assertEquals(5L, m1.get(3));
  }

  @Test
  public void testLazyPlus() throws IOException {
    Int2LongOpenHashMapWritable m1 = new Int2LongOpenHashMapWritable();

    m1.put(1, 5L);
    m1.put(2, 22L);

    Int2LongOpenHashMapWritable m2 = new Int2LongOpenHashMapWritable();

    m2.put(1, 4L);
    m2.put(3, 5L);

    Int2LongOpenHashMapWritable.setLazyDecodeFlag(true);
    Int2LongOpenHashMapWritable m3 = Int2LongOpenHashMapWritable.create(m2.serialize());

    assertEquals(0, m3.size());

    m1.lazyplus(m3);

    assertEquals(3, m1.size());
    assertEquals(9L, m1.get(1));
    assertEquals(22L, m1.get(2));
    assertEquals(5L, m1.get(3));
  }

  @Test
  public void testDot() throws IOException {
    Int2LongOpenHashMapWritable m1 = new Int2LongOpenHashMapWritable();

    m1.put(1, 2L);
    m1.put(2, 1L);
    m1.put(3, 3L);

    Int2LongOpenHashMapWritable m2 = new Int2LongOpenHashMapWritable();

    m2.put(1, 1L);
    m2.put(2, 4L);
    m2.put(4, 5L);

    long s = m1.dot(m2);

    assertEquals(6, s);
  }

  @Test
  public void testSortedEntries1() {
    Int2LongOpenHashMapWritable m = new Int2LongOpenHashMapWritable();

    m.put(1, 5L);
    m.put(2, 2L);
    m.put(3, 3L);
    m.put(4, 3L);
    m.put(5, 1L);

    Int2LongMap.Entry[] e = m.getEntriesSortedByValue();
    assertEquals(5, e.length);

    assertEquals(1, e[0].getIntKey());
    assertEquals(5L, e[0].getLongValue());

    assertEquals(3, e[1].getIntKey());
    assertEquals(3L, e[1].getLongValue());

    assertEquals(4, e[2].getIntKey());
    assertEquals(3L, e[2].getLongValue());

    assertEquals(2, e[3].getIntKey());
    assertEquals(2L, e[3].getLongValue());

    assertEquals(5, e[4].getIntKey());
    assertEquals(1L, e[4].getLongValue());
  }

  @Test
  public void testSortedEntries2() {
    Int2LongOpenHashMapWritable m = new Int2LongOpenHashMapWritable();

    m.put(1, 5);
    m.put(2, 2);
    m.put(3, 3);
    m.put(4, 3);
    m.put(5, 1);

    Int2LongMap.Entry[] e = m.getEntriesSortedByValue(2);

    assertEquals(2, e.length);

    assertEquals(1, e[0].getIntKey());
    assertEquals(5L, e[0].getLongValue());

    assertEquals(3, e[1].getIntKey());
    assertEquals(3L, e[1].getLongValue());
  }

  @Test
  public void testSortedEntries3() {
    Int2LongOpenHashMapWritable m = new Int2LongOpenHashMapWritable();

    m.put(1, 5);
    m.put(2, 2);

    Int2LongMap.Entry[] e = m.getEntriesSortedByValue(5);

    assertEquals(2, e.length);

    assertEquals(1, e[0].getIntKey());
    assertEquals(5L, e[0].getLongValue());

    assertEquals(2, e[1].getIntKey());
    assertEquals(2L, e[1].getLongValue());
  }

  @Test
  public void testSortedEntries4() {
    Int2LongOpenHashMapWritable m = new Int2LongOpenHashMapWritable();

    Int2LongMap.Entry[] e = m.getEntriesSortedByValue();
    assertTrue(e == null);
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(Int2LongOpenHashMapWritableTest.class);
  }
}
