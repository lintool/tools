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

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

public class Int2LongFrequencyDistributionEntryTest extends Int2LongFrequencyDistributionTestBase {

  @Test
  public void test1Entry() {
    Int2LongFrequencyDistribution fd = new Int2LongFrequencyDistributionEntry();
    test1Common(fd);
  }

  @Test
  public void test2Entry() {
    Int2LongFrequencyDistribution fd = new Int2LongFrequencyDistributionEntry();
    test2Common(fd);
  }

  @Test
  public void test3Entry() {
    Int2LongFrequencyDistribution fd = new Int2LongFrequencyDistributionEntry();
    test3Common(fd);
  }

  @Test(expected = RuntimeException.class)
  public void testFailedDecrement1Entry() {
    Int2LongFrequencyDistribution fd = new Int2LongFrequencyDistributionEntry();
    testFailedDecrement1Common(fd);
  }

  @Test(expected = RuntimeException.class)
  public void testFailedDecrement2Entry() {
    Int2LongFrequencyDistribution fd = new Int2LongFrequencyDistributionEntry();
    testFailedDecrement2Common(fd);
  }

  @Test
  public void testMultiIncrementDecrementEntry() {
    Int2LongFrequencyDistribution fd = new Int2LongFrequencyDistributionEntry();
    testMultiIncrementDecrementCommon(fd);
  }

  @Test
  public void testGetFrequencySortedEventsEntry() {
    Int2LongFrequencyDistribution fd = new Int2LongFrequencyDistributionEntry();
    testGetFrequencySortedEventsCommon(fd);
  }

  @Test
  public void testGetSortedEventsEntry() {
    Int2LongFrequencyDistribution fd = new Int2LongFrequencyDistributionEntry();
    testGetSortedEventsCommon(fd);
  }

  @Test
  public void testIterableEntry() {
    Int2LongFrequencyDistribution fd = new Int2LongFrequencyDistributionEntry();
    testIterableCommon(fd);
  }

  @Test
  public void testSerialization() throws Exception {
    Int2LongFrequencyDistribution fd = new Int2LongFrequencyDistributionEntry();
    testSerialization(fd, Int2LongFrequencyDistributionEntry.class);
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(Int2LongFrequencyDistributionEntryTest.class);
  }
}
