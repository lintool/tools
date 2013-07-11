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

import static org.junit.Assert.assertEquals;

public class Int2IntConditionalFrequencyDistributionTestBase {
  protected void test1Common(Int2IntConditionalFrequencyDistribution cfd) {
    cfd.set(1, 1, 2);
    cfd.check();

    assertEquals(2, cfd.get(1, 1));
    assertEquals(2, cfd.getSumOfAllCounts());

    cfd.set(2, 1, 3);
    cfd.check();

    assertEquals(2, cfd.get(1, 1));
    assertEquals(3, cfd.get(2, 1));
    assertEquals(5, cfd.getSumOfAllCounts());

    cfd.set(3, 1, 10);
    cfd.check();

    assertEquals(2, cfd.get(1, 1));
    assertEquals(3, cfd.get(2, 1));
    assertEquals(10, cfd.get(3, 1));
    assertEquals(15, cfd.getSumOfAllCounts());

    cfd.set(10, 2, 1);
    cfd.check();

    assertEquals(2, cfd.get(1, 1));
    assertEquals(3, cfd.get(2, 1));
    assertEquals(10, cfd.get(3, 1));
    assertEquals(1, cfd.get(10, 2));
    assertEquals(16, cfd.getSumOfAllCounts());

    cfd.set(1, 1, 5);
    cfd.check();

    assertEquals(5, cfd.get(1, 1));
    assertEquals(3, cfd.get(2, 1));
    assertEquals(10, cfd.get(3, 1));
    assertEquals(1, cfd.get(10, 2));
    assertEquals(19, cfd.getSumOfAllCounts());
  }

  protected void test2Common(Int2IntConditionalFrequencyDistribution cfd) {
    cfd.set(1, 1, 2);
    cfd.check();

    assertEquals(2, cfd.get(1, 1));
    assertEquals(2, cfd.getSumOfAllCounts());

    cfd.increment(1, 1);
    cfd.check();
    assertEquals(3, cfd.get(1, 1));
    assertEquals(3, cfd.getSumOfAllCounts());

    cfd.increment(1, 1, 2);
    cfd.check();
    assertEquals(5, cfd.get(1, 1));
    assertEquals(5, cfd.getSumOfAllCounts());

    cfd.increment(2, 1);
    cfd.check();
    assertEquals(5, cfd.get(1, 1));
    assertEquals(1, cfd.get(2, 1));
    assertEquals(6, cfd.getSumOfAllCounts());

    cfd.increment(1, 2, 10);
    cfd.check();
    assertEquals(5, cfd.get(1, 1));
    assertEquals(1, cfd.get(2, 1));
    assertEquals(10, cfd.get(1, 2));
    assertEquals(16, cfd.getSumOfAllCounts());
  }

  protected void test3Common(Int2IntConditionalFrequencyDistribution cfd) {
    cfd.set(1, 1, 2);
    cfd.set(1, 2, 5);
    cfd.set(1, 3, 6);
    cfd.set(1, 4, 4);
    cfd.set(2, 1, 3);
    cfd.set(3, 1, 7);
    cfd.check();

    assertEquals(17, cfd.getMarginalCount(1));
    assertEquals(27, cfd.getSumOfAllCounts());

    cfd.increment(1, 1, 2);
    cfd.increment(2, 1);

    assertEquals(19, cfd.getMarginalCount(1));
    assertEquals(4, cfd.getMarginalCount(2));
    assertEquals(30, cfd.getSumOfAllCounts());
  }

  protected void testLargeMarginalCommon(Int2IntConditionalFrequencyDistribution cfd) {
    cfd.set(1, 2, 2000000000);
    cfd.set(1, 3, 2000000000);
    cfd.set(1, 5, 2000000000);
    cfd.set(1, 1, 2000000000);

    assertEquals(8000000000L, cfd.getMarginalCount(1));
  }
}
