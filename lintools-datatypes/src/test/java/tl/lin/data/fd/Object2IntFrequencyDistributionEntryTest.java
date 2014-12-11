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

public class Object2IntFrequencyDistributionEntryTest extends
    Object2IntFrequencyDistributionTestBase {

  @Test
  public void test1Entry() {
    Object2IntFrequencyDistribution<String> fd = new Object2IntFrequencyDistributionEntry<String>();
    test1Common(fd);
  }

  @Test
  public void test2Entry() {
    Object2IntFrequencyDistribution<String> fd = new Object2IntFrequencyDistributionEntry<String>();
    test2Common(fd);
  }

  @Test
  public void test3Entry() {
    Object2IntFrequencyDistribution<String> fd = new Object2IntFrequencyDistributionEntry<String>();
    test3Common(fd);
  }

  @Test(expected = RuntimeException.class)
  public void testFailedDecrement1Entry() {
    Object2IntFrequencyDistribution<String> fd = new Object2IntFrequencyDistributionEntry<String>();
    testFailedDecrement1Common(fd);
  }

  @Test(expected = RuntimeException.class)
  public void testFailedDecrement2Entry() {
    Object2IntFrequencyDistribution<String> fd = new Object2IntFrequencyDistributionEntry<String>();
    testFailedDecrement2Common(fd);
  }

  @Test
  public void testMultiIncrementDecrementEntry() {
    Object2IntFrequencyDistribution<String> fd = new Object2IntFrequencyDistributionEntry<String>();
    testMultiIncrementDecrementCommon(fd);
  }

  @Test
  public void testGetFrequencySortedEntry() {
    Object2IntFrequencyDistribution<String> fd = new Object2IntFrequencyDistributionEntry<String>();
    testGetFrequencySortedCommon(fd);
  }

  @Test
  public void testGetSortedEventsEntry() {
    Object2IntFrequencyDistribution<String> fd = new Object2IntFrequencyDistributionEntry<String>();
    testGetSortedEventsCommon(fd);
  }

  @Test
  public void testIterableEntry() {
    Object2IntFrequencyDistribution<String> fd = new Object2IntFrequencyDistributionEntry<String>();
    testIterableCommon(fd);
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(Object2IntFrequencyDistributionEntryTest.class);
  }
}