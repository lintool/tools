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

public class Object2IntFrequencyDistributionFastutilTest extends
    Object2IntFrequencyDistributionTestBase {

  @Test
  public void test1Fastutil() {
    Object2IntFrequencyDistribution<String> fd =
      new Object2IntFrequencyDistributionFastutil<String>();
    test1Common(fd);
  }

  @Test
  public void test2Fastutil() {
    Object2IntFrequencyDistribution<String> fd =
      new Object2IntFrequencyDistributionFastutil<String>();
    test2Common(fd);
  }

  @Test
  public void test3Fastutil() {
    Object2IntFrequencyDistribution<String> fd =
      new Object2IntFrequencyDistributionFastutil<String>();
    test3Common(fd);
  }

  @Test(expected = RuntimeException.class)
  public void testFailedDecrement1Fastutil() {
    Object2IntFrequencyDistribution<String> fd =
      new Object2IntFrequencyDistributionFastutil<String>();
    testFailedDecrement1Common(fd);
  }

  @Test(expected = RuntimeException.class)
  public void testFailedDecrement2Fastutil() {
    Object2IntFrequencyDistribution<String> fd =
      new Object2IntFrequencyDistributionFastutil<String>();
    testFailedDecrement2Common(fd);
  }

  @Test
  public void testMultiIncrementDecrementFastutil() {
    Object2IntFrequencyDistribution<String> fd =
      new Object2IntFrequencyDistributionFastutil<String>();
    testMultiIncrementDecrementCommon(fd);
  }

  @Test
  public void testGetFrequencySortedFastutil() {
    Object2IntFrequencyDistribution<String> fd =
      new Object2IntFrequencyDistributionFastutil<String>();
    testGetFrequencySortedCommon(fd);
  }

  @Test
  public void testGetSortedEventsFastutil() {
    Object2IntFrequencyDistribution<String> fd =
      new Object2IntFrequencyDistributionFastutil<String>();
    testGetSortedEventsCommon(fd);
  }

  @Test
  public void testIterableFastutil() {
    Object2IntFrequencyDistribution<String> fd =
      new Object2IntFrequencyDistributionFastutil<String>();
    testIterableCommon(fd);
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(Object2IntFrequencyDistributionFastutilTest.class);
  }
}