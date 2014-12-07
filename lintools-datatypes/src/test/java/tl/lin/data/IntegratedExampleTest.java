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

package tl.lin.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

import tl.lin.data.array.ArrayListWritable;
import tl.lin.data.array.IntArrayWritable;
import tl.lin.data.map.HMapIVW;
import tl.lin.data.pair.PairOfInts;
import tl.lin.data.pair.PairOfStringInt;
import tl.lin.data.pair.PairOfStrings;
import tl.lin.data.pair.PairOfWritables;

public class IntegratedExampleTest {

  @Test
  public void testBasic() throws IOException {
    PairOfWritables<PairOfStringInt, IntArrayWritable> data =
        new PairOfWritables<PairOfStringInt, IntArrayWritable>(
            new PairOfStringInt("foo", 42),
            new IntArrayWritable(new int[] {1, 2, 3, 4, 5}));

    //System.out.println(data);
    ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
    DataOutputStream dataOut = new DataOutputStream(bytesOut);
    data.write(dataOut);

    PairOfWritables<PairOfStringInt, IntArrayWritable> reconstructed =
        new PairOfWritables<PairOfStringInt, IntArrayWritable>();
    reconstructed.readFields(new DataInputStream(new ByteArrayInputStream(bytesOut.toByteArray())));

    assertEquals(data.getLeftElement(), reconstructed.getLeftElement());
    assertTrue(Arrays.equals(data.getRightElement().getArray(), reconstructed.getRightElement().getArray()));
  }

  @Test
  public void testBasic2() throws IOException {
    ArrayListWritable<PairOfInts> data = new ArrayListWritable<PairOfInts>();
    data.add(new PairOfInts(1, 2));
    data.add(new PairOfInts(3, 4));
    data.add(new PairOfInts(5, 6));
    data.add(new PairOfInts(7, 8));

    //System.out.println(data);
    ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
    DataOutputStream dataOut = new DataOutputStream(bytesOut);
    data.write(dataOut);

    ArrayListWritable<PairOfInts> reconstructed = new ArrayListWritable<PairOfInts>();
    reconstructed.readFields(new DataInputStream(new ByteArrayInputStream(bytesOut.toByteArray())));

    for (int i = 0; i < data.size(); i++) {
      assertEquals(data.get(i), reconstructed.get(i));
    }
  }

  @Test
  public void testBasic3() throws IOException {
    HMapIVW<PairOfStrings> data = new HMapIVW<PairOfStrings>();
    data.put(1, new PairOfStrings("a", "b"));
    data.put(2, new PairOfStrings("foo", "bar"));
    data.put(4, new PairOfStrings("alpha", "beta"));
    data.put(42, new PairOfStrings("four", "two"));

    //System.out.println(data);
    ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
    DataOutputStream dataOut = new DataOutputStream(bytesOut);
    data.write(dataOut);

    HMapIVW<PairOfStrings> reconstructed = new HMapIVW<PairOfStrings>();
    reconstructed.readFields(new DataInputStream(new ByteArrayInputStream(bytesOut.toByteArray())));

    for (int i = 0; i < data.size(); i++) {
      assertEquals(data.get(i), reconstructed.get(i));
    }
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(IntegratedExampleTest.class);
  }

}
