/*******************************************************************************
 * This file is part of the lucene indexing eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.dubture.indexing.core.index;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;

/**
 * 
 * Main class for indexing lucene documents.
 * 
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
public class DocumentManager
{
    private static DocumentManager instance;
    
    private IndexWriter writer;
    
    private IndexReader reader;
    
    private Map<IFile, ReferenceInfo> pendingReferences;
    
    private Directory index;
    
    private DocumentManager() throws Exception
    {
        StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_29);
        MaxFieldLength length = new MaxFieldLength(255);
        
        index = new RAMDirectory();
        writer = new IndexWriter(index, analyzer, length);
        reader = IndexReader.open(index, true);
        pendingReferences = new HashMap<IFile, ReferenceInfo>();
                
    }
    
    public static DocumentManager getInstance() throws Exception
    {
        if (instance == null) {
            instance = new DocumentManager();
        }
        
        return instance;
    }

    public void deleteReferences(IFile file) throws Exception
    {
        BooleanQuery boolQuery = new BooleanQuery();
        
        boolQuery.add(new TermQuery(new Term("path", file.getFullPath().toString())), 
                BooleanClause.Occur.MUST
        );
        
        boolQuery.add(new TermQuery( new Term("filename", file.getName())),
                BooleanClause.Occur.MUST
        );
        
        boolQuery.add(new TermQuery(new Term("type", "reference")), 
                BooleanClause.Occur.MUST
        );
        
        writer.deleteDocuments(boolQuery);
        writer.commit();
        
        reader = IndexReader.open(index, true);
    }
    
    public Query getPathQuery(IPath path)
    {
        TermQuery query = new TermQuery(new Term("path", path.toString()));
        return query;
    }
    
    public IndexWriter getWriter()
    {
        return writer;
    }

    public void addReference(IFile file, ReferenceInfo reference) throws Exception
    {
        pendingReferences.put(file, reference);
    }

    @SuppressWarnings("rawtypes")
    public void flush() throws Exception
    {
        Iterator it = pendingReferences.keySet().iterator();
        
        while(it.hasNext()) {
            
            IFile file = (IFile) it.next();
            ReferenceInfo ref = pendingReferences.get(file);
            
            Document doc = new Document();
            
            doc.add(new Field("path", file.getFullPath().toString(), Field.Store.YES, Field.Index.ANALYZED));
            doc.add(new Field("filename", file.getName(), Field.Store.YES, Field.Index.ANALYZED));
            doc.add(new Field("name", ref.name, Field.Store.YES, Field.Index.ANALYZED));
            doc.add(new Field("type", "reference", Field.Store.YES, Field.Index.ANALYZED));
            
            writer.addDocument(doc);
        }
        
        writer.commit();
        pendingReferences.clear();
        
    }
    
    public void shutdown()
    {
        try {
            writer.close();
        } catch (CorruptIndexException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public IndexReader getReader()
    {
        return reader;
    }
}
