/*******************************************************************************
 * Copyright (c) 2010, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.pde.api.tools.internal.search;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.jdt.internal.core.OverflowingLRUCache;
import org.eclipse.jdt.internal.core.util.LRUCache;
import org.eclipse.pde.api.tools.internal.IApiCoreConstants;
import org.eclipse.pde.api.tools.internal.provisional.ApiPlugin;
import org.eclipse.pde.api.tools.internal.provisional.model.IApiComponent;
import org.eclipse.pde.api.tools.internal.util.FileManager;
import org.eclipse.pde.api.tools.internal.util.Util;

public class UseScanManager {

	private static UseScanCache fApiComponentCache;
	private static UseScanManager fUseScanProcessor;
	private static String tempLocation = "${workspace_loc}/.metadata/.plugins/" + ApiPlugin.PLUGIN_ID + "/ApiUseScans/"; //$NON-NLS-1$ //$NON-NLS-2$

	public static final String STATE_DELIM = "*"; //$NON-NLS-1$
	public static final String LOCATION_DELIM = "|"; //$NON-NLS-1$
	public static final String ESCAPE_REGEX = "\\"; //$NON-NLS-1$
	
	/**
	 * Number of entries to cache in the {@link UseScanCache}
	 */
	public static final int DEFAULT_CACHE_SIZE = 1000;
	
	/**
	 * Cache to maintain the list of least recently used <code>UseScanReferences</code>
	 */
	private static class UseScanCache extends OverflowingLRUCache {

		public UseScanCache(int size) {
			super(size);
		}

		public UseScanCache(int size, int overflow) {
			super(size, overflow);
		}

		protected boolean close(LRUCacheEntry entry) {
			IReferenceCollection references = (IReferenceCollection) entry.value;
			references.clear();
			return true;
		}

		protected LRUCache newInstance(int size, int newOverflow) {
			return new UseScanCache(size, newOverflow);
		}

	}

	private String[] fLocations = null;

	//Singleton
	private UseScanManager() {
	}

	public synchronized static UseScanManager getInstance() {
		if (fUseScanProcessor == null) {
			fUseScanProcessor = new UseScanManager();
			fApiComponentCache = new UseScanCache(DEFAULT_CACHE_SIZE);
		}
		return fUseScanProcessor;
	}

	/**
	 * Returns the references for a given <code>IApiComponent</code>. If it can not find them in cache, they will be fetched from the 
	 * API Use Scans and stored.
	 * @param apiComponent component whose references have to be fetched
	 * @param refTypes reference types for which the references has to be computed in the  given <code>IApiComponent</code>. 
	 * If <code>null</code> or empty, all references will be returned
	 * @param monitor
	 * @return the array of reference descriptors
	 */
	public IReferenceDescriptor[] getExternalDependenciesFor(IApiComponent apiComponent, String[] apiUseTypes, IProgressMonitor monitor) {
		IReferenceCollection references = (IReferenceCollection) fApiComponentCache.get(apiComponent);
		if (references == null) {
			references = apiComponent.getExternalDependencies();
		}
		SubMonitor localmonitor = SubMonitor.convert(monitor, SearchMessages.collecting_external_dependencies, 10);
		try {
			ArrayList unavailableMembers = new ArrayList();
			if (apiUseTypes != null && apiUseTypes.length > 0) {
				for (int i = 0; i < apiUseTypes.length; i++) {
					if (!references.hasReferencesTo(apiUseTypes[i])) {
						unavailableMembers.add(apiUseTypes[i]);
					}
				}
				if (unavailableMembers.size() > 0) {
					fetch(apiComponent, (String[]) unavailableMembers.toArray(new String[unavailableMembers.size()]), references, monitor);
				}
				Util.updateMonitor(localmonitor, 1);
				return references.getExternalDependenciesTo(apiUseTypes);
			} else {
				fetch(apiComponent, null, references, localmonitor.newChild(8)); // full build has been triggered so re-fetch
				Util.updateMonitor(localmonitor, 1);
				return references.getAllExternalDependencies();
			}
		}
		finally {
			localmonitor.done();
		}
	}

	/**
	 * fetches the references from the API Use Scan locations
	 * @param apiComponent
	 * @param member
	 * @param references
	 * @param monitor
	 */
	private void fetch(IApiComponent apiComponent, String[] types, IReferenceCollection references, IProgressMonitor monitor) {
		UseScanParser parser = new UseScanParser();
		UseScanReferenceVisitor visitor = new UseScanReferenceVisitor(apiComponent, types, references);
		SubMonitor localmonitor = SubMonitor.convert(monitor, SearchMessages.load_external_dependencies, 10);
		try {
			String[] locations;
			if (fLocations == null) {
				locations = getReportLocations();
			}
			else {
				locations = fLocations;
			}
			if (locations != null) {
				IStringVariableManager stringManager = null;
				localmonitor.setWorkRemaining(locations.length * 2);
				for (int i = 0; i < locations.length; i++) {
					Util.updateMonitor(localmonitor, 1);
					File file = new File(locations[i]);
					if (!file.exists()) {
						continue;
					}
					if (file.isFile()) {
						if (Util.isArchive(file.getName())) {
							String destDirPath = tempLocation + file.getName();
							if (stringManager == null) {
								stringManager = VariablesPlugin.getDefault().getStringVariableManager();
							}
							destDirPath = stringManager.performStringSubstitution(destDirPath);
							locations[i] = destDirPath + '/' + file.lastModified();
							File unzipDirLoc = new File(destDirPath);
							if (unzipDirLoc.exists()) {
								String[] childDirs = unzipDirLoc.list();
								for (int j = 0; j < childDirs.length; j++) {
									if (!childDirs[j].equals(String.valueOf(file.lastModified()))) {
										FileManager.getManager().recordTempFileRoot(destDirPath + '/' + childDirs[j]);
									}
								}
							} else {
								Util.unzip(file.getPath(), locations[i]);
							}							
						} else {
							continue;
						}
					}
					try {
						locations[i] = getExactScanLocation(locations[i]);
						parser.parse(locations[i], localmonitor.newChild(2), visitor);
						Util.updateMonitor(localmonitor);
					} catch (Exception e) {
						ApiPlugin.log(e); // log the exception and continue with next location
					}
				}
				fApiComponentCache.remove(apiComponent); // remove current value so that it only doesn't gets purged if size limit is reached
				fApiComponentCache.put(apiComponent, references);
			}
		} catch (Exception e) {
			ApiPlugin.log(e);
		}
		finally {
			localmonitor.done();
		}
	}

	public String getExactScanLocation(String location) {
		IPath path = new Path(location);
		if (path.lastSegment().equalsIgnoreCase(IApiCoreConstants.XML)) {
			return location;
		}
		File reportDir = path.append(IApiCoreConstants.XML).toFile();
		if (reportDir.exists()) {
			if (reportDir.isDirectory()) {
					return reportDir.getAbsolutePath();
			}
		} else {
			reportDir = path.toFile();
			File[] reportDirChildren = reportDir.listFiles();
			if (reportDirChildren != null && reportDirChildren.length == 1) {
				reportDir = path.append(reportDirChildren[0].getName()).append(IApiCoreConstants.XML).toFile();
				if (reportDir.isDirectory()) 
					return reportDir.getAbsolutePath();
			}
		} 
		return null;
	}

	/**
	 * Returns the report locations from the preferences
	 * @return
	 */
	public String[] getReportLocations() {
		IEclipsePreferences node = InstanceScope.INSTANCE.getNode(ApiPlugin.PLUGIN_ID);
		String apiUseScanPaths = node.get(IApiCoreConstants.API_USE_SCAN_LOCATION, null);
		if (apiUseScanPaths == null || apiUseScanPaths.length() == 0) {
			return new String[0];
		}
		
		String[] locations = apiUseScanPaths.split(ESCAPE_REGEX + LOCATION_DELIM);
		ArrayList locationList = new ArrayList(locations.length);
		for (int i = 0; i < locations.length; i++) {
			String values[] = locations[i].split(ESCAPE_REGEX + STATE_DELIM);
			if (Boolean.valueOf(values[1]).booleanValue())
				locationList.add(values[0]);
		}
		return (String[]) locationList.toArray(new String[locationList.size()]);
	}

	/**
	 * Sets the report locations to be used. Once set, these locations will be used instead of ones in the preference.
	 * When set to <code>null</code>, the locations in preference will be used.
	 * @param locations
	 */
	public void setReportLocations(String[] locations) {
		fLocations = locations;
	}

	/**
	 * Sets the cache size
	 * @param size The total number of references that can be held in memory
	 */
	public void setCacheSize(int size) {
		fApiComponentCache.setSpaceLimit(size);
	}

	/**
	 * Purges all reference information
	 */
	public void clearCache() {
		Enumeration elementss = fApiComponentCache.elements();
		while (elementss.hasMoreElements()) {
			IReferenceCollection reference = (IReferenceCollection) elementss.nextElement();
			reference.clear();
		}
		fApiComponentCache.flush();
	}
}