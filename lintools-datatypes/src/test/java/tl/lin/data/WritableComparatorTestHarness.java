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

package tl.lin.data;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class WritableComparatorTestHarness {

  @SuppressWarnings("rawtypes")
  public static int compare(WritableComparator comparator, WritableComparable obj1,
      WritableComparable obj2) {

    byte[] bytes1 = null, bytes2 = null;

    try {
      ByteArrayOutputStream bytesOut1 = new ByteArrayOutputStream();
      DataOutputStream dataOut1 = new DataOutputStream(bytesOut1);
      obj1.write(dataOut1);
      bytes1 = bytesOut1.toByteArray();

      ByteArrayOutputStream bytesOut2 = new ByteArrayOutputStream();
      DataOutputStream dataOut2 = new DataOutputStream(bytesOut2);
      obj2.write(dataOut2);
      bytes2 = bytesOut2.toByteArray();

    } catch (IOException e) {
      e.printStackTrace();
    }

    return comparator.compare(bytes1, 0, bytes1.length, bytes2, 0, bytes2.length);
  }
}
