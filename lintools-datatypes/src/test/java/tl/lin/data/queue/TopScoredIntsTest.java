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

package tl.lin.data.queue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Random;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

import tl.lin.data.pair.PairOfIntFloat;

public class TopScoredIntsTest {

  @Test
  public void testRandom() {
    Random random = new Random();

    TopScoredInts set = new TopScoredInts(10);
    for (int i = 0; i < 10000; i++) {
      set.add(random.nextInt(Integer.MAX_VALUE), random.nextFloat());
    }

    assertEquals(10, set.getMaxElements());
    assertEquals(10, set.size());

    PairOfIntFloat[] arr = set.extractAll();
    assertEquals(10, arr.length);
    for ( int i = 0; i<arr.length-1; i++) {
      assertTrue(arr[i].getValue() > arr[i+1].getValue());
    }
  }

  @Test
  public void testBasic1() {
    TopScoredInts set = new TopScoredInts(5);

    set.add(1, 0);
    set.add(2, 5);
    set.add(3, 4);
    set.add(4, 6);
    set.add(5, 1);
    set.add(6, 199);
    set.add(7, -31);

    PairOfIntFloat[] arr = set.extractAll();

    assertEquals(5, arr.length);
    assertEquals(6, arr[0].getLeftElement());
    assertEquals(199, arr[0].getRightElement(), 10e-6);
    assertEquals(4, arr[1].getLeftElement());
    assertEquals(6, arr[1].getRightElement(), 10e-6);
    assertEquals(2, arr[2].getLeftElement());
    assertEquals(5, arr[2].getRightElement(), 10e-6);
    assertEquals(3, arr[3].getLeftElement());
    assertEquals(4, arr[3].getRightElement(), 10e-6);
    assertEquals(5, arr[4].getLeftElement());
    assertEquals(1, arr[4].getRightElement(), 10e-6);
  }

  @Test
  public void testBasic2() {
    TopScoredInts set = new TopScoredInts(5);

    set.add(1, 5);
    set.add(2, 5);
    set.add(3, 4);
    set.add(4, 6);
    set.add(5, 1);
    set.add(6, 1);
    // 6 should get preserved.

    PairOfIntFloat[] arr = set.extractAll();

    assertEquals(5, arr.length);
    assertEquals(4, arr[0].getLeftElement());
    assertEquals(6, arr[0].getRightElement(), 10e-6);
    assertEquals(2, arr[1].getLeftElement());
    assertEquals(5, arr[2].getRightElement(), 10e-6);
    assertEquals(1, arr[2].getLeftElement());
    assertEquals(5, arr[2].getRightElement(), 10e-6);
    assertEquals(3, arr[3].getLeftElement());
    assertEquals(4, arr[3].getRightElement(), 10e-6);
    assertEquals(6, arr[4].getLeftElement());
    assertEquals(1, arr[4].getRightElement(), 10e-6);
  }

  @Test
  public void testBasic3() {
    TopScoredInts set = new TopScoredInts(5);
    // What if # objects is less than size of queue?
    set.add(1, 5);
    set.add(2, 4);
    set.add(3, 6);

    assertEquals(5, set.getMaxElements());
    assertEquals(3, set.size());
    PairOfIntFloat[] arr = set.extractAll();

    assertEquals(3, arr.length);
    assertEquals(3, arr[0].getLeftElement());
    assertEquals(6, arr[0].getRightElement(), 10e-6);
    assertEquals(1, arr[1].getLeftElement());
    assertEquals(5, arr[1].getRightElement(), 10e-6);
    assertEquals(2, arr[2].getLeftElement());
    assertEquals(4, arr[2].getRightElement(), 10e-6);
  }

  @Test
  public void testTieBreaking() {
    TopScoredInts set = new TopScoredInts(5);

    set.add(1, 1.0f);
    set.add(2, 1.0f);
    set.add(3, 1.0f);
    set.add(4, 1.0f);
    set.add(5, 1.0f);
    set.add(6, 1.0f);
    set.add(7, 1.0f);

    PairOfIntFloat[] arr = set.extractAll();

    assertEquals(5, arr.length);
    assertEquals(7, arr[0].getLeftElement());
    assertEquals(1.0f, arr[0].getRightElement(), 10e-6);
    assertEquals(6, arr[1].getLeftElement());
    assertEquals(1.0f, arr[1].getRightElement(), 10e-6);
    assertEquals(5, arr[2].getLeftElement());
    assertEquals(1.0f, arr[2].getRightElement(), 10e-6);
    assertEquals(4, arr[3].getLeftElement());
    assertEquals(1.0f, arr[3].getRightElement(), 10e-6);
    assertEquals(3, arr[4].getLeftElement());
    assertEquals(1.0f, arr[4].getRightElement(), 10e-6);
  }

  @Test
  public void testSerialization() throws Exception {
    TopScoredInts set = new TopScoredInts(5);

    set.add(1, 5);
    set.add(2, 5);
    set.add(3, 4);
    set.add(4, 6);
    set.add(5, 1);
    set.add(6, 1);
    // 6 should get preserved.

    assertEquals(5, set.getMaxElements());
    assertEquals(5, set.size());

    ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
    DataOutputStream dataOut = new DataOutputStream(bytesOut);
    set.write(dataOut);

    TopScoredInts reconstructed = new TopScoredInts();
    reconstructed.readFields(new DataInputStream(new ByteArrayInputStream(bytesOut.toByteArray())));

    // Note that serialization is destructive.
    assertEquals(5, set.getMaxElements());
    assertEquals(0, set.size());

    assertEquals(5, reconstructed.getMaxElements());
    assertEquals(5, reconstructed.size());

    PairOfIntFloat[] arr = reconstructed.extractAll();

    assertEquals(5, arr.length);
    assertEquals(4, arr[0].getLeftElement());
    assertEquals(6, arr[0].getRightElement(), 10e-6);
    assertEquals(2, arr[1].getLeftElement());
    assertEquals(5, arr[2].getRightElement(), 10e-6);
    assertEquals(1, arr[2].getLeftElement());
    assertEquals(5, arr[2].getRightElement(), 10e-6);
    assertEquals(3, arr[3].getLeftElement());
    assertEquals(4, arr[3].getRightElement(), 10e-6);
    assertEquals(6, arr[4].getLeftElement());
    assertEquals(1, arr[4].getRightElement(), 10e-6);
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(TopScoredIntsTest.class);
  }
}