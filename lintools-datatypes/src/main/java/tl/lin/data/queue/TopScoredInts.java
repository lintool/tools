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

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.util.PriorityQueue;

import tl.lin.data.pair.PairOfIntFloat;

public class TopScoredInts implements Writable {
  private class ScoredIntPriorityQueue extends PriorityQueue<PairOfIntFloat> {
    private ScoredIntPriorityQueue(int maxSize) {
      super.initialize(maxSize);
    }

    @Override
    protected boolean lessThan(Object obj0, Object obj1) {
      // If equal scores, break tie by int descending.
      if (((PairOfIntFloat) obj0).getValue() == ((PairOfIntFloat) obj1).getValue()) {
        return ((PairOfIntFloat) obj0).getKey() < ((PairOfIntFloat) obj1).getKey() ? true : false;
      }

      return (((PairOfIntFloat) obj0).getValue() < ((PairOfIntFloat) obj1).getValue()) ?
          true : false;
    }
  }

  private ScoredIntPriorityQueue queue;
  private int maxElements;

  // TODO: Add the option to control how to break scoring ties

  public TopScoredInts() {
    this(10);
  }

  public TopScoredInts(int n) {
    queue = new ScoredIntPriorityQueue(n);
    maxElements = n;
  }

  public void add(int n, float f) {
    queue.insert(new PairOfIntFloat(n, f));
  }

  public int getMaxElements() {
    return maxElements;
  }

  public int size() {
    return queue.size();
  }

  public PairOfIntFloat[] extractAll() {
    int len = queue.size();
    PairOfIntFloat[] arr = (PairOfIntFloat[]) new PairOfIntFloat[len];
    for (int i = 0; i < len; i++) {
      arr[len - 1 - i] = queue.pop();
    }
    return arr;
  }

  @Override
  public void readFields(DataInput in) throws IOException {
    maxElements = in.readInt();
    queue = new ScoredIntPriorityQueue(maxElements);

    int n = in.readInt();
    for (int i = 0; i < n; i++) {
      int k = in.readInt();
      float v = in.readFloat();
      add(k, v);
    }
  }

  @Override
  public void write(DataOutput out) throws IOException {
    out.writeInt(maxElements);
    int sz = size();
    out.writeInt(sz);

    // Note: serialization is destructive!
    PairOfIntFloat pair;
    for (int i = 0; i < sz; i++) {
      pair = queue.pop();
      out.writeInt(pair.getKey());
      out.writeFloat(pair.getValue());
    }
  }
}