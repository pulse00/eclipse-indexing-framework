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
import org.eclipse.core.runtime.IPath;

import com.dubture.indexing.core.index.DocumentManager;
import com.dubture.indexing.core.index.IResultHandler;
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
        final List<ReferenceInfo> references = new ArrayList<ReferenceInfo>();
        
        manager.search(manager.getPathQuery(path), new IResultHandler()
        {
            
            @Override
            public void handle(Document document)
            {
                ReferenceInfo info = new ReferenceInfo();
                info.setName(document.get("name"));
                references.add(info);
            }
        });
        
        return references;
    }
}
