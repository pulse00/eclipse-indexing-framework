/*******************************************************************************
 * This file is part of the lucene indexing eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.dubture.indexing.test;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import com.dubture.indexing.core.index.DocumentManager;

public class TestDocumentManager extends DocumentManager
{

    protected TestDocumentManager() throws Exception
    {
        super();
    }
    
    @Override
    protected Directory getIndex()
    {
        if (index != null) {
            return index;
        }
        
        return index = new RAMDirectory(); 
    }
    
    public static DocumentManager getInstance() throws Exception
    {
        if (instance == null) {
            instance = new TestDocumentManager();
        }
        
        return instance;
    }
}
