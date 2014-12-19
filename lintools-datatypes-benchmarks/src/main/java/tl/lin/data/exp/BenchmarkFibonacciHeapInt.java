package tl.lin.data.exp;

import java.util.Random;

public class BenchmarkFibonacciHeapInt {
  public static void main(String[] args) {
    FibonacciHeapInt heap = new FibonacciHeapInt();
    Random r = new Random();

    long startTime;
    long endTime;

    startTime = System.currentTimeMillis();
    for (int i = 0; i < 1000000; i++) {
      int n = r.nextInt(1000);
      float f = r.nextFloat();

      heap.insert(n, f);
    }

    endTime = System.currentTimeMillis();
    System.out.println("inserts completed in " + (endTime - startTime) + " ms");

    @SuppressWarnings("unused")
    long sum = 0;
    startTime = System.currentTimeMillis();
    for (int i = 0; i < 500000; i++) {
      sum += heap.removeMin().getDatum();
    }
    endTime = System.currentTimeMillis();
    System.out.println("mins completed in " + (endTime - startTime) + " ms");
  }
}
