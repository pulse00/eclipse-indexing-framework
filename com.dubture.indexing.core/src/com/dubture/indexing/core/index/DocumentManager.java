/*******************************************************************************
 * This file is part of the lucene indexing eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.dubture.indexing.core.index;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;

import com.dubture.indexing.core.IndexingCorePlugin;

/**
 * 
 * Main class for indexing and searching lucene documents.
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
public class DocumentManager
{
    protected static DocumentManager instance;
    
    protected IndexWriter writer;
    
    protected IndexReader reader;
    
    protected Map<IFile, ReferenceInfo> pendingReferences;
    
    protected Directory index;
    
    protected IndexSearcher searcher;
    
    protected DocumentManager() throws Exception
    {
        StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_29);
        MaxFieldLength length = new MaxFieldLength(255);
        
        index = getIndex();
        writer = new IndexWriter(index, analyzer, length);
        reader = IndexReader.open(index, true);
        searcher = new IndexSearcher(reader);        
        pendingReferences = new HashMap<IFile, ReferenceInfo>();
                
    }
    
    public void resetIndex()
    {
    	// used for tests
    }
    
    protected Directory getIndex() throws IOException
    {
        if (index != null) {
            return index;
        }
        
        IPath location = IndexingCorePlugin.getDefault().getStateLocation();
        IPath indexPath = location.append("resources/index");
        File indexFile = indexPath.toFile();
            
        if (indexFile.exists() == false && indexFile.mkdirs() == false) {
            throw new IOException("Unable to create lucene index directory " + indexFile.toString());
        }        
        
        return index = FSDirectory.open(indexFile);
    }
    
    public static DocumentManager getInstance() throws Exception
    {
        if (instance == null) {
            instance = new DocumentManager();
        }
        
        return instance;
    }

    public void deleteReferences(IFile file, String type) throws Exception
    {
        Query query = QueryBuilder.createDeleteReferencesQuery(file, type);
        
        writer.deleteDocuments(query);
        writer.commit();
        
        updateReader();
    }
    
    protected void updateReader()
    {
        try {
            
            IndexReader newReader = reader.reopen();
            
            if (newReader != reader) {
                reader.close();
                reader = newReader;
                searcher = new IndexSearcher(reader);
                IndexingCorePlugin.debug("Updated reader");
            }
        } catch (Exception e) {
            IndexingCorePlugin.logException(e);
        }
    }
    
    public void addReference(IFile file, ReferenceInfo reference) throws Exception
    {
        pendingReferences.put(file, reference);
    }

    public void flush() throws Exception
    {
        flushReferences();
        writer.commit();
        updateReader();
    }
    
    @SuppressWarnings("rawtypes")
    protected void flushReferences() throws Exception
    {
        Iterator it = pendingReferences.keySet().iterator();
        
        while(it.hasNext()) {
            
            IFile file = (IFile) it.next();
            ReferenceInfo ref = pendingReferences.get(file);
            addDocument(file, ref);
        }
        pendingReferences.clear();        
    }
    
    protected void addDocument(IFile file, ReferenceInfo ref) throws Exception
    {
        if (ref.type == null || ref.name == null) {
            IndexingCorePlugin.debug("Reference info failure: " + ref.metadata);
            return;
        }
        
        Document doc = new Document();
//        String path = file.getFullPath().removeLastSegments(1).toString();
        String path = file.getFullPath().toString();
        
        IndexingCorePlugin.debug("indexing document with path " + path);
        doc.add(new Field(IndexField.PATH, path, Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field(IndexField.FILENAME, file.getName(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        
        doc.add(new Field(IndexField.TYPE, ref.getType(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field(IndexField.REFERENCENAME, ref.name, Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field(IndexField.METADATA, ref.getMetadata(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        
        IndexingCorePlugin.debug("Indexing reference " + doc.toString());            
        writer.addDocument(doc);
    }
    
    public void search(Query pathQuery, final IResultHandler handler)
    {
        //TODO: howto handle this?
        int hitsPerPage = 100;
        TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
        
        try {
            searcher.search(pathQuery, collector);
            ScoreDoc[] hits = collector.topDocs().scoreDocs;
            
            for(int i=0;i<hits.length;++i) {
                int docId = hits[i].doc;
                handler.handle(searcher.doc(docId));
            }
        } catch (IOException e) {
            IndexingCorePlugin.logException(e);
        }
    }
    
    public void shutdown()
    {
        try {
            writer.close();
        } catch (Exception e) {
            IndexingCorePlugin.logException(e);
        }
    }

    public IndexReader getReader()
    {
        return reader;
    }
    
    public IndexWriter getWriter()
    {
        return writer;
    }
}
