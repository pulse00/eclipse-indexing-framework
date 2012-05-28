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
     * The transformed POJO.
     * 
     * @param object can be safely cast to your implemetation object.
     */
    void visit(Object object);
    
    /**
     * The mapped POJO.
     */
    Class<?> getTransformerClass();
}
