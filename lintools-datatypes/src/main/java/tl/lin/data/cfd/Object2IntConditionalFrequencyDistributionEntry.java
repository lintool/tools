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

import java.util.Map;

import tl.lin.data.fd.Object2IntFrequencyDistribution;
import tl.lin.data.fd.Object2IntFrequencyDistributionEntry;
import tl.lin.data.map.HMapKI;
import tl.lin.data.map.HMapKL;
import tl.lin.data.pair.PairOfObjectInt;

import com.google.common.collect.Maps;

/**
 * An implementation of a conditional frequency distribution for arbitrary
 * events, backed by {@link HMapKI}. This class keeps track of
 * frequencies using ints, so beware when dealing with a large number of
 * observations.
 *
 * @author Jimmy Lin
 *
 */
public class Object2IntConditionalFrequencyDistributionEntry<K extends Comparable<K>>
    implements Object2IntConditionalFrequencyDistribution<K> {

  private final Map<K, Object2IntFrequencyDistribution<K>> distributions = Maps.newHashMap();
  private final HMapKL<K> marginals = new HMapKL<K>();

  private long sumOfAllCounts = 0;

  @Override
  public void set(K k, K cond, int v) {
    if (!distributions.containsKey(cond)) {
      Object2IntFrequencyDistribution<K> fd = new Object2IntFrequencyDistributionEntry<K>();
      fd.set(k, v);
      distributions.put(cond, fd);
      marginals.increment(k, v);

      sumOfAllCounts += v;
    } else {
      Object2IntFrequencyDistribution<K> fd = distributions.get(cond);
      int rv = fd.get(k);

      fd.set(k, v);
      distributions.put(cond, fd);
      marginals.increment(k, -rv + v);

      sumOfAllCounts = sumOfAllCounts - rv + v;
    }
  }

  @Override
  public void increment(K k, K cond) {
    increment(k, cond, 1);
  }

  @Override
  public void increment(K k, K cond, int v) {
    int cur = get(k, cond);
    if (cur == 0) {
      set(k, cond, v);
    } else {
      set(k, cond, cur + v);
    }
  }

  @Override
  public int get(K k, K cond) {
    if (!distributions.containsKey(cond)) {
      return 0;
    }

    return distributions.get(cond).get(k);
  }

  @Override
  public long getMarginalCount(K k) {
    return marginals.get(k);
  }

  @Override
  public Object2IntFrequencyDistribution<K> getConditionalDistribution(K cond) {
    if (distributions.containsKey(cond)) {
      return distributions.get(cond);
    }

    return new Object2IntFrequencyDistributionEntry<K>();
  }

  @Override
  public long getSumOfAllCounts() {
    return sumOfAllCounts;
  }

  @Override
  public void check() {
    Object2IntFrequencyDistribution<K> m = new Object2IntFrequencyDistributionEntry<K>();

    long totalSum = 0;
    for (Object2IntFrequencyDistribution<K> fd : distributions.values()) {
      long conditionalSum = 0;

      for (PairOfObjectInt<K> pair : fd) {
        conditionalSum += pair.getRightElement();
        m.increment(pair.getLeftElement(), pair.getRightElement());
      }

      if (conditionalSum != fd.getSumOfCounts()) {
        throw new RuntimeException("Internal Error!");
      }
      totalSum += fd.getSumOfCounts();
    }

    if (totalSum != getSumOfAllCounts()) {
      throw new RuntimeException("Internal Error! Got " + totalSum
          + ", Expected " + getSumOfAllCounts());
    }

    for (PairOfObjectInt<K> e : m) {
      if (e.getRightElement() != marginals.get(e.getLeftElement())) {
        throw new RuntimeException("Internal Error!");
      }
    }

    for (PairOfObjectInt<K> e : m) {
      if (e.getRightElement() != m.get(e.getLeftElement())) {
        throw new RuntimeException("Internal Error!");
      }
    }
  }
}
