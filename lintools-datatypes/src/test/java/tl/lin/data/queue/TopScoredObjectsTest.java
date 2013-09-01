/*
 * Cloud9: A MapReduce Library for Hadoop
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

import java.util.Random;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

import tl.lin.data.pair.PairOfObjectFloat;

public class TopScoredObjectsTest {

  @Test
  public void testRandom() {
    Random random = new Random();

    TopScoredObjects<String> set = new TopScoredObjects<String>(10);
    for (int i = 0; i < 10000; i++) {
      set.add(""+random.nextInt(Integer.MAX_VALUE), random.nextFloat());
    }

    assertEquals(10, set.getMaxElements());
    assertEquals(10, set.size());

    PairOfObjectFloat<String>[] arr = set.extractAll();
    assertEquals(10, arr.length);
    for ( int i = 0; i<arr.length-1; i++) {
      assertTrue(arr[i].getRightElement() > arr[i+1].getRightElement());
    }
  }

  @Test
  public void testBasic1() {
    TopScoredObjects<String> set = new TopScoredObjects<String>(5);

    set.add("1", 0);
    set.add("a", 5);
    set.add("b", 4);
    set.add("c", 6);
    set.add("d", 1);
    set.add("e", 199);
    set.add("2", -31);

    PairOfObjectFloat<String>[] arr = set.extractAll();

    assertEquals(5, arr.length);
    assertEquals("e", arr[0].getLeftElement());
    assertEquals(199, arr[0].getRightElement(), 10e-6);
    assertEquals("c", arr[1].getLeftElement());
    assertEquals(6, arr[1].getRightElement(), 10e-6);
    assertEquals("a", arr[2].getLeftElement());
    assertEquals(5, arr[2].getRightElement(), 10e-6);
    assertEquals("b", arr[3].getLeftElement());
    assertEquals(4, arr[3].getRightElement(), 10e-6);
    assertEquals("d", arr[4].getLeftElement());
    assertEquals(1, arr[4].getRightElement(), 10e-6);
  }

  @Test
  public void testBasic2() {
    TopScoredObjects<String> set = new TopScoredObjects<String>(5);

    set.add("a", 5);
    set.add("z", 5);
    set.add("b", 4);
    set.add("c", 6);
    set.add("d", 1);
    set.add("e", 1);
    // "e" should get preserved.

    PairOfObjectFloat<String>[] arr = set.extractAll();

    assertEquals(5, arr.length);
    assertEquals("c", arr[0].getLeftElement());
    assertEquals(6, arr[0].getRightElement(), 10e-6);
    assertEquals("z", arr[1].getLeftElement());
    assertEquals(5, arr[1].getRightElement(), 10e-6);
    assertEquals("a", arr[2].getLeftElement());
    assertEquals(5, arr[2].getRightElement(), 10e-6);
    assertEquals("b", arr[3].getLeftElement());
    assertEquals(4, arr[3].getRightElement(), 10e-6);
    assertEquals("e", arr[4].getLeftElement());
    assertEquals(1, arr[4].getRightElement(), 10e-6);
  }

  @Test
  public void testBasic3() {
    TopScoredObjects<String> set = new TopScoredObjects<String>(5);
    // What if # objects is less than size of queue?
    set.add("a", 5);
    set.add("b", 4);
    set.add("c", 6);

    assertEquals(5, set.getMaxElements());
    assertEquals(3, set.size());
    PairOfObjectFloat<String>[] arr = set.extractAll();

    assertEquals(3, arr.length);
    assertEquals("c", arr[0].getLeftElement());
    assertEquals(6, arr[0].getRightElement(), 10e-6);
    assertEquals("a", arr[1].getLeftElement());
    assertEquals(5, arr[1].getRightElement(), 10e-6);
    assertEquals("b", arr[2].getLeftElement());
    assertEquals(4, arr[2].getRightElement(), 10e-6);
  }

  @Test
  public void testTieBreaking() {
    TopScoredObjects<String> set = new TopScoredObjects<String>(5);

    set.add("a", 1.0f);
    set.add("b", 1.0f);
    set.add("c", 1.0f);
    set.add("d", 1.0f);
    set.add("e", 1.0f);
    set.add("f", 1.0f);
    set.add("g", 1.0f);

    PairOfObjectFloat<String>[] arr = set.extractAll();

    assertEquals(5, arr.length);
    assertEquals("g", arr[0].getLeftElement());
    assertEquals(1.0f, arr[0].getRightElement(), 10e-6);
    assertEquals("f", arr[1].getLeftElement());
    assertEquals(1.0f, arr[1].getRightElement(), 10e-6);
    assertEquals("e", arr[2].getLeftElement());
    assertEquals(1.0f, arr[2].getRightElement(), 10e-6);
    assertEquals("d", arr[3].getLeftElement());
    assertEquals(1.0f, arr[3].getRightElement(), 10e-6);
    assertEquals("c", arr[4].getLeftElement());
    assertEquals(1.0f, arr[4].getRightElement(), 10e-6);
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(TopScoredObjectsTest.class);
  }
}