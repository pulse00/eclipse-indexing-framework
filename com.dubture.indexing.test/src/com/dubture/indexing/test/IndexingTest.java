/*******************************************************************************
 * This file is part of the lucene indexing eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.dubture.indexing.test;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

import com.dubture.indexing.core.index.DocumentManager;
import com.dubture.indexing.core.index.ReferenceInfo;
import com.dubture.indexing.core.search.SearchEngine;

public class IndexingTest extends TestCase
{
    
    @Test
    public void testIndexing()
    {
        try {
            
            IPath path = new Path("/test/foo/bar");
            IFile file = new MockFile("services.xml", path);
           
            DocumentManager manager = TestDocumentManager.getInstance();
            assertEquals(0, manager.getReader().numDocs());
            
            ReferenceInfo reference = new ReferenceInfo();
            reference.setName("session");
            
            manager.addReference(file, reference);
            manager.flush();
            
            manager.deleteReferences(file);
            assertEquals(manager.getReader().numDocs(), 0);
            
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
    
    @Test
    public void testSearch()
    {
        try {
            
            IPath path = new Path("/test/foo/bar");
            IFile file = new MockFile("services.xml", path);
           
            DocumentManager manager = TestDocumentManager.getInstance();
            SearchEngine searchEngine = SearchEngine.getInstance();
            assertEquals(0, manager.getReader().numDocs());
            
            ReferenceInfo reference = new ReferenceInfo();
            reference.setName("session");
            
            manager.addReference(file, reference);
            manager.flush();
            
            IPath searchPath = new Path("/test/foo/bar");
            List<ReferenceInfo> references = searchEngine.findReferences(searchPath);
            assertEquals(1, references.size());
            
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}