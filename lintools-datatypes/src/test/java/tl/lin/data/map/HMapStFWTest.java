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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

import com.google.common.collect.Lists;

public class HMapStFWTest {

  @Test
  public void testBasic() throws IOException {
    HMapStFW m = new HMapStFW();

    m.put("hi", 5.0f);
    m.put("there", 22.0f);

    assertEquals(2, m.size());
    assertEquals(5.0f, m.get("hi"), 10e-6);

    m.remove("hi");
    assertEquals(1, m.size());

    assertEquals(22.0f, m.get("there"), 10e-6);
  }

  @Test
  public void testAccent() throws IOException {
    HMapStFW map1 = new HMapStFW();

    // '\u00E0': [LATIN SMALL LETTER A WITH GRAVE]
    // '\u00E6': [LATIN SMALL LETTER AE]
    // '\u00E7': [LATIN SMALL LETTER C WITH CEDILLA]
    // '\u00FC': [LATIN SMALL LETTER U WITH DIAERESIS]

    map1.put("\u00E0", 1.0f);
    map1.put("\u00E6", 2.0f);
    map1.put("\u00E7", 3.0f);
    map1.put("\u00FC", 4.0f);

    assertEquals(1.0f, map1.get("\u00E0"), 10e-6);
    assertEquals(2.0f, map1.get("\u00E6"), 10e-6);
    assertEquals(3.0f, map1.get("\u00E7"), 10e-6);
    assertEquals(4.0f, map1.get("\u00FC"), 10e-6);

    map1.put("\u00E0", 10.0f);
    map1.remove("\u00E6");
    map1.remove("\u00E7");
    map1.put("\u00E7", 2.0f);

    assertEquals(10.0f, map1.get("\u00E0"), 10e-6);
    assertEquals(2.0f, map1.get("\u00E7"), 10e-6);
    assertEquals(4.0f, map1.get("\u00FC"), 10e-6);

    assertEquals(3, map1.size());

    // Test serialization
    HMapStFW map2 = HMapStFW.create(map1.serialize());

    assertEquals(10.0f, map2.get("\u00E0"), 10e-6);
    assertEquals(2.0f, map2.get("\u00E7"), 10e-6);
    assertEquals(4.0f, map2.get("\u00FC"), 10e-6);
  }

  @Test
  public void testJp() throws IOException {
    HMapStFW map1 = new HMapStFW();
    BufferedReader in = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader()
        .getResourceAsStream("jp-sample.txt"), "UTF8"));

    List<String> list = Lists.newArrayList();
    int cnt = 0;
    String line;
    while ((line = in.readLine()) != null) {
      list.add(line);
      map1.put(line, cnt++);
    }

    for (int i = 0; i < list.size(); i++) {
      assertEquals((float) i, map1.get(list.get(i)), 10e-6);
    }
    assertEquals(5, map1.size());

    // Test serialization
    HMapStFW map2 = HMapStFW.create(map1.serialize());
    for (int i = 0; i < list.size(); i++) {
      assertEquals((float) i, map2.get(list.get(i)), 10e-6);
    }
    assertEquals(5, map2.size());

    for (int i = 0; i < list.size(); i++) {
      map2.remove(list.get(i));
    }
    assertEquals(5, map1.size());
    assertEquals(0, map2.size());
    in.close();
  }

  @Test
  public void testSerialize1() throws IOException {
    HMapStFW m1 = new HMapStFW();

    m1.put("hi", 5.0f);
    m1.put("there", 22.0f);

    HMapStFW n2 = HMapStFW.create(m1.serialize());

    String key;
    float value;

    assertEquals(n2.size(), 2);

    key = "hi";
    value = n2.get(key);
    assertTrue(value == 5.0f);

    value = n2.remove(key);
    assertEquals(n2.size(), 1);

    key = "there";
    value = n2.get(key);
    assertTrue(value == 22.0f);
  }

  @Test
  public void testSerializeEmpty() throws IOException {
    HMapStFW m1 = new HMapStFW();

    assertTrue(m1.size() == 0);

    HMapStFW m2 = HMapStFW.create(m1.serialize());

    assertTrue(m2.size() == 0);
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(HMapStFWTest.class);
  }
}
