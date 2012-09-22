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
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
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

	public static IndexingCorePlugin plugin;
	
	public static final String ID = "com.dubture.indexing.core";

    private static final String DEBUG = "com.dubture.indexing.core/debug";

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
	    
	    super.start(bundleContext);
	    plugin = this;
		
		Job job = new Job("Setting up lucene builders...")
        {
            @Override
            protected IStatus run(IProgressMonitor monitor)
            {
                try {
                    setupBuilders();
                } catch (CoreException e) {
                    IndexingCorePlugin.logException(e);
                    return Status.CANCEL_STATUS;
                }
                return Status.OK_STATUS;
            }
        };
        
        job.schedule();
        
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
	    
	    super.stop(bundleContext);
	    
	    DocumentManager.getInstance().shutdown();
		plugin = null;
	}
	
	public static IndexingCorePlugin getDefault()
	{
	    return plugin;
	}
	
	public static void debug(String message)
	{
	    if (plugin == null) {
	        // tests
	        System.err.println(message);
	        return;
	    }
	    
	    String debugOption = Platform.getDebugOption(DEBUG);
	    
	    if (plugin.isDebugging() && "true".equalsIgnoreCase(debugOption)) {
	        plugin.getLog().log(new Status(Status.INFO, ID, message));
	    }
	}
	
	public static void logException(Exception e) 
	{
	    IStatus status = new Status(Status.ERROR, IndexingCorePlugin.ID, e.getMessage(), e); 
	    plugin.getLog().log(status);
	}
	
    private void setupBuilders() throws CoreException
    {
        for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
        	setupBuilder(project);
        }
    }
    
    public void setupBuilder(IProject project ) throws CoreException
    {
        ExtensionManager manager = ExtensionManager.getInstance();
        List<BuildParticipant> participants = manager.getBuildParticipants();
        
        for (BuildParticipant participant : participants) {
            participant.addBuilder(project);
        }
    }
}
