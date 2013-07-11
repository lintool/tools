package tl.lin.data.fd;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

public class Int2IntFrequencyDistributionEntryTest extends Int2IntFrequencyDistributionTestBase {
  @Test
  public void test1Entry() {
    Int2IntFrequencyDistribution fd = new Int2IntFrequencyDistributionEntry();
    test1Common(fd);
  }

  @Test
  public void test2Entry() {
    Int2IntFrequencyDistribution fd = new Int2IntFrequencyDistributionEntry();
    test2Common(fd);
  }

  @Test
  public void test3Entry() {
    Int2IntFrequencyDistribution fd = new Int2IntFrequencyDistributionEntry();
    test3Common(fd);
  }

  @Test(expected = RuntimeException.class)
  public void testFailedDecrement1Entry() {
    Int2IntFrequencyDistribution fd = new Int2IntFrequencyDistributionEntry();
    testFailedDecrement1Common(fd);
  }

  @Test(expected = RuntimeException.class)
  public void testFailedDecrement2Entry() {
    Int2IntFrequencyDistribution fd = new Int2IntFrequencyDistributionEntry();
    testFailedDecrement2Common(fd);
  }

  @Test
  public void testMultiIncrementDecrementEntry() {
    Int2IntFrequencyDistribution fd = new Int2IntFrequencyDistributionEntry();
    testMultiIncrementDecrementCommon(fd);
  }

  @Test
  public void testGetFrequencySortedEventsEntry() {
    Int2IntFrequencyDistribution fd = new Int2IntFrequencyDistributionEntry();
    testGetFrequencySortedEventsCommon(fd);
  }

  @Test
  public void testGetSortedEventsEntry() {
    Int2IntFrequencyDistribution fd = new Int2IntFrequencyDistributionEntry();
    testGetSortedEventsCommon(fd);
  }

  @Test
  public void testIterableEntry() {
    Int2IntFrequencyDistribution fd = new Int2IntFrequencyDistributionEntry();
    testIterableCommon(fd);
  }

  public static junit.framework.Test suite() {
    return new JUnit4TestAdapter(Int2IntFrequencyDistributionEntryTest.class);
  }
}
