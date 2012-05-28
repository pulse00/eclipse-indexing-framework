/*******************************************************************************
 * This file is part of the lucene indexing eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.dubture.indexing.core.search;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.eclipse.core.runtime.IPath;

import com.dubture.indexing.core.index.DocumentManager;
import com.dubture.indexing.core.index.ReferenceInfo;

/**
 * 
 * Main class for searching the lucene index.
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
public class SearchEngine
{
    private static SearchEngine instance;
    
    private DocumentManager manager;
    
    
    private SearchEngine() throws Exception
    {
        manager = DocumentManager.getInstance();
    }
    
    public static SearchEngine getInstance() throws Exception
    {
        if (instance == null) {
            instance = new SearchEngine();
        }
        
        return instance;
    }
    
    public List<ReferenceInfo> findReferences(IPath path) throws Exception
    {
        List<ReferenceInfo> references = new ArrayList<ReferenceInfo>();
        int hitsPerPage = 10;
    
        IndexReader reader = manager.getReader();
        IndexSearcher searcher = new IndexSearcher(reader);
        TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
        searcher.search(manager.getPathQuery(path), collector);
        ScoreDoc[] hits = collector.topDocs().scoreDocs;        
        
        for(int i=0;i<hits.length;++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            ReferenceInfo info = new ReferenceInfo();
            info.setName(d.get("name"));
            references.add(info);
        }
        
        System.err.println(" NUM DOCS " + reader.numDocs());
        return references;
    }
}
