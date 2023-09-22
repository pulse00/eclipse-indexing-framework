/*******************************************************************************
 * This file is part of the lucene indexing eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.dubture.indexing.test;

import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;

import com.dubture.indexing.core.index.DocumentManager;

public class TestDocumentManager extends DocumentManager
{

    protected TestDocumentManager() throws Exception
    {
        super();
    }
    
    public void resetIndex()
    {
    	try {
			index.close();
			index = null;
			init();
		} catch (Exception e) {
			
		}
    }
 
    
    @Override
    protected Directory getIndex()
    {
        if (index != null) {
            return index;
        }
        
        return index = new ByteBuffersDirectory(); 
    }
    
    public static DocumentManager getInstance() throws Exception
    {
        if (instance == null) {
            instance = new TestDocumentManager();
        }
        
        return instance;
    }
}
