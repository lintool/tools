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

package tl.lin.data.fd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import tl.lin.data.fd.SortableEntries.Order;
import tl.lin.data.pair.PairOfIntLong;

public class Int2LongFrequencyDistributionTestBase {
  protected void test1Common(Int2LongFrequencyDistribution fd) {
    assertEquals(0, fd.get(1));

    fd.increment(1);
    fd.increment(2);
    fd.increment(3);
    fd.increment(2);
    fd.increment(3);
    fd.increment(3);

    assertEquals(3, fd.getNumberOfEvents());
    assertEquals(6, fd.getSumOfCounts());

    assertEquals(1, fd.get(1));
    assertEquals(2, fd.get(2));
    assertEquals(3, fd.get(3));

    assertEquals((double) 1 / 6, fd.computeRelativeFrequency(1), 10e-6);
    assertEquals((double) 2 / 6, fd.computeRelativeFrequency(2), 10e-6);
    assertEquals((double) 3 / 6, fd.computeRelativeFrequency(3), 10e-6);

    assertEquals(Math.log((double) 1 / 6), fd.computeLogRelativeFrequency(1), 10e-6);
    assertEquals(Math.log((double) 2 / 6), fd.computeLogRelativeFrequency(2), 10e-6);
    assertEquals(Math.log((double) 3 / 6), fd.computeLogRelativeFrequency(3), 10e-6);

    fd.decrement(3);

    assertEquals(3, fd.getNumberOfEvents());
    assertEquals(5, fd.getSumOfCounts());

    assertEquals(1, fd.get(1));
    assertEquals(2, fd.get(2));
    assertEquals(2, fd.get(3));

    assertEquals((double) 1 / 5, fd.computeRelativeFrequency(1), 10e-6);
    assertEquals((double) 2 / 5, fd.computeRelativeFrequency(2), 10e-6);
    assertEquals((double) 2 / 5, fd.computeRelativeFrequency(3), 10e-6);

    assertEquals(Math.log((double) 1 / 5), fd.computeLogRelativeFrequency(1), 10e-6);
    assertEquals(Math.log((double) 2 / 5), fd.computeLogRelativeFrequency(2), 10e-6);
    assertEquals(Math.log((double) 2 / 5), fd.computeLogRelativeFrequency(3), 10e-6);

    fd.decrement(1);

    assertEquals(2, fd.getNumberOfEvents());
    assertEquals(4, fd.getSumOfCounts());

    assertEquals(0, fd.get(1));
    assertEquals(2, fd.get(2));
    assertEquals(2, fd.get(3));

    assertEquals((double) 2 / 4, fd.computeRelativeFrequency(2), 10e-6);
    assertEquals((double) 2 / 4, fd.computeRelativeFrequency(3), 10e-6);

    assertEquals(Math.log((double) 2 / 4), fd.computeLogRelativeFrequency(2), 10e-6);
    assertEquals(Math.log((double) 2 / 4), fd.computeLogRelativeFrequency(3), 10e-6);
  }

  protected void test2Common(Int2LongFrequencyDistribution fd) {
    fd.increment(1);
    fd.increment(1);
    fd.increment(2);
    fd.increment(3);

    assertEquals(3, fd.getNumberOfEvents());
    assertEquals(4, fd.getSumOfCounts());

    assertEquals(2, fd.get(1));
    assertEquals(1, fd.get(2));
    assertEquals(1, fd.get(3));

    fd.set(4, 5);

    assertEquals(4, fd.getNumberOfEvents());
    assertEquals(9, fd.getSumOfCounts());

    assertEquals(2, fd.get(1));
    assertEquals(1, fd.get(2));
    assertEquals(1, fd.get(3));
    assertEquals(5, fd.get(4));

    fd.set(1, 5);

    assertEquals(4, fd.getNumberOfEvents());
    assertEquals(12, fd.getSumOfCounts());

    assertEquals(5, fd.get(1));
    assertEquals(1, fd.get(2));
    assertEquals(1, fd.get(3));
    assertEquals(5, fd.get(4));

    fd.increment(3);
    fd.increment(3);
    fd.increment(3);

    assertEquals(4, fd.getNumberOfEvents());
    assertEquals(15, fd.getSumOfCounts());

    assertEquals(5, fd.get(1));
    assertEquals(1, fd.get(2));
    assertEquals(4, fd.get(3));
    assertEquals(5, fd.get(4));

    fd.set(3, 1);

    assertEquals(4, fd.getNumberOfEvents());
    assertEquals(12, fd.getSumOfCounts());

    assertEquals(5, fd.get(1));
    assertEquals(1, fd.get(2));
    assertEquals(1, fd.get(3));
    assertEquals(5, fd.get(4));
  }

  protected void test3Common(Int2LongFrequencyDistribution fd) {
    fd.increment(1);
    fd.increment(1);
    fd.increment(2);
    fd.increment(3);

    assertEquals(3, fd.getNumberOfEvents());
    assertEquals(4, fd.getSumOfCounts());

    assertEquals(2, fd.get(1));
    assertEquals(1, fd.get(2));
    assertEquals(1, fd.get(3));

    fd.clear();
    assertEquals(0, fd.getNumberOfEvents());
    assertEquals(0, fd.getSumOfCounts());
  }

  protected void testFailedDecrement1Common(Int2LongFrequencyDistribution fd) {
    fd.increment(1);

    assertEquals(1, fd.getNumberOfEvents());
    assertEquals(1, fd.getSumOfCounts());
    assertEquals(1, fd.get(1));

    fd.decrement(1);

    assertEquals(0, fd.getNumberOfEvents());
    assertEquals(0, fd.getSumOfCounts());
    assertEquals(0, fd.get(1));

    fd.decrement(1);
  }

  protected void testFailedDecrement2Common(Int2LongFrequencyDistribution fd) {
    fd.increment(1, 1000);

    assertEquals(1, fd.getNumberOfEvents());
    assertEquals(1000, fd.getSumOfCounts());
    assertEquals(1000, fd.get(1));

    fd.decrement(1, 997);

    assertEquals(1, fd.getNumberOfEvents());
    assertEquals(3, fd.getSumOfCounts());
    assertEquals(3, fd.get(1));

    fd.decrement(1, 3);

    assertEquals(0, fd.getNumberOfEvents());
    assertEquals(0, fd.getSumOfCounts());
    assertEquals(0, fd.get(1));

    fd.increment(1, 3);
    fd.decrement(1, 4);
  }

  protected void testMultiIncrementDecrementCommon(Int2LongFrequencyDistribution fd) {
    fd.increment(1, 2);
    fd.increment(2, 3);
    fd.increment(3, 4);
    fd.increment(4, Integer.MAX_VALUE + 1L);

    assertEquals(4, fd.getNumberOfEvents());
    assertEquals(Integer.MAX_VALUE + 10L, fd.getSumOfCounts());

    assertEquals(2, fd.get(1));
    assertEquals(3, fd.get(2));
    assertEquals(4, fd.get(3));

    fd.decrement(2, 2);

    assertEquals(4, fd.getNumberOfEvents());
    assertEquals(Integer.MAX_VALUE + 8L, fd.getSumOfCounts());

    assertEquals(2, fd.get(1));
    assertEquals(1, fd.get(2));
    assertEquals(4, fd.get(3));
    assertEquals(Integer.MAX_VALUE + 1L, fd.get(4));
  }

  protected void testGetFrequencySortedEventsCommon(Int2LongFrequencyDistribution fd) {
    fd.set(1, 5L);
    fd.set(4, 2L);
    fd.set(2, 5L);
    fd.set(5, 2L);
    fd.set(6, 1L);
    fd.set(3, 5L);

    assertEquals(6, fd.getNumberOfEvents());
    assertEquals(20, fd.getSumOfCounts());

    List<PairOfIntLong> list = fd.getEntries(Order.ByRightElementDescending);

    assertEquals(6, list.size());

    assertEquals(1, list.get(0).getLeftElement());
    assertEquals(5, list.get(0).getRightElement());
    assertEquals(2, list.get(1).getLeftElement());
    assertEquals(5, list.get(1).getRightElement());
    assertEquals(3, list.get(2).getLeftElement());
    assertEquals(5, list.get(2).getRightElement());
    assertEquals(4, list.get(3).getLeftElement());
    assertEquals(2, list.get(3).getRightElement());
    assertEquals(5, list.get(4).getLeftElement());
    assertEquals(2, list.get(4).getRightElement());
    assertEquals(6, list.get(5).getLeftElement());
    assertEquals(1, list.get(5).getRightElement());

    list = fd.getEntries(Order.ByRightElementAscending);

    assertEquals(6, list.size());

    assertEquals(6, list.get(0).getLeftElement());
    assertEquals(1, list.get(0).getRightElement());
    assertEquals(4, list.get(1).getLeftElement());
    assertEquals(2, list.get(1).getRightElement());
    assertEquals(5, list.get(2).getLeftElement());
    assertEquals(2, list.get(2).getRightElement());
    assertEquals(1, list.get(3).getLeftElement());
    assertEquals(5, list.get(3).getRightElement());
    assertEquals(2, list.get(4).getLeftElement());
    assertEquals(5, list.get(4).getRightElement());
    assertEquals(3, list.get(5).getLeftElement());
    assertEquals(5, list.get(5).getRightElement());

    list = fd.getEntries(Order.ByRightElementDescending, 4);

    assertEquals(4, list.size());

    assertEquals(1, list.get(0).getLeftElement());
    assertEquals(5, list.get(0).getRightElement());
    assertEquals(2, list.get(1).getLeftElement());
    assertEquals(5, list.get(1).getRightElement());
    assertEquals(3, list.get(2).getLeftElement());
    assertEquals(5, list.get(2).getRightElement());
    assertEquals(4, list.get(3).getLeftElement());
    assertEquals(2, list.get(3).getRightElement());

    list = fd.getEntries(Order.ByRightElementAscending, 4);

    assertEquals(4, list.size());

    assertEquals(6, list.get(0).getLeftElement());
    assertEquals(1, list.get(0).getRightElement());
    assertEquals(4, list.get(1).getLeftElement());
    assertEquals(2, list.get(1).getRightElement());
    assertEquals(5, list.get(2).getLeftElement());
    assertEquals(2, list.get(2).getRightElement());
    assertEquals(1, list.get(3).getLeftElement());
    assertEquals(5, list.get(3).getRightElement());
  }

  protected void testGetSortedEventsCommon(Int2LongFrequencyDistribution fd) {
    fd.set(1, 1L);
    fd.set(4, 3L);
    fd.set(2, 4L);
    fd.set(5, 7L);
    fd.set(6, 9L);
    fd.set(3, 2L);

    assertEquals(6, fd.getNumberOfEvents());
    assertEquals(26, fd.getSumOfCounts());

    List<PairOfIntLong> list = fd.getEntries(Order.ByLeftElementAscending);

    assertEquals(6, list.size());

    assertEquals(1, list.get(0).getLeftElement());
    assertEquals(1, list.get(0).getRightElement());
    assertEquals(2, list.get(1).getLeftElement());
    assertEquals(4, list.get(1).getRightElement());
    assertEquals(3, list.get(2).getLeftElement());
    assertEquals(2, list.get(2).getRightElement());
    assertEquals(4, list.get(3).getLeftElement());
    assertEquals(3, list.get(3).getRightElement());
    assertEquals(5, list.get(4).getLeftElement());
    assertEquals(7, list.get(4).getRightElement());
    assertEquals(6, list.get(5).getLeftElement());
    assertEquals(9, list.get(5).getRightElement());

    list = fd.getEntries(Order.ByLeftElementDescending);

    assertEquals(6, list.size());

    assertEquals(6, list.get(0).getLeftElement());
    assertEquals(9, list.get(0).getRightElement());
    assertEquals(5, list.get(1).getLeftElement());
    assertEquals(7, list.get(1).getRightElement());
    assertEquals(4, list.get(2).getLeftElement());
    assertEquals(3, list.get(2).getRightElement());
    assertEquals(3, list.get(3).getLeftElement());
    assertEquals(2, list.get(3).getRightElement());
    assertEquals(2, list.get(4).getLeftElement());
    assertEquals(4, list.get(4).getRightElement());
    assertEquals(1, list.get(5).getLeftElement());
    assertEquals(1, list.get(5).getRightElement());

    list = fd.getEntries(Order.ByLeftElementAscending, 4);

    assertEquals(4, list.size());

    assertEquals(1, list.get(0).getLeftElement());
    assertEquals(1, list.get(0).getRightElement());
    assertEquals(2, list.get(1).getLeftElement());
    assertEquals(4, list.get(1).getRightElement());
    assertEquals(3, list.get(2).getLeftElement());
    assertEquals(2, list.get(2).getRightElement());
    assertEquals(4, list.get(3).getLeftElement());
    assertEquals(3, list.get(3).getRightElement());

    list = fd.getEntries(Order.ByLeftElementDescending, 4);

    assertEquals(4, list.size());

    assertEquals(6, list.get(0).getLeftElement());
    assertEquals(9, list.get(0).getRightElement());
    assertEquals(5, list.get(1).getLeftElement());
    assertEquals(7, list.get(1).getRightElement());
    assertEquals(4, list.get(2).getLeftElement());
    assertEquals(3, list.get(2).getRightElement());
    assertEquals(3, list.get(3).getLeftElement());
    assertEquals(2, list.get(3).getRightElement());
  }

