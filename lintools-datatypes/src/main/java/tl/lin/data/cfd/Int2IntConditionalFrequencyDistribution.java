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

import org.apache.hadoop.io.Writable;

import tl.lin.data.fd.Int2IntFrequencyDistribution;

/**
 * A conditional frequency distribution where events are ints and counts are ints.
 */
public interface Int2IntConditionalFrequencyDistribution extends Writable {

	/**
	 * Sets the observed count of <code>k</code> conditioned on <code>cond</code> to <code>v</code>.
	 */
	public void set(int k, int cond, int v);

	/**
	 * Increments the observed count of <code>k</code> conditioned on <code>cond</code>.
	 */
	public void increment(int k, int cond);

	/**
	 * Increments the observed count of <code>k</code> conditioned on <code>cond</code> by <code>v</code>.
	 */
	public void increment(int k, int cond, int v);

	/**
	 * Returns the observed count of <code>k</code> conditioned on <code>cond</code>.
	 */
	public int get(int k, int cond);

	/**
	 * Returns the marginal count of <code>k</code>. That is, sum of counts of
	 * <code>k</code> conditioned on all <code>cond</code>.
	 */
	public long getMarginalCount(int k);

	/**
	 * Returns the frequency distribution conditioned on <code>cond</code>.
	 */
	public Int2IntFrequencyDistribution getConditionalDistribution(int cond);

	/**
	 * Returns the sum of all counts.
	 */
	public long getSumOfAllCounts();

	/**
	 * Performs an internal consistency check of this data structure. An
	 * exception will be thrown if an error is found.
	 */
	public void check();
}
