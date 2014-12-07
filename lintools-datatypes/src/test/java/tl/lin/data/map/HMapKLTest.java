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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Random;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

public class HMapKLTest {
  @Test
  public void testRandomInsert1() {
    int size = 100000;
    Random r = new Random();
    long[] longs = new long[size];

    MapKL<Integer> map = new HMapKL<Integer>();
    for (int i = 0; i < size; i++) {
      long k = r.nextLong();
      map.put(i, k);
      longs[i] = k;
    }

    for (int i = 0; i < size; i++) {
      long v = map.get(i);

      assertEquals(longs[i], v);
      assertTrue(map.containsKey(i));
    }
  }

  @Test
  public void testRandomInsert2() {
    int size = 100000;
    Random r = new Random();
    long[] longs = new long[size];
    String[] strings = new String[size];

    MapKL<String> map = new HMapKL<String>();
    for (int i = 0; i < size; i++) {
      int k = r.nextInt(size);
      String s = new Integer(k).toString();
      map.put(s, k);
      longs[i] = k;
      strings[i] = s;
    }

    for (int i = 0; i < size; i++) {
      long v = map.get(strings[i]);

      assertEquals(longs[i], v);
      assertTrue(map.containsKey(strings[i]));
    }
  }

  @Test
  public void testRandomUpdate() {
    int size = 100000;
    Random r = new Random();
    long[] longs = new long[size];

    MapKL<Integer> map = new HMapKL<Integer>();
    for (int i = 0; i < size; i++) {
      int k = r.nextInt(size);
      map.put(i, k);
      longs[i] = k;
    }

    assertEquals(size, map.size());

    for (int i = 0; i < size; i++) {
      map.put(i, longs[i] + (long) Integer.MAX_VALUE);
    }

    assertEquals(size, map.size());

    for (int i = 0; i < size; i++) {
      long v = map.get(i);

      assertEquals(longs[i] + (long) Integer.MAX_VALUE, v);
      assertTrue(map.containsKey(i));
    }
  }

  @Test
  public void testBasic() throws IOException {
    HMapKL<String> m = new HMapKL<String>();

    m.put("1", Integer.MAX_VALUE + 5L);
    m.put("2", Integer.MAX_VALUE + 22L);

    long value;

    assertEquals(2, m.size());

    value = m.get("1");
    assertEquals(Integer.MAX_VALUE + 5L, value);

    value = m.remove("1");
    assertEquals(m.size(), 1);

    value = m.get("2");
    assertEquals(Integer.MAX_VALUE + 22L, value);
  }

  @Test
  public void testPlus() throws IOException {
    HMapKL<String> m1 = new HMapKL<String>();

    m1.put("1", Integer.MAX_VALUE + 5L);
    m1.put("2", Integer.MAX_VALUE + 22L);
    m1.put("" + Integer.MAX_VALUE, Integer.MAX_VALUE);

    HMapKL<String> m2 = new HMapKL<String>();

    m2.put("1", 4L);
    m2.put("3", Integer.MAX_VALUE + 5L);
    m2.put("" + Integer.MAX_VALUE, Integer.MAX_VALUE);

    m1.plus(m2);

    assertEquals(m1.size(), 4);
    assertTrue(m1.get("1") == Integer.MAX_VALUE + 9L);
    assertTrue(m1.get("2") == Integer.MAX_VALUE + 22L);
    assertTrue(m1.get("3") == Integer.MAX_VALUE + 5L);
    assertTrue(m1.get("" + Integer.MAX_VALUE) == 2L * Integer.MAX_VALUE);
  }

  @Test
  public void testDot() throws IOException {
    HMapKL<String> m1 = new HMapKL<String>();

    m1.put("1", 2L);
    m1.put("2", 1L);
    m1.put("3", 3L);
    m1.put("" + Integer.MAX_VALUE, (long) Integer.MAX_VALUE);

    HMapKL<String> m2 = new HMapKL<String>();

    m2.put("1", 1L);
    m2.put("2", 4L);
    m2.put("4", 5L);
    m2.put("" + Integer.MAX_VALUE, (long) Integer.MAX_VALUE);

    long s = m1.dot(m2);

    assertTrue(s > Integer.MAX_VALUE);
    assertEquals((long) Integer.MAX_VALUE * Integer.MAX_VALUE + 6L, s);
  }

  @Test
  public void testIncrement() {
    HMapKL<String> m = new HMapKL<String>();
    assertEquals(0L, m.get("1"));

    m.increment("1", 1L);
    assertEquals(1L, m.get("1"));

    m.increment("1", 1L);
    m.increment("2", 0L);
    m.increment("3", -1L);

    assertEquals(2L, m.get("1"));
    assertEquals(0L, m.get("2"));
    assertEquals(-1L, m.get("3"));

    m.increment("" + Integer.MAX_VALUE, Integer.MAX_VALUE);
    assertEquals((long) Integer.MAX_VALUE, m.get("" + Integer.MAX_VALUE));

    m.increment("" + Integer.MAX_VALUE);
    assertEquals(1L + Integer.MAX_VALUE, m.get("" + Integer.MAX_VALUE));
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(HMapKLTest.class);
  }
}