/*******************************************************************************
 * This file is part of the lucene indexing eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.dubture.indexing.core.index;



/**
 * 
 * Represents a reference to an model element somewhere in a resource. 
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
public class ReferenceInfo
{
    protected String name;
    protected String type;
    protected String metadata;
    
    public ReferenceInfo(String type, String name, String metadata)
    {
        setName(name);
        setType(type);
        setMetadata(metadata);
    }
    
    public ReferenceInfo(String type, String name)
    {
        this(type, name, "");
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getMetadata()
    {
        return metadata;
    }

    public void setMetadata(String metadata)
    {
        this.metadata = metadata;
    }
}
