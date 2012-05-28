/*******************************************************************************
 * This file is part of the lucene indexing eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.dubture.indexing.core.index;

import org.eclipse.core.resources.IResource;
import org.w3c.dom.Document;

/**
 * 
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
public interface XmlIndexingVisitor
{
    XmlIndexingVisitor setRequestor(IIndexingRequestor requestor);
    
    void visit(Document doc);
    
    IResource getResource();
    
    XmlIndexingVisitor setResource(IResource resource);
}
