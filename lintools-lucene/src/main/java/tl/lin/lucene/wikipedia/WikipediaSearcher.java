package tl.lin.lucene.wikipedia;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.store.FSDirectory;

import tl.lin.lucene.wikipedia.IndexWikipediaDump.IndexField;

import com.google.common.base.Preconditions;

public class WikipediaSearcher {
  protected IndexReader reader;
  protected IndexSearcher searcher;
  private QueryParser parserArticle;
  private QueryParser parserTitle;

  protected WikipediaSearcher() {}

  public WikipediaSearcher(File indexLocation) throws IOException {
    Preconditions.checkNotNull(indexLocation);
    Preconditions.checkArgument(indexLocation.exists());

    reader = DirectoryReader.open(FSDirectory.open(indexLocation));

    init();
  }

  protected void init() {
    searcher = new IndexSearcher(reader);
    searcher.setSimilarity(new LMDirichletSimilarity(2500.0f));

    parserArticle = new QueryParser(IndexField.TEXT.name, IndexWikipediaDump.ANALYZER);
    parserTitle = new QueryParser(IndexField.TITLE.name, IndexWikipediaDump.ANALYZER);
  }

  public float scoreArticle(String q, int wikiId) {
    try {
      int internalId = internalIdFromWikipediaId(wikiId);
      if (internalId == -1) return 0.0f;
      Query query = parserArticle.parse(q);
      Explanation explanation = searcher.explain(query, internalId);
      System.out.println(explanation);
      return explanation.getValue();
    } catch (Exception e) {
      e.printStackTrace();
      return 0.0f;
    }
  }

  public float scoreArticle(String q, String title) {
    try {
      int internalId = internalIdFromWikipediaTitle(title);
      if (internalId == -1) return 0.0f;
      Query query = parserArticle.parse(q);
      Explanation explanation = searcher.explain(query, internalId);
      System.out.println(explanation);
      return explanation.getValue();
    } catch (Exception e) {
      e.printStackTrace();
      return 0.0f;
    }
  }

  public TopDocs searchArticle(String q, int numResults) {
    try {
      Query query = parserArticle.parse(q);
      return searcher.search(query, numResults);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public TopDocs searchTitle(String q, int numResults) {
    try {
      Query query = parserTitle.parse(q);
      return searcher.search(query, numResults);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public Document doc(int docid) {
    try {
      return searcher.doc(docid);
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  public int getArticleId(String s) {
    try {
      Query query = parserTitle.parse("\"" + s + "\"");
      TopDocs rs = searcher.search(query, 10);

      for (ScoreDoc scoreDoc : rs.scoreDocs) {
        Document hit = searcher.doc(scoreDoc.doc);
        if (s.equals(hit.getField(IndexField.TITLE.name).stringValue())) {
          return (Integer) hit.getField(IndexField.ID.name).numericValue();
        }
      }

      return -1;
    } catch (Exception e) {
      e.printStackTrace();
      return -1;
    }
  }

  public Document getArticle(int id) {
    try {
      Query query = NumericRangeQuery.newIntRange(IndexField.ID.name, id, id, true, true);
      TopDocs rs = searcher.search(query, 1);

      if (rs.totalHits == 0) {
        return null;
      }
      ScoreDoc scoreDoc = rs.scoreDocs[0];
      Document hit = searcher.doc(scoreDoc.doc);

      return hit;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  public Document getArticle(String s) {
    try {
      Query query = parserTitle.parse("\"" + s + "\"");
      TopDocs rs = searcher.search(query, 10);

      for (ScoreDoc scoreDoc : rs.scoreDocs) {
        Document hit = searcher.doc(scoreDoc.doc);
        if (s.equals(hit.getField(IndexField.TITLE.name).stringValue())) {
          return hit;
        }
      }

      return null;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private int internalIdFromWikipediaTitle(String s) {
    try {
      Query query = parserTitle.parse("\"" + s + "\"");
      TopDocs rs = searcher.search(query, 10);

      for (ScoreDoc scoreDoc : rs.scoreDocs) {
        Document hit = searcher.doc(scoreDoc.doc);
        if (s.equals(hit.getField(IndexField.TITLE.name).stringValue())) {
          return scoreDoc.doc;
        }
      }

      return -1;
    } catch (Exception e) {
      e.printStackTrace();
      return -1;
    }
  }

  private int internalIdFromWikipediaId(int id) {
    try {
      Query query = NumericRangeQuery.newIntRange(IndexField.ID.name, id, id, true, true);
      TopDocs rs = searcher.search(query, 1);

      if (rs.totalHits == 0) {
        return -1;
      }
      ScoreDoc scoreDoc = rs.scoreDocs[0];

      return scoreDoc.doc;
    } catch (IOException e) {
      e.printStackTrace();
      return -1;
    }
  }

  public void close() {
    try {
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
