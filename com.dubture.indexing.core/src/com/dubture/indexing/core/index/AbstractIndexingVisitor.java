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


public abstract class AbstractIndexingVisitor implements XmlIndexingVisitor
{
    protected IIndexingRequestor requestor;
    protected IResource resource;
    
    @Override
    public XmlIndexingVisitor setRequestor(IIndexingRequestor requestor)
    {
        this.requestor = requestor;
        return this;
    }

    @Override
    public IResource getResource()
    {
        return resource;
    }
    
    public XmlIndexingVisitor setResource(IResource resource)
    {
        this.resource = resource;
        return this;
    }
}
