/*******************************************************************************
 * This file is part of the lucene indexing eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.dubture.indexing.core;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.framework.BundleContext;

import com.dubture.indexing.core.build.BuildParticipant;
import com.dubture.indexing.core.index.DocumentManager;

/**
 * Core Plugin. Adds the IndexingBuilder to project which implement the buildParticipant
 * extension point.
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
public class IndexingCorePlugin extends Plugin {

	

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
        ExtensionManager manager = ExtensionManager.getInstance();
        
        List<BuildParticipant> participants = manager.getBuildParticipants();
        
        for (IProject project : workspace.getRoot().getProjects()) {
            for (BuildParticipant participant : participants) {
                try {
                    participant.addBuilder(project);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
	}

    /*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
	    
	    DocumentManager.getInstance().shutdown();
		IndexingCorePlugin.context = null;
		plugin = null;
	}
}
