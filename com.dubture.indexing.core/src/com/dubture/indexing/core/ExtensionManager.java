/*******************************************************************************
 * This file is part of the lucene indexing eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.dubture.indexing.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import com.dubture.indexing.core.build.BuildParticipant;
import com.dubture.indexing.core.index.XmlIndexingVisitor;

/**
 * 
 * Manage the retrieval of plugin extensions.
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
public class ExtensionManager
{
    private static ExtensionManager instance;
    private List<BuildParticipant> participants;
    
    private ExtensionManager()
    {
        initBuildParticipants();
    }
    
    private void initBuildParticipants()
    {
        participants = new ArrayList<BuildParticipant>();
        IConfigurationElement[] config = Platform.getExtensionRegistry()
                .getConfigurationElementsFor(BuildParticipant.BUILD_PARTICIPANT_ID);        

        try {                           
            
            for (IConfigurationElement element : config) {
                String nature = element.getAttribute("nature_id");
                String extensions = element.getAttribute("file_extensions");
                
                XmlIndexingVisitor xmlVisitor = (XmlIndexingVisitor) element.createExecutableExtension("xml_visitor");
                
                if (nature != null) {
                    
                    boolean add = true;
                    
                    for (BuildParticipant existing : participants) {
                        if (existing.getNature().equals(nature)) {
                            add = false;
                            break;
                        }
                    }
                    
                    if (add) {
                        
                        BuildParticipant participant = new BuildParticipant(nature, extensions);
                        
                        if (xmlVisitor != null) {
                            participant.setXmlVisitor(xmlVisitor);
                        }
                        
                        participants.add(participant);
                    }
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        
    }
    
    public static ExtensionManager getInstance()
    {
        if (instance == null) {
            instance = new ExtensionManager();
        }
        
        return instance;
    }

    public List<BuildParticipant> getBuildParticipants()
    {
        return participants;
    }

}
