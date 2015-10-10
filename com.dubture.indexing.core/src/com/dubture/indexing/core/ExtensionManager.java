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
import com.dubture.indexing.core.index.IndexingVisitor;

/**
 * 
 * Manage the retrieval of plugin extensions.
 * 
 * @author Robert Gruendler <r.gruendler@gmail.com>
 *
 */
public class ExtensionManager {
	private static ExtensionManager instance;
	private List<BuildParticipant> participants;

	private ExtensionManager() {
		initBuildParticipants();
	}

	private void initBuildParticipants() {
		participants = new ArrayList<BuildParticipant>();
		IConfigurationElement[] config = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(BuildParticipant.BUILD_PARTICIPANT_ID);

		try {

			for (IConfigurationElement element : config) {

				String nature = element.getAttribute("nature_id");
				String extensions = element.getAttribute("file_extensions");
				IndexingVisitor visitor = (IndexingVisitor) element.createExecutableExtension("visitor");

				if (nature != null) {

					boolean add = true;

					for (BuildParticipant existing : participants) {
						if (existing.getNature().equals(nature)) {
							add = false;
							break;
						}
					}

					if (add == false) {
						continue;
					}

					BuildParticipant participant = new BuildParticipant(nature, extensions, visitor);
					participants.add(participant);
				}
			}
		} catch (Exception e1) {
			IndexingCorePlugin.logException(e1);
		}
	}

	public static ExtensionManager getInstance() {
		if (instance == null) {
			instance = new ExtensionManager();
		}

		return instance;
	}

	public List<BuildParticipant> getBuildParticipants() {
		return participants;
	}
}
