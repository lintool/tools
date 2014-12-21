package tl.lin.lucene.wikipedia;

import java.io.File;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.benchmark.byTask.feeds.DocMaker;
import org.apache.lucene.benchmark.byTask.feeds.EnwikiContentSource;
import org.apache.lucene.benchmark.byTask.utils.Config;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

// Adapted from https://github.com/lemire/IndexWikipedia
public class IndexWikipediaDumpDefault {

  private static final String INPUT_OPTION = "input";
  private static final String INDEX_OPTION = "index";

  @SuppressWarnings("static-access")
  public static void main(String[] args) throws Exception {
    Options options = new Options();
    options.addOption(OptionBuilder.withArgName("path").hasArg()
        .withDescription("bz2 Wikipedia XML dump file").create(INPUT_OPTION));
    options.addOption(OptionBuilder.withArgName("dir").hasArg()
        .withDescription("index location").create(INDEX_OPTION));

    CommandLine cmdline = null;
    CommandLineParser parser = new GnuParser();
    try {
      cmdline = parser.parse(options, args);
    } catch (ParseException exp) {
      System.err.println("Error parsing command line: " + exp.getMessage());
      System.exit(-1);
    }

    if (!cmdline.hasOption(INPUT_OPTION) || !cmdline.hasOption(INDEX_OPTION)) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp(IndexWikipediaDumpDefault.class.getCanonicalName(), options);
      System.exit(-1);
    }

    File indexDir = new File(cmdline.getOptionValue(INDEX_OPTION));
    File dumpFile = new File(cmdline.getOptionValue(INPUT_OPTION));

    FSDirectory dir = FSDirectory.open(indexDir);

    StandardAnalyzer analyzer = new StandardAnalyzer();
    IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_2, analyzer);
    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

    IndexWriter indexWriter = new IndexWriter(dir, config);
    DocMaker docMaker = new DocMaker();
    Properties properties = new Properties();
    properties.setProperty("content.source.forever", "false");
    // Will parse each document only once
    properties.setProperty("docs.file", dumpFile.getAbsolutePath());
    properties.setProperty("keep.image.only.docs", "false");

    Config c = new Config(properties);
    EnwikiContentSource source = new EnwikiContentSource();
    source.setConfig(c);
    source.resetInputs();
    docMaker.setConfig(c, source);

    int count = 0;
    System.out.println("Starting Indexing of Wikipedia dump " + dumpFile.getAbsolutePath());
    long start = System.currentTimeMillis();
    Document doc;
    try {
      while ((doc = docMaker.makeDocument()) != null) {
        indexWriter.addDocument(doc);
        count++;
        if (count % 10000 == 0)
          System.out.println("Indexed " + count + " documents in "
              + (System.currentTimeMillis() - start) + " ms");
      }
    } catch (org.apache.lucene.benchmark.byTask.feeds.NoMoreDataException nmd) {
      nmd.printStackTrace();
    }

    // Optimize down to a single segment
    indexWriter.forceMerge(1);

    long finish = System.currentTimeMillis();
    System.out.println("Indexing " + count + " documents took " + (finish - start) + " ms");
    System.out.println("Total data processed: " + source.getTotalBytesCount() + " bytes");
    System.out.println("Index should be located at " + dir.getDirectory().getAbsolutePath());
    docMaker.close();
    indexWriter.close();
  }
}