  protected void testIterableCommon(Int2LongFrequencyDistribution fd) {
    fd.set(1, 1L);
    fd.set(4, 3L);
    fd.set(2, 4L);
    fd.set(5, 7L);
    fd.set(6, 9L);
    fd.set(3, 2L);

    assertEquals(6, fd.getNumberOfEvents());
    assertEquals(26, fd.getSumOfCounts());

    SortedSet<PairOfIntLong> list = new TreeSet<PairOfIntLong>();

    for (PairOfIntLong pair : fd) {
      list.add(pair.clone());
    }

    assertEquals(6, list.size());

    Iterator<PairOfIntLong> iter = list.iterator();
    PairOfIntLong e = iter.next();
    assertEquals(1, e.getLeftElement());
    assertEquals(1, e.getRightElement());
    e = iter.next();
    assertEquals(2, e.getLeftElement());
    assertEquals(4, e.getRightElement());
    e = iter.next();
    assertEquals(3, e.getLeftElement());
    assertEquals(2, e.getRightElement());
    e = iter.next();
    assertEquals(4, e.getLeftElement());
    assertEquals(3, e.getRightElement());
    e = iter.next();
    assertEquals(5, e.getLeftElement());
    assertEquals(7, e.getRightElement());
    e = iter.next();
    assertEquals(6, e.getLeftElement());
    assertEquals(9, e.getRightElement());
  }

  protected void testSerialization(Int2LongFrequencyDistribution fd,
      Class<? extends Int2LongFrequencyDistribution> cls) throws Exception {
    fd.set(1, Integer.MAX_VALUE + 1L);
    fd.set(4, Integer.MAX_VALUE + 3L);
    fd.set(2, Integer.MAX_VALUE + 4L);
    fd.set(5, Integer.MAX_VALUE + 7L);
    fd.set(6, Integer.MAX_VALUE + 9L);
    fd.set(3, Integer.MAX_VALUE + 2L);

    assertEquals(6, fd.getNumberOfEvents());
    assertEquals(Integer.MAX_VALUE * 6L + 26L, fd.getSumOfCounts());

    ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
    DataOutputStream dataOut = new DataOutputStream(bytesOut);
    fd.write(dataOut);

    Int2LongFrequencyDistribution reconstructed = cls.newInstance();
    reconstructed.readFields(new DataInputStream(new ByteArrayInputStream(bytesOut.toByteArray())));

    assertFalse(fd == reconstructed);
    assertEquals(Integer.MAX_VALUE + 1L, reconstructed.get(1));
    assertEquals(Integer.MAX_VALUE + 3L, reconstructed.get(4));
    assertEquals(Integer.MAX_VALUE + 4L, reconstructed.get(2));
    assertEquals(Integer.MAX_VALUE + 7L, reconstructed.get(5));
    assertEquals(Integer.MAX_VALUE + 9L, reconstructed.get(6));
    assertEquals(Integer.MAX_VALUE + 2L, reconstructed.get(3));

    assertEquals(6, reconstructed.getNumberOfEvents());
    assertEquals(Integer.MAX_VALUE * 6L + 26L, reconstructed.getSumOfCounts());
  }
}
