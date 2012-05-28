/*******************************************************************************
 * This file is part of the lucene indexing eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.dubture.indexing.core.index;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;

public class DocumentManager
{
    private static DocumentManager instance;
    
    private IndexWriter writer;
    
    private Map<IFile, ReferenceInfo> pendingReferences;
    
    private DocumentManager() throws Exception
    {
        StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_29);
        Directory index = new RAMDirectory();
        MaxFieldLength length = new MaxFieldLength(255);
        writer = new IndexWriter(index, analyzer, length);
        pendingReferences = new HashMap<IFile, ReferenceInfo>();
                
    }
    
    public static DocumentManager getInstance() throws Exception
    {
        if (instance == null) {
            instance = new DocumentManager();
        }
        
        return instance;
    }

    public void deleteByPath(IPath fullPath)
    {
        
    }
    
    public IndexWriter getWriter()
    {
        return writer;
    }

    public void addReference(IFile file, ReferenceInfo reference) throws Exception
    {
        pendingReferences.put(file, reference);
    }

    public void flush() throws Exception
    {
        Iterator it = pendingReferences.keySet().iterator();
        
        while(it.hasNext()) {
            IFile file = (IFile) it.next();
            ReferenceInfo ref = pendingReferences.get(file);
            
            Document doc = new Document();
            doc.add(new Field("path", file.getFullPath().toString(), Field.Store.YES, Field.Index.ANALYZED));
            doc.add(new Field("name", ref.name, Field.Store.YES, Field.Index.ANALYZED));
            writer.addDocument(doc);
        }
        
        System.err.println("flushed " + pendingReferences.size() + " documents");
        writer.close();
        pendingReferences.clear();
        
    }
}
