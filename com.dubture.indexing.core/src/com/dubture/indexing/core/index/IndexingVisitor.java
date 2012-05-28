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
    
    IndexingVisitor setRequestor(IIndexingRequestor requestor);
    
    IResource getResource();
    
    IndexingVisitor setResource(IResource resource);
    
    void visit(IFile file);

}
