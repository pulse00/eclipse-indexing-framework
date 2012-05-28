package com.dubture.indexing.text;

import java.io.IOException;

import junit.framework.TestCase;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

public class IndexingTest extends TestCase
{

    @Test
    public void testIndexing()
    {
        
        try {
            StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_29);
            Directory index = new RAMDirectory();
            MaxFieldLength length = new MaxFieldLength(255);
            
            IndexWriter w = new IndexWriter(index, analyzer, length);
            addDoc(w, "Lucene in Action");
            addDoc(w, "Lucene for Dummies");
            addDoc(w, "Managing Gigabytes");
            addDoc(w, "The Art of Computer Science");
            w.close();

            // 2. query
            String querystr = "lucene";

            // the "title" arg specifies the default field to use
            // when no field is explicitly specified in the query.
            Query q = new QueryParser(Version.LUCENE_29, "title", analyzer).parse(querystr);

            // 3. search
            int hitsPerPage = 10;
            IndexReader reader = IndexReader.open(index, false);
            IndexSearcher searcher = new IndexSearcher(reader);
            TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
            searcher.search(q, collector);
            ScoreDoc[] hits = collector.topDocs().scoreDocs;

            // 4. display results
            System.out.println("Found " + hits.length + " hits.");
            for(int i=0;i<hits.length;++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                System.out.println((i + 1) + ". " + d.get("title"));
            }

            // searcher can only be closed when there
            // is no need to access the documents any more.
            searcher.close();
        } catch (CorruptIndexException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (LockObtainFailedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }
    
    private void addDoc(IndexWriter w, String value) throws IOException {
        Document doc = new Document();
        doc.add(new Field("title", value, Field.Store.YES, Field.Index.ANALYZED));
        w.addDocument(doc);
      }    

}
