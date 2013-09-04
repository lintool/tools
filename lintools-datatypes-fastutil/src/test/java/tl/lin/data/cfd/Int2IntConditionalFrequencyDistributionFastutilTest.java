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

package tl.lin.data.cfd;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

public class Int2IntConditionalFrequencyDistributionFastutilTest extends 
    Int2IntConditionalFrequencyDistributionTestBase {

  @Test
  public void test1Entry() {
    Int2IntConditionalFrequencyDistribution cfd =
        new Int2IntConditionalFrequencyDistributionEntry();
    test1Common(cfd);
  }

  @Test
  public void test1Fastutil() {
    Int2IntConditionalFrequencyDistribution cfd =
        new Int2IntConditionalFrequencyDistributionFastutil();
    test1Common(cfd);
  }

  @Test
  public void test2Entry() {
    Int2IntConditionalFrequencyDistribution cfd =
        new Int2IntConditionalFrequencyDistributionEntry();
    test2Common(cfd);
  }

  @Test
  public void test2Fastutil() {
    Int2IntConditionalFrequencyDistribution cfd =
        new Int2IntConditionalFrequencyDistributionFastutil();
    test2Common(cfd);
  }

  @Test
  public void test3Entry() {
    Int2IntConditionalFrequencyDistribution cfd =
        new Int2IntConditionalFrequencyDistributionEntry();
    test3Common(cfd);
  }

  @Test
  public void test3Fastutil() {
    Int2IntConditionalFrequencyDistribution cfd =
        new Int2IntConditionalFrequencyDistributionFastutil();
    test3Common(cfd);
  }

  @Test
  public void testLargeMarginalEntry() {
    Int2IntConditionalFrequencyDistribution cfd =
        new Int2IntConditionalFrequencyDistributionEntry();
    testLargeMarginalCommon(cfd);
  }

  @Test
  public void testLargeMarginalFastutil() {
    Int2IntConditionalFrequencyDistribution cfd =
        new Int2IntConditionalFrequencyDistributionFastutil();
    testLargeMarginalCommon(cfd);
  }

  @Test
  public void testSerialization() throws Exception {
    Int2IntConditionalFrequencyDistribution fd = new Int2IntConditionalFrequencyDistributionFastutil();
    testSerialization(fd, Int2IntConditionalFrequencyDistributionFastutil.class);
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(
        Int2IntConditionalFrequencyDistributionFastutilTest.class);
  }
}
