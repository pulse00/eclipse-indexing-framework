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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
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
    
    protected Map<IFile, List<ReferenceInfo> > pendingReferences;
    
    protected Directory index;
    
    protected IndexSearcher searcher;
    
    protected DocumentManager() throws Exception
    {
        init();
    }
    
    protected void init() throws Exception {
    	
    	StandardAnalyzer analyzer = new StandardAnalyzer();
    	
    	index = getIndex();
    	IndexWriterConfig config = new IndexWriterConfig(analyzer);
    	writer = new IndexWriter(index, config);
    	writer.commit();
    	
    	reader = DirectoryReader.open(writer);
    	searcher = new IndexSearcher(reader);        
    	pendingReferences = new HashMap<IFile, List<ReferenceInfo>>();
    	
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
        
        
        return index = FSDirectory.open(Paths.get(indexFile.getPath()));
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
        
        IndexingCorePlugin.debug("deleting references for " + file.getFullPath().toString() + " => " + type);
        writer.deleteDocuments(query);
        writer.commit();
        
        updateReader();
    }
    
    protected void updateReader()
    {
    	// XXX : reimplement or remove
    }
    
    public void addReference(IFile file, ReferenceInfo reference) throws Exception
    {
    	IndexingCorePlugin.debug("Adding pending reference " + reference.name);
    	
    	if (!pendingReferences.containsKey(file)) {
    		pendingReferences.put(file, new ArrayList<ReferenceInfo>());
    	}
    	
    	List<ReferenceInfo> list = pendingReferences.get(file);
    	list.add(reference);
        pendingReferences.put(file, list);
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
        
        IndexingCorePlugin.debug("flushing " + pendingReferences.size() + " references");
        while(it.hasNext()) {
            
            IFile file = (IFile) it.next();
            List<ReferenceInfo> refs = pendingReferences.get(file);
            
            for (ReferenceInfo info : refs) {
            	addDocument(file, info);
            }
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
        String path = file.getFullPath().toString();
        
        IndexingCorePlugin.debug("indexing document with path " + path);
        doc.add(new StringField(IndexField.PATH, path, Field.Store.YES));
        doc.add(new StringField(IndexField.FILENAME, file.getName(), Field.Store.YES));
        
        doc.add(new StringField(IndexField.TYPE, ref.getType(), Field.Store.YES));
        doc.add(new StringField(IndexField.REFERENCENAME, ref.name, Field.Store.YES));
        doc.add(new StringField(IndexField.METADATA, ref.getMetadata(), Field.Store.YES));
        
        IndexingCorePlugin.debug("Indexing reference " + doc.toString());            
        writer.addDocument(doc);
    }
    
    public void search(Query pathQuery, final IResultHandler handler)
    {
        //TODO: howto handle this?
        int hitsPerPage = 100;
        TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
        
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
