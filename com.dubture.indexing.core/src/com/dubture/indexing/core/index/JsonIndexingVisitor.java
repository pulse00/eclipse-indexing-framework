/*******************************************************************************
 * This file is part of the lucene indexing eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.dubture.indexing.core.index;

import com.google.gson.FieldNamingStrategy;
import com.google.gson.Gson;

/**
 * IndexingVisitor for json files.
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
public interface JsonIndexingVisitor extends IndexingVisitor
{
    /**
     * Implement this if you need some special field name mappings.
     * 
     * @return
     */
    FieldNamingStrategy getFieldNamingStrategy();
    
    /**
     * Retrieve the gson object for your indexer
     * 
     * @return
     */
    Gson getBuilder();
    
    
    /**
     * The mapped POJO.
     */
    Class<?> getTransformerClass();
}
