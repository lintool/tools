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

import junit.framework.JUnit4TestAdapter;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class HMapILWTest {

  @Test
  public void testBasic() throws IOException {
    HMapILW m = new HMapILW();

    m.put(2, Integer.MAX_VALUE + 5L);
    m.put(1, Integer.MAX_VALUE + 22L);

    long value;

    assertEquals(m.size(), 2);

    value = m.get(2);
    assertEquals(Integer.MAX_VALUE + 5L, value);
    assertTrue(value > 0);

    value = m.remove(2);
    assertEquals(m.size(), 1);

    value = m.get(1);
    assertEquals(Integer.MAX_VALUE + 22L, value);
    assertTrue(value > 0);
  }

  @Test
  public void testSerialize1() throws IOException {
    HMapILW m1 = new HMapILW();

    m1.put(3, Integer.MAX_VALUE + 5L);
    m1.put(4, Integer.MAX_VALUE + 22L);

    HMapILW n2 = HMapILW.create(m1.serialize());

    long value;

    assertEquals(n2.size(), 2);

    value = n2.get(3);
    assertEquals(Integer.MAX_VALUE + 5L, value);
    assertTrue(value > 0);

    value = n2.remove(3);
    assertEquals(n2.size(), 1);

    value = n2.get(4);
    assertEquals(Integer.MAX_VALUE + 22L, value);
    assertTrue(value > 0);
  }

  @Test
  public void testSerializeLazy1() throws IOException {
    HMapILW.setLazyDecodeFlag(true);
    HMapILW m1 = new HMapILW();

    m1.put(3, Integer.MAX_VALUE + 5L);
    m1.put(4, Integer.MAX_VALUE + 22L);

    HMapILW m2 = HMapILW.create(m1.serialize());

    assertEquals(2, m2.size());

    int[] keys = m2.getKeys();
    long[] values = m2.getValues();

    assertTrue(keys[0] == 3);
    assertTrue(keys[1] == 4);

    assertTrue(values[0] == Integer.MAX_VALUE + 5L);
    assertTrue(values[0] > 0);
    assertTrue(values[1] == Integer.MAX_VALUE + 22L);
    assertTrue(values[1] > 0);

    assertFalse(m2.isDecoded());
    assertEquals(m2.size(), 2);

    m2.decode();
    assertTrue(m2.isDecoded());

    long value;
    assertEquals(m2.size(), 2);

    value = m2.get(3);
    assertTrue(value == Integer.MAX_VALUE + 5L);
    assertTrue(value > 0);

    value = m2.remove(3);
    assertEquals(m2.size(), 1);

    value = m2.get(4);
    assertTrue(value == Integer.MAX_VALUE + 22L);
    assertTrue(value > 0);

    HMapILW.setLazyDecodeFlag(false);
  }

  @Test
  public void testSerializeEmpty() throws IOException {
    HMapILW m1 = new HMapILW();

    // make sure this does nothing
    m1.decode();

    assertTrue(m1.size() == 0);

    HMapIFW m2 = HMapIFW.create(m1.serialize());

    assertTrue(m2.size() == 0);
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(HMapILWTest.class);
  }

}
