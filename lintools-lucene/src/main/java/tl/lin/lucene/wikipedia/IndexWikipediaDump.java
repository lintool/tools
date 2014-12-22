package tl.lin.lucene.wikipedia;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.IntField;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wikiclean.WikiClean;
import org.wikiclean.WikiCleanBuilder;
import org.wikiclean.WikipediaBz2DumpInputStream;

public class IndexWikipediaDump {
  private static final Logger LOG = Logger.getLogger(IndexWikipediaDump.class);

  public static final Analyzer ANALYZER = new StandardAnalyzer();

  static final FieldType TEXT_OPTIONS = new FieldType();

  static {
    TEXT_OPTIONS.setIndexed(true);
    //TEXT_OPTIONS.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
    TEXT_OPTIONS.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
    TEXT_OPTIONS.setStored(true);
    TEXT_OPTIONS.setTokenized(true);        
  }

  public static enum IndexField {
    ID("id"),
    TITLE("title"),
    TEXT("text");

    public final String name;

    IndexField(String s) {
      name = s;
    }
  };

  private static final int DEFAULT_NUM_THREADS = 4;

  private static final String INPUT_OPTION = "input";
  private static final String INDEX_OPTION = "index";
  private static final String MAX_OPTION = "maxdocs";
  private static final String OPTIMIZE_OPTION = "optimize";
  private static final String THREADS_OPTION = "threads";

  @SuppressWarnings("static-access")
  public static void main(String[] args) throws Exception {
    Options options = new Options();
    options.addOption(OptionBuilder.withArgName("path").hasArg()
        .withDescription("bz2 Wikipedia XML dump file").create(INPUT_OPTION));
    options.addOption(OptionBuilder.withArgName("dir").hasArg()
        .withDescription("index location").create(INDEX_OPTION));
    options.addOption(OptionBuilder.withArgName("num").hasArg()
        .withDescription("maximum number of documents to index").create(MAX_OPTION));
    options.addOption(OptionBuilder.withArgName("num").hasArg()
        .withDescription("number of indexing threads").create(THREADS_OPTION));

    options.addOption(new Option(OPTIMIZE_OPTION, "merge indexes into a single segment"));

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
      formatter.printHelp(IndexWikipediaDump.class.getCanonicalName(), options);
      System.exit(-1);
    }

    String indexPath = cmdline.getOptionValue(INDEX_OPTION);
    int maxdocs = cmdline.hasOption(MAX_OPTION) ?
        Integer.parseInt(cmdline.getOptionValue(MAX_OPTION)) : Integer.MAX_VALUE;
    int threads = cmdline.hasOption(THREADS_OPTION) ?
        Integer.parseInt(cmdline.getOptionValue(THREADS_OPTION)) : DEFAULT_NUM_THREADS;

    long startTime = System.currentTimeMillis();

    String path = cmdline.getOptionValue(INPUT_OPTION);
    PrintStream out = new PrintStream(System.out, true, "UTF-8");
    WikiClean cleaner = new WikiCleanBuilder().withTitle(true).build();

    Directory dir = FSDirectory.open(new File(indexPath));
    IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_4_10_2, ANALYZER);
    config.setOpenMode(OpenMode.CREATE);

    IndexWriter writer = new IndexWriter(dir, config);
    LOG.info("Creating index at " + indexPath);
    LOG.info("Indexing with " + threads + " threads");

    try {
      WikipediaBz2DumpInputStream stream = new WikipediaBz2DumpInputStream(path);

      ExecutorService executor = Executors.newFixedThreadPool(threads);
      int cnt = 0;
      String page;
      while ((page = stream.readNext()) != null) {
        String title = cleaner.getTitle(page);

        // These are heuristic specifically for filtering out non-articles in enwiki-20120104.
        if (title.startsWith("Wikipedia:") || title.startsWith("Portal:") || title.startsWith("File:")) {
          continue;
        }

        if (page.contains("#REDIRECT") || page.contains("#redirect") || page.contains("#Redirect")) {
          continue;
        }

        Runnable worker = new AddDocumentRunnable(writer, cleaner, page);
        executor.execute(worker);

        cnt++;
        if (cnt % 10000 == 0) {
          LOG.info(cnt + " articles added");
        }
        if (cnt >= maxdocs) {
          break;
        }
      }

      executor.shutdown();
      // Wait until all threads are finish
      while (!executor.isTerminated()) {}

      LOG.info("Total of " + cnt + " articles indexed.");

      if (cmdline.hasOption(OPTIMIZE_OPTION)) {
        LOG.info("Merging segments...");
        writer.forceMerge(1);
        LOG.info("Done!");
      }

      LOG.info("Total elapsed time: " + (System.currentTimeMillis() - startTime) + "ms");
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      writer.close();
      dir.close();
      out.close();
    }
  }
  
  private static class AddDocumentRunnable implements Runnable {
    private final IndexWriter writer;
    private final WikiClean cleaner;
    private final String page;

    AddDocumentRunnable(IndexWriter writer, WikiClean cleaner, String page) {
      this.writer = writer;
      this.cleaner = cleaner;
      this.page = page;
    }

    @Override
    public void run() {
      Document doc = new Document();
      doc.add(new IntField(IndexField.ID.name, Integer.parseInt(cleaner.getId(page)), Field.Store.YES));
      doc.add(new Field(IndexField.TEXT.name, cleaner.clean(page), TEXT_OPTIONS));
      doc.add(new Field(IndexField.TITLE.name, cleaner.getTitle(page), TEXT_OPTIONS));

      try {
        writer.addDocument(doc);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  } 
}
