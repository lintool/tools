package tl.lin.lucene.wikipedia;

import java.io.PrintStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.wikiclean.WikiClean;
import org.wikiclean.WikiCleanBuilder;
import org.wikiclean.WikipediaBz2DumpInputStream;

public class ConvertWikipediaDumpToTrecFormat {

  private static final String INPUT_OPTION = "input";
  private static final String MAX_OPTION = "maxdocs";

  @SuppressWarnings("static-access")
  public static void main(String[] args) throws Exception {
    Options options = new Options();
    options.addOption(OptionBuilder.withArgName("path").hasArg()
        .withDescription("bz2 Wikipedia XML dump file").create(INPUT_OPTION));
    options.addOption(OptionBuilder.withArgName("num").hasArg()
        .withDescription("maximum number of documents to index").create(MAX_OPTION));

    CommandLine cmdline = null;
    CommandLineParser parser = new GnuParser();
    try {
      cmdline = parser.parse(options, args);
    } catch (ParseException exp) {
      System.err.println("Error parsing command line: " + exp.getMessage());
      System.exit(-1);
    }

    if (!cmdline.hasOption(INPUT_OPTION)) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp(IndexWikipediaDump.class.getCanonicalName(), options);
      System.exit(-1);
    }

    int maxdocs = cmdline.hasOption(MAX_OPTION) ?
        Integer.parseInt(cmdline.getOptionValue(MAX_OPTION)) : Integer.MAX_VALUE;
    String path = cmdline.getOptionValue(INPUT_OPTION);

    PrintStream out = new PrintStream(System.out, true, "UTF-8");
    WikiClean cleaner = new WikiCleanBuilder().withTitle(true).build();

    try {
      WikipediaBz2DumpInputStream stream = new WikipediaBz2DumpInputStream(path);

      int cnt = 0;
      String page;
      while ((page = stream.readNext()) != null) {
        String title = cleaner.getTitle(page);

        // These are heuristic specifically for filtering out non-articles in enwiki-20120104.
        if (title.startsWith("Wikipedia:") || title.startsWith("Portal:") || title.startsWith("File:")) {
          continue;
        }

        // Filtering based on enwiki-20141106
        if (title.startsWith("Draft:")) {
          continue;
        }

        if (page.contains("#REDIRECT") || page.contains("#redirect") || page.contains("#Redirect")) {
          continue;
        }

        out.println("<DOC>");
        out.println("<DOCNO>" + cleaner.getId(page) + ":" + cleaner.getTitle(page).replaceAll("\\s+", "_") + "</DOCNO>");
        out.println(cleaner.clean(page));
        out.println("</DOC>");
        cnt++;
        if (cnt % 10000 == 0) {
          System.err.println(cnt + " articles read");
        }
        if (cnt >= maxdocs) {
          break;
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      out.close();
    }
  }
}
