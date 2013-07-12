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

package tl.lin.data.array;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

public class FloatArrayWritableTest {

  @Test
  public void testBasic1() throws IOException {
    FloatArrayWritable array1 = new FloatArrayWritable(new float[] {1, 3, 53, 97, 286});
    assertEquals(1, array1.get(0), 10e-6);
    assertEquals(3, array1.get(1), 10e-6);
    assertEquals(53, array1.get(2), 10e-6);
    assertEquals(97, array1.get(3), 10e-6);
    assertEquals(286, array1.get(4), 10e-6);
    assertEquals(5, array1.size());

    FloatArrayWritable array2 = new FloatArrayWritable(new float[] {1, 3, 53, 97, 286}, 3);
    assertEquals(1, array2.get(0), 10e-6);
    assertEquals(3, array2.get(1), 10e-6);
    assertEquals(53, array2.get(2), 10e-6);
    assertEquals(3, array2.size());

    FloatArrayWritable array3 = new FloatArrayWritable(new float[] {});
    assertEquals(0, array3.size());
  }

  @Test
  public void testBasic2() throws IOException {
    FloatArrayWritable array = new FloatArrayWritable();

    assertEquals(0, array.size());

    float[] list1 = new float[] {1, 3, 53, 97, 286};
    array.setArray(list1);

    assertEquals(1, array.get(0), 10e-6);
    assertEquals(3, array.get(1), 10e-6);
    assertEquals(53, array.get(2), 10e-6);
    assertEquals(97, array.get(3), 10e-6);
    assertEquals(286, array.get(4), 10e-6);
    assertEquals(list1.length, array.size());

    array.set(0, 2);
    array.set(3, -5);

    assertEquals(2, array.get(0), 10e-6);
    assertEquals(-5, array.get(3), 10e-6);

    float[] list2 = new float[] {9, -1, 6, 22, 76};
    array.setArray(list2, 3);

    assertEquals(9, array.get(0), 10e-6);
    assertEquals(-1, array.get(1), 10e-6);
    assertEquals(6, array.get(2), 10e-6);
    assertEquals(3, array.size(), 10e-6);

    array.setArray(null);
    assertEquals(0, array.size());
    assertEquals(0, array.getArray().length);
  }

  @Test
  public void testSerialize1() throws IOException {
    FloatArrayWritable array = new FloatArrayWritable(new float[] {1, 3, 53, 97, 286});

    ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
    DataOutputStream dataOut = new DataOutputStream(bytesOut);

    array.write(dataOut);

    FloatArrayWritable array2 = new FloatArrayWritable();
    array2.readFields(new DataInputStream(new ByteArrayInputStream(
        bytesOut.toByteArray())));

    assertEquals(5, array2.size(), 10e-6);
    assertEquals(1, array2.get(0), 10e-6);
    assertEquals(3, array2.get(1), 10e-6);
    assertEquals(53, array2.get(2), 10e-6);
    assertEquals(97, array2.get(3), 10e-6);
    assertEquals(286, array2.get(4), 10e-6);
  }

  @Test
  public void testSerialize2() throws IOException {
    FloatArrayWritable array = new FloatArrayWritable(new float[] {1, 3, 53, 97, 286}, 4);

    ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
    DataOutputStream dataOut = new DataOutputStream(bytesOut);

    array.write(dataOut);

    FloatArrayWritable array2 = new FloatArrayWritable();
    array2.readFields(new DataInputStream(new ByteArrayInputStream(
        bytesOut.toByteArray())));

    assertEquals(4, array2.size());
    assertEquals(1, array2.get(0), 10e-6);
    assertEquals(3, array2.get(1), 10e-6);
    assertEquals(53, array2.get(2), 10e-6);
    assertEquals(97, array2.get(3), 10e-6);
  }

  @Test
  public void testSerialize3() throws IOException {
    FloatArrayWritable array = new FloatArrayWritable(new float[] {1, 2, 3, 4, 5});

    ByteArrayOutputStream bytesOut1 = new ByteArrayOutputStream();
    DataOutputStream dataOut1 = new DataOutputStream(bytesOut1);

    array.write(dataOut1);

    FloatArrayWritable array2 = new FloatArrayWritable();
    array2.readFields(new DataInputStream(new ByteArrayInputStream(
        bytesOut1.toByteArray())));

    assertEquals(5, array2.size());
    assertEquals(1, array2.get(0), 10e-6);
    assertEquals(2, array2.get(1), 10e-6);
    assertEquals(3, array2.get(2), 10e-6);
    assertEquals(4, array2.get(3), 10e-6);
    assertEquals(5, array2.get(4), 10e-6);

    array.setArray(new float[] {6, 7});

    ByteArrayOutputStream bytesOut2 = new ByteArrayOutputStream();
    DataOutputStream dataOut2 = new DataOutputStream(bytesOut2);

    array.write(dataOut2);

    FloatArrayWritable array3 = new FloatArrayWritable();
    array3.readFields(new DataInputStream(new ByteArrayInputStream(
        bytesOut2.toByteArray())));

    assertEquals(2, array3.size());
    assertEquals(6, array3.get(0), 10e-6);
    assertEquals(7, array3.get(1), 10e-6);
  }

  @Test
  public void testSerializeEmpty() throws IOException {
    FloatArrayWritable array = new FloatArrayWritable();

    ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
    DataOutputStream dataOut = new DataOutputStream(bytesOut);

    array.write(dataOut);

    FloatArrayWritable array2 = new FloatArrayWritable();
    array2.readFields(new DataInputStream(new ByteArrayInputStream(
        bytesOut.toByteArray())));

    assertEquals(0, array2.size());
    assertTrue(array2.getArray() != null);
    assertEquals(0, array2.getArray().length);
  }

  @Test
  public void testClone1() throws IOException {
    FloatArrayWritable array = new FloatArrayWritable(new float[] {1, 3, 53, 97, 286});
    assertEquals(1, array.get(0), 10e-6);
    assertEquals(3, array.get(1), 10e-6);
    assertEquals(53, array.get(2), 10e-6);
    assertEquals(97, array.get(3), 10e-6);
    assertEquals(286, array.get(4), 10e-6);
    assertEquals(5, array.size());

    float[] p = array.getClone();
    p[0] = 8;
    p[1] = 7;
    p[2] = -4;

    assertEquals(1, array.get(0), 10e-6);
    assertEquals(3, array.get(1), 10e-6);
    assertEquals(53, array.get(2), 10e-6);
    assertEquals(5, array.size());
  }

  @Test
  public void testClone2() throws IOException {
    FloatArrayWritable array = new FloatArrayWritable(new float[] {1, 3, 53, 97, 286}, 2);
    assertEquals(1, array.get(0), 10e-6);
    assertEquals(3, array.get(1), 10e-6);
    assertEquals(2, array.size());

    float[] p = array.getClone();
    assertEquals(2, p.length);
    p[0] = 8;
    p[1] = 7;

    assertEquals(1, array.get(0), 10e-6);
    assertEquals(3, array.get(1), 10e-6);
    assertEquals(2, array.size());
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(FloatArrayWritableTest.class);
  }
}
