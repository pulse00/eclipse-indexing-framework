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

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.framework.BundleContext;

/**
 * Core Plugin. Adds the IndexingBuilder to project which implement the buildParticipant
 * extension point.
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
public class IndexingCorePlugin extends Plugin {

	private static final String BUILD_PARTICIPANT = "com.dubture.indexing.core.buildParticipant";
	private static final String BUILDER_ID = "com.dubture.indexing.core.indexingBuilder";
	

    private static BundleContext context;
	
	public IndexingCorePlugin plugin;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
	    
	    plugin = this;
		IndexingCorePlugin.context = bundleContext;
		
		Job job = new Job("Setting up lucene builders...")
        {
            @Override
            protected IStatus run(IProgressMonitor monitor)
            {
                try {
                    setupBuilders();
                } catch (CoreException e) {
                    e.printStackTrace();
                    return Status.CANCEL_STATUS;
                }
                return Status.OK_STATUS;
            }
        };
        
        job.schedule();
	}
	
	private void setupBuilders() throws CoreException
	{
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        
        IConfigurationElement[] config = Platform.getExtensionRegistry()
                .getConfigurationElementsFor(BUILD_PARTICIPANT);        

        List<String> natures = new ArrayList<String>();
        
        try {                           
            
            for (IConfigurationElement element : config) {
                String nature = element.getAttribute("nature_id");
                if (nature != null) {
                    natures.add(nature);
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        
        for (IProject project : workspace.getRoot().getProjects()) {
            for (String nature : natures) {
                if (project.hasNature(nature)) {
                    try {
                        addBuilder(project);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
	}

	private void addBuilder(IProject project) throws CoreException
    {
	    IProjectDescription desc = project.getDescription();
	      ICommand[] commands = desc.getBuildSpec();
	      for (int i = 0; i < commands.length; ++i)
	         if (commands[i].getBuilderName().equals(BUILDER_ID))
	            return;
	      //add builder to project
	      ICommand command = desc.newCommand();
	      command.setBuilderName(BUILDER_ID);
	      ICommand[] nc = new ICommand[commands.length + 1];
	      // Add it before other builders.
	      System.arraycopy(commands, 0, nc, 1, commands.length);
	      nc[0] = command;
	      desc.setBuildSpec(nc);
	      project.setDescription(desc, null);	    
    }

    /*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		IndexingCorePlugin.context = null;
		plugin = null;
	}
}
