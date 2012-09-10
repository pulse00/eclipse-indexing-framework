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


public interface IIndexingRequestor
{
    void addReference(ReferenceInfo reference);
    
    void deleteReferences(IFile file, String type);
    
    void flush();

}
