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

import org.apache.hadoop.io.Text;
import org.junit.Test;

public class HMapKSTest {
  @Test
  public void testRandomInsert1() {
    int size = 100000;
    Random r = new Random();
    short[] shorts = new short[size];

    MapKS<Integer> map = new HMapKS<Integer>();
    for (int i = 0; i < size; i++) {
      short k = (short) r.nextInt(Short.MAX_VALUE);
      map.put(i, k);
      shorts[i] = k;
    }

    for (int i = 0; i < size; i++) {
      short v = map.get(i);

      assertEquals(shorts[i], v);
      assertTrue(map.containsKey(i));
    }
  }

  @Test
  public void testRandomInsert2() {
    int size = 100000;
    Random r = new Random();
    short[] shorts = new short[size];
    String[] strings = new String[size];

    MapKS<String> map = new HMapKS<String>();
    for (int i = 0; i < size; i++) {
      short k = (short) r.nextInt(Short.MAX_VALUE);
      String s = new Integer(k).toString();
      map.put(s, k);
      shorts[i] = k;
      strings[i] = s;
    }

    for (int i = 0; i < size; i++) {
      short v = map.get(strings[i]);

      assertEquals(shorts[i], v);
      assertTrue(map.containsKey(strings[i]));
    }
  }

  @Test
  public void testRandomUpdate() {
    int size = 100000;
    Random r = new Random();
    short[] shorts = new short[size];

    MapKS<Integer> map = new HMapKS<Integer>();
    for (int i = 0; i < size; i++) {
      short k = (short) r.nextInt(Short.MAX_VALUE);
      map.put(i, k);
      shorts[i] = k;
    }

    assertEquals(size, map.size());

    for (int i = 0; i < size; i++) {
      map.put(i, (short) (shorts[i] + 1));
    }

    assertEquals(size, map.size());

    for (int i = 0; i < size; i++) {
      short v = map.get(i);

      assertEquals(shorts[i] + 1, v);
      assertTrue(map.containsKey(i));
    }
  }

  @Test
  public void testBasic() throws IOException {
    HMapKS<Text> m = new HMapKS<Text>();

    m.put(new Text("hi"), (short) 5);
    m.put(new Text("there"), (short) 22);

    Text key;
    int value;

    assertEquals(m.size(), 2);

    key = new Text("hi");
    value = m.get(key);
    assertEquals(value, 5);

    value = m.remove(key);
    assertEquals(m.size(), 1);

    key = new Text("there");
    value = m.get(key);
    assertEquals(value, 22);
  }

  @Test
  public void testPlus() throws IOException {
    HMapKS<Text> m1 = new HMapKS<Text>();

    m1.put(new Text("hi"), (short) 5);
    m1.put(new Text("there"), (short) 22);

    HMapKS<Text> m2 = new HMapKS<Text>();

    m2.put(new Text("hi"), (short) 4);
    m2.put(new Text("test"), (short) 5);

    m1.plus(m2);

    assertEquals(3, m1.size());
    assertTrue(m1.get(new Text("hi")) == 9);
    assertTrue(m1.get(new Text("there")) == 22);
    assertTrue(m1.get(new Text("test")) == 5);
  }

  @Test
  public void testDot() throws IOException {
    HMapKS<Text> m1 = new HMapKS<Text>();

    m1.put(new Text("hi"), (short) 5);
    m1.put(new Text("there"), (short) 2);
    m1.put(new Text("empty"), (short) 3);

    HMapKS<Text> m2 = new HMapKS<Text>();

    m2.put(new Text("hi"), (short) 4);
    m2.put(new Text("there"), (short) 4);
    m2.put(new Text("test"), (short) 5);

    assertEquals(28, m1.dot(m2));
  }

  @Test
  public void testIncrement() {
    HMapKS<String> m = new HMapKS<String>();
    assertEquals(0, m.get("one"));

    m.increment("one", (short) 1);
    assertEquals(1, m.get("one"));

    m.increment("one", (short) 1);
    m.increment("two", (short) 0);
    m.increment("three", (short) -1);

    assertEquals(2, m.get("one"));
    assertEquals(0, m.get("two"));
    assertEquals(-1, m.get("three"));
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(HMapKSTest.class);
  }
}