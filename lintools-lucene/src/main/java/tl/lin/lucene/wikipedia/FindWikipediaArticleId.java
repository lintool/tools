package tl.lin.lucene.wikipedia;

import java.io.File;
import java.io.PrintStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class FindWikipediaArticleId {
  private static final String INDEX_OPTION = "index";
  private static final String TITLE_OPTION = "title";

  @SuppressWarnings("static-access")
  public static void main(String[] args) throws Exception {
    Options options = new Options();
    options.addOption(OptionBuilder.withArgName("path").hasArg()
        .withDescription("index location").create(INDEX_OPTION));
    options.addOption(OptionBuilder.withArgName("string").hasArg()
        .withDescription("article title").create(TITLE_OPTION));

    CommandLine cmdline = null;
    CommandLineParser parser = new GnuParser();
    try {
      cmdline = parser.parse(options, args);
    } catch (ParseException exp) {
      System.err.println("Error parsing command line: " + exp.getMessage());
      System.exit(-1);
    }

    if (!cmdline.hasOption(TITLE_OPTION) || !cmdline.hasOption(INDEX_OPTION)) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp(FindWikipediaArticleId.class.getName(), options);
      System.exit(-1);
    }

    File indexLocation = new File(cmdline.getOptionValue(INDEX_OPTION));
    if (!indexLocation.exists()) {
      System.err.println("Error: " + indexLocation + " does not exist!");
      System.exit(-1);
    }

    String title = cmdline.getOptionValue(TITLE_OPTION);

    PrintStream out = new PrintStream(System.out, true, "UTF-8");

    WikipediaSearcher searcher = new WikipediaSearcher(indexLocation);
    int id = searcher.getArticleId(title);

    out.println(title + ": id = " + id);

    searcher.close();
    out.close();
  }
}
