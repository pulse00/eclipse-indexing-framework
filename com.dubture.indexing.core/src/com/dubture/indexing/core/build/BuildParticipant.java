/*******************************************************************************
 * This file is part of the lucene indexing eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.dubture.indexing.core.build;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;

import com.dubture.indexing.core.index.JsonIndexingVisitor;
import com.dubture.indexing.core.index.XmlIndexingVisitor;

/**
 * 
 *  A {@link BuildParticipant} is created by other plugins implementing the buildParticipant
 *  extension point.
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
public class BuildParticipant
{
    
    public static final String BUILD_PARTICIPANT_ID = "com.dubture.indexing.core.buildParticipant";
    public static final String BUILDER_ID = "com.dubture.indexing.core.indexingBuilder";

    private String natureId;
    private List<String> extensions;
    private XmlIndexingVisitor xmlVisitor;
    private JsonIndexingVisitor jsonVisitor;
    
    
    /**
     * Creates a BuildParticipant with the corresponding nature and file extensions 
     * @param natureId
     * @param extensions
     */
    public BuildParticipant(String natureId, String extensions) {
        
        this.natureId = natureId;
        this.extensions = new ArrayList<String>();
        
        if (extensions.contains(",")) {
            for (String extension : extensions.split(",")) {
                this.extensions.add(extension);
            }
        } else {
            this.extensions.add(extensions);
        }
    }
    
    public List<String> getExtensions()
    {
        return extensions;
    }
    
    public boolean hasExtension(String extension)
    {
        return this.extensions.contains(extension);
    }
    
    public boolean hasNature(String nature)
    {
        return natureId.equals(nature);
    }

    public String getNature()
    {
        return natureId;
    }
    
    /**
     * Add the lucene builder to the project if it has the required nature ID
     * 
     * @param project
     * @throws CoreException
     */
    public void addBuilder(IProject project) throws CoreException
    {
        if (!project.hasNature(natureId)) {
            return;
        }
        
        IProjectDescription desc = project.getDescription();
        ICommand[] commands = desc.getBuildSpec();
        
        for (int i = 0; i < commands.length; ++i) {
           if (commands[i].getBuilderName().equals(BUILDER_ID)) {
              return;
           }
        }
        
        //add builder to project
        ICommand command = desc.newCommand();
        command.setBuilderName(BUILDER_ID);
        ICommand[] nc = new ICommand[commands.length + 1];
        
        //TODO: make this configurable
        // Add it before other builders.
        System.arraycopy(commands, 0, nc, 1, commands.length);
        nc[0] = command;
        desc.setBuildSpec(nc);
        project.setDescription(desc, null);       
        
    }

    public void setXmlVisitor(XmlIndexingVisitor xmlVisitor)
    {
        this.xmlVisitor = xmlVisitor;
    }
    
    public boolean hasXmlVisitor()
    {
        return xmlVisitor != null;
    }

    public XmlIndexingVisitor getXmlVisitor()
    {
        return xmlVisitor;
    }

    public boolean hasJsonVisitor()
    {
        return jsonVisitor != null;
    }
    
    public JsonIndexingVisitor getJsonVisitor()
    {
        return jsonVisitor;
    }

    public void setJsonVisitor(JsonIndexingVisitor jsonVisitor)
    {
        this.jsonVisitor = jsonVisitor;
    }
}
