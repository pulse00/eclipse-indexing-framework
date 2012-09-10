/*******************************************************************************
 * This file is part of the lucene indexing eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.dubture.indexing.core.index;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;

/**
 * IndexingVisitor interface for buildParticipant extension implementation. 
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
public interface IndexingVisitor
{
	/**
	 * Set the indexingrequestor.
	 * 
	 * @param requestor
	 * @return {@link IndexingVisitor}
	 */
    IndexingVisitor setRequestor(IIndexingRequestor requestor);
    
    /**
     * Get the resource the visitor is operating on.
     * 
     * @return {@link IResource}
     */
    IResource getResource();
    
    /**
     * Sets the resource the visitor is operating on.
     *  
     * @param resource
     * @return {@link IndexingVisitor}
     */
    IndexingVisitor setResource(IResource resource);
    
    
    /**
     * The transformed POJO.
     * 
     * @param object can be safely cast to your implemetation object.
     */
    void visit(Object object);
    
    /**
     * A resource is about to be deleted.
     * 
     * @param file the resource being deleted
     */
    void resourceDeleted(IFile file);
}
