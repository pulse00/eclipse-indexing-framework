/*******************************************************************************
 * This file is part of the lucene indexing eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.dubture.indexing.core;

import org.eclipse.ui.IStartup;

/**
 * 
 * Not sure how to avoid this, but we somehow get aware of the 
 * projects that require this builder.
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
public class IndexingStartup implements IStartup
{
    @Override
    public void earlyStartup()
    {
        
    }
}
