package tl.lin.data.benchmark.basic;

import java.util.Random;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;

import tl.lin.data.pair.PairOfInts;

/**
 * Generates one million random PairOfInts. Each is populated with two randomly-generated integers
 * between 0 and 1000.
 */
public class GenerateRandomPairsOfInts {

  private GenerateRandomPairsOfInts() {
  }

  /**
   * Runs this program.
   */
  public static void main(String[] args) throws Exception {
    Random r = new Random();

    Configuration conf = new Configuration();
    SequenceFile.Writer writer = SequenceFile.createWriter(conf,
        SequenceFile.Writer.file(new Path("random-pairs.seq")),
        SequenceFile.Writer.keyClass(PairOfInts.class),
        SequenceFile.Writer.valueClass(IntWritable.class));

    IntWritable n = new IntWritable();
    PairOfInts pair = new PairOfInts();

    for (int i = 0; i < 1000000; i++) {
      n.set(i);
      pair.set(r.nextInt(1000), r.nextInt(1000));
      writer.append(pair, n);
    }

    writer.close();

  }
}
