/*******************************************************************************
 * This file is part of the lucene indexing eclipse plugin.
 * 
 * (c) Robert Gruendler <r.gruendler@gmail.com>
 * 
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 ******************************************************************************/
package com.dubture.indexing.core.build;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.w3c.dom.Document;

import com.dubture.indexing.core.ExtensionManager;
import com.dubture.indexing.core.IndexingCorePlugin;
import com.dubture.indexing.core.index.JsonIndexingVisitor;
import com.dubture.indexing.core.index.LuceneIndexingRequestor;
import com.dubture.indexing.core.index.XmlIndexingVisitor;
import com.dubture.indexing.core.xml.PositionalXMLReader;
import com.google.gson.Gson;

public class IndexingBuilder extends IncrementalProjectBuilder
{
    public IndexingBuilder()
    {
    }

    @Override
    protected IProject[] build(int kind, Map<String, String> args,
            IProgressMonitor monitor) throws CoreException
    {
        
        if (kind == FULL_BUILD) {
            fullBuild(monitor);
        } else {
            IResourceDelta delta = getDelta(getProject());
            if (delta == null) {
                fullBuild(monitor);
            } else {
                incrementalBuild(delta, monitor);
            }
        }
        
        return null;
    }
    
    protected void fullBuild(final IProgressMonitor monitor)
            throws CoreException {
        try {
            getProject().accept(new IndexingResourceVisitor());
        } catch (CoreException e) {
        }
    }

    protected void incrementalBuild(IResourceDelta delta,
            IProgressMonitor monitor) throws CoreException {
        // the visitor does the work.
        delta.accept(new IndexingDeltaVisitor());
    }
    
    private void deleteReferences(IFile file) throws Exception
    {
        for (BuildParticipant builder : ExtensionManager.getInstance().getBuildParticipants()) {
            
            LuceneIndexingRequestor requestor = new LuceneIndexingRequestor(file);
            
            if ("xml".equals(file.getFileExtension()) && builder.hasXmlVisitor()) {
                
                XmlIndexingVisitor visitor = builder.getXmlVisitor();
                visitor.setRequestor(requestor).setResource(file);
                visitor.resourceDeleted(file);
                
            } else if ("json".equals(file.getFileExtension()) && builder.hasJsonVisitor()) {

                JsonIndexingVisitor visitor = builder.getJsonVisitor();
                visitor.setRequestor(requestor).setResource(file);
                visitor.resourceDeleted(file);
            }
            
            requestor.flush();
        }
    }
    
    private void callParticipants(IFile file) throws Exception
    {
        for (BuildParticipant builder : ExtensionManager.getInstance().getBuildParticipants()) {
            
            LuceneIndexingRequestor requestor = new LuceneIndexingRequestor(file);
            
            if ("xml".equals(file.getFileExtension()) && builder.hasXmlVisitor()) {
                
                FileInputStream fis = new FileInputStream(file.getLocation().toFile());
                Document doc = PositionalXMLReader.readXML(fis);
                XmlIndexingVisitor visitor = builder.getXmlVisitor();
                visitor.setRequestor(requestor).setResource(file);
                visitor.visit(doc);
                
            } else if ("json".equals(file.getFileExtension()) && builder.hasJsonVisitor()) {
                
                JsonIndexingVisitor visitor = builder.getJsonVisitor();
                visitor.setRequestor(requestor).setResource(file);
                Gson gson = visitor.getBuilder();
                InputStreamReader reader = new InputStreamReader(file.getContents());
                Object object = gson.fromJson(reader, visitor.getTransformerClass());
                visitor.visit(object);
            }
            
            requestor.flush();
        }
    }
    
    class IndexingDeltaVisitor implements IResourceDeltaVisitor {
        /*
         * (non-Javadoc)
         * 
         * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
         */
        public boolean visit(IResourceDelta delta) throws CoreException {
            
            IResource resource = delta.getResource();
            
            switch (delta.getKind()) {
                case IResourceDelta.ADDED:
                	
                    // handle added resource
                    if (resource instanceof IFile) {
                        try {
                            callParticipants((IFile) resource);
                        } catch (Exception e) {
                        	IndexingCorePlugin.logException(e);
                        }
                    }
                    break;
                case IResourceDelta.REMOVED:
                	
                	if (resource instanceof IFile) {
                		try {
							deleteReferences((IFile) resource);
						} catch (Exception e) {
							IndexingCorePlugin.logException(e);
						}
                	}
                    break;
                case IResourceDelta.CHANGED:
                    
                    if (resource instanceof IFile) {
                        try {
                            callParticipants((IFile) resource);
                        } catch (Exception e) {
                        	IndexingCorePlugin.logException(e);
                        }
                    }
                    break;
            }
            //return true to continue visiting children.
            return true;
        }
    }

    class IndexingResourceVisitor implements IResourceVisitor {
        
        public boolean visit(IResource resource) {
            
            if (resource instanceof IFile) {
                try {
                    callParticipants((IFile) resource);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            //return true to continue visiting children.
            return true;
        }
    }
}
