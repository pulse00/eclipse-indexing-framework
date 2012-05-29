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

import com.dubture.indexing.core.IndexingCorePlugin;

/**
 * 
 * An IndexingRequestor which uses lucene.
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
public class LuceneIndexingRequestor implements IIndexingRequestor
{
    protected IFile file;
    
    protected DocumentManager manager;

    public LuceneIndexingRequestor(IFile file)
    {
        try {
            this.file = file;
            manager = DocumentManager.getInstance();
            manager.deleteReferences(file);
        } catch (Exception e) {
            IndexingCorePlugin.logException(e);
        }
    }

    @Override
    public void addReference(ReferenceInfo reference)
    {
        try {
            manager.addReference(file, reference);
        } catch (Exception e) {
            IndexingCorePlugin.logException(e);
        }
    }

    @Override
    public void flush()
    {
        try {
            manager.flush();
        } catch (Exception e) {
            IndexingCorePlugin.logException(e);
        }
    }    
}