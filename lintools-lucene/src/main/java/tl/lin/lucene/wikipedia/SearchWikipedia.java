package tl.lin.lucene.wikipedia;

import java.io.File;
import java.io.PrintStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import tl.lin.lucene.wikipedia.IndexWikipediaDump.IndexField;

public class SearchWikipedia {
  private static final int DEFAULT_NUM_RESULTS = 10;

  private static final String INDEX_OPTION = "index";
  private static final String QUERY_OPTION = "q";
  private static final String NUM_RESULTS_OPTION = "num_results";
  private static final String VERBOSE_OPTION = "verbose";
  private static final String ARTICLE_OPTION = "article";
  private static final String TITLE_OPTION = "title";

  @SuppressWarnings("static-access")
  public static void main(String[] args) throws Exception {
    Options options = new Options();
    options.addOption(OptionBuilder.withArgName("path").hasArg()
        .withDescription("index location").create(INDEX_OPTION));
    options.addOption(OptionBuilder.withArgName("string").hasArg()
        .withDescription("query text").create(QUERY_OPTION));
    options.addOption(OptionBuilder.withArgName("num").hasArg()
        .withDescription("number of results to return").create(NUM_RESULTS_OPTION));

    options.addOption(new Option(VERBOSE_OPTION, "print out complete document"));
    options.addOption(new Option(TITLE_OPTION, "search title"));
    options.addOption(new Option(ARTICLE_OPTION, "search article"));

    CommandLine cmdline = null;
    CommandLineParser parser = new GnuParser();
    try {
      cmdline = parser.parse(options, args);
    } catch (ParseException exp) {
      System.err.println("Error parsing command line: " + exp.getMessage());
      System.exit(-1);
    }

    if (!cmdline.hasOption(QUERY_OPTION) || !cmdline.hasOption(INDEX_OPTION)
        || !cmdline.hasOption(QUERY_OPTION)) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp(SearchWikipedia.class.getName(), options);
      System.exit(-1);
    }

    File indexLocation = new File(cmdline.getOptionValue(INDEX_OPTION));
    if (!indexLocation.exists()) {
      System.err.println("Error: " + indexLocation + " does not exist!");
      System.exit(-1);
    }

    long startTime = System.currentTimeMillis();

    String queryText = cmdline.getOptionValue(QUERY_OPTION);
    int numResults = cmdline.hasOption(NUM_RESULTS_OPTION) ?
        Integer.parseInt(cmdline.getOptionValue(NUM_RESULTS_OPTION)) : DEFAULT_NUM_RESULTS;
    boolean verbose = cmdline.hasOption(VERBOSE_OPTION);
    boolean searchArticle = !cmdline.hasOption(TITLE_OPTION);

    PrintStream out = new PrintStream(System.out, true, "UTF-8");

    WikipediaSearcher searcher = new WikipediaSearcher(indexLocation);
    TopDocs rs = null;
    if (searchArticle) {
      rs = searcher.searchArticle(queryText, numResults);
    } else {
      rs = searcher.searchTitle(queryText, numResults);
    }

    int i = 1;
    for (ScoreDoc scoreDoc : rs.scoreDocs) {
      Document hit = searcher.doc(scoreDoc.doc);

      out.println(String.format("%d. %s (wiki id = %s, lucene id = %d) %f", i,
          hit.getField(IndexField.TITLE.name).stringValue(),
          hit.getField(IndexField.ID.name).stringValue(),
          scoreDoc.doc,
          scoreDoc.score));
      if (verbose) {
        out.println("# " + hit.toString().replaceAll("[\\n\\r]+", " "));
      }
      i++;
    }

    out.println("Search completed in " + (System.currentTimeMillis() - startTime) + "ms");

    searcher.close();
    out.close();
  }
}
