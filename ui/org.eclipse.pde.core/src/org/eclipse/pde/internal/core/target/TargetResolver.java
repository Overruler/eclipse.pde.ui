package org.eclipse.pde.internal.core.target;

import java.net.URI;
import java.util.*;
import org.eclipse.core.runtime.*;
import org.eclipse.equinox.internal.p2.director.PermissiveSlicer;
import org.eclipse.equinox.internal.provisional.p2.core.ProvisionException;
import org.eclipse.equinox.internal.provisional.p2.metadata.Version;
import org.eclipse.equinox.internal.provisional.p2.metadata.MetadataFactory.InstallableUnitDescription;
import org.eclipse.equinox.internal.provisional.p2.metadata.query.*;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.query.IQueryResult;
import org.eclipse.equinox.p2.repository.IRepository;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepositoryManager;
import org.eclipse.osgi.util.NLS;
import org.eclipse.pde.internal.core.PDECore;
import org.eclipse.pde.internal.core.PDECoreMessages;
import org.eclipse.pde.internal.core.target.provisional.IBundleContainer;
import org.eclipse.pde.internal.core.target.provisional.ITargetDefinition;

/**
 * Helper class for TargetDefinition to encapsulate code for resolving a target.
 * 
 * @since 3.6
 * @see TargetProvisioner
 * @see TargetDefinition
 */
public class TargetResolver {

	private ITargetDefinition fTarget;
	private IProvisioningAgent fAgent;

	private MultiStatus fStatus;
	private List fAllRepos;
	private List fRootIUs;
	private Collection fAvailableIUs;

	/**
	 * Query to find installable units that match a set of id/version pairs stored in InstallableUnitDescription objects
	 */
//	private class IUDescriptionQuery extends MatchQuery {
//		private InstallableUnitDescription[] fDescriptions;
//
//		public IUDescriptionQuery(InstallableUnitDescription[] descriptions) {
//			fDescriptions = descriptions;
//		}
//
//		public boolean isMatch(Object object) {
//			if (!(object instanceof IInstallableUnit))
//				return false;
//			if (fDescriptions == null)
//				return true;
//			IInstallableUnit unit = (IInstallableUnit) object;
//			for (int i = 0; i < fDescriptions.length; i++) {
//				if (fDescriptions[i].getId().equalsIgnoreCase(unit.getId()) && fDescriptions[i].getVersion().equals(unit.getVersion())) {
//
//					// TODO Having problems slicing non-bundle IUs
//					IProvidedCapability[] provided = unit.getProvidedCapabilities();
//					for (int j = 0; j < provided.length; j++) {
//						if (provided[j].getNamespace().equals(P2Utils.CAPABILITY_NS_OSGI_BUNDLE)) {
//							return true;
//						}
//					}
//
//				}
//			}
//			return false;
//		}
//	}

	TargetResolver(ITargetDefinition target) {
		fTarget = target;
		// TODO Get proper agent in target metadata area
		fAgent = (IProvisioningAgent) PDECore.getDefault().acquireService(IProvisioningAgent.SERVICE_NAME);
	}

	public IStatus getStatus() {
		return fStatus;
	}

	public Collection getAvailableIUs() {
		return fAvailableIUs;
	}

	public IStatus resolve(IProgressMonitor monitor) {
		SubMonitor subMon = SubMonitor.convert(monitor, Messages.TargetDefinition_1, 80);
		fStatus = new MultiStatus(PDECore.PLUGIN_ID, 0, Messages.AbstractBundleContainer_0, null);

		try {
			if (fAgent == null) {
				throw new CoreException(new Status(IStatus.ERROR, PDECore.PLUGIN_ID, PDECoreMessages.P2Utils_UnableToAcquireP2Service));
			}

			fAllRepos = new ArrayList();
			fRootIUs = new ArrayList();
			fAvailableIUs = new ArrayList();

			// TODO Handle exceptions more gracefully, or try to continue
			// TODO Give descriptive names to monitor tasks/subtasks

			// Ask locations to generate repositories
			IStatus result = generateRepos(subMon.newChild(20));
			if (!result.isOK()) {
				fStatus.add(result);
			}
			if (subMon.isCanceled()) {
				fStatus.add(Status.CANCEL_STATUS);
				return fStatus;
			}

			// Combine generated repos and explicit repos
			result = loadExplicitRepos(subMon.newChild(20));
			if (!result.isOK()) {
				fStatus.add(result);
			}
			if (subMon.isCanceled()) {
				fStatus.add(Status.CANCEL_STATUS);
				return fStatus;
			}

			// Collect the list of IUs
			result = collectRootIUs(subMon.newChild(20));
			if (!result.isOK()) {
				fStatus.add(result);
			}
			if (subMon.isCanceled()) {
				fStatus.add(Status.CANCEL_STATUS);
				return fStatus;
			}

			// Use slicer/planner to get complete enclosure of IUs
			result = collectAllIUs(subMon.newChild(20));
			if (!result.isOK()) {
				fStatus.add(result);
			}
			if (subMon.isCanceled()) {
				fStatus.add(Status.CANCEL_STATUS);
				return fStatus;
			}

		} catch (CoreException e) {
			fStatus.add(e.getStatus());
		}
		return fStatus;
	}

	private IStatus generateRepos(IProgressMonitor monitor) throws CoreException {
		IBundleContainer[] containers = fTarget.getBundleContainers();
		SubMonitor subMon = SubMonitor.convert(monitor, containers.length);
		for (int i = 0; i < containers.length; i++) {
			IRepository[] currentRepos = containers[i].generateRepositories(fAgent, subMon.newChild(1));
			for (int j = 0; j < currentRepos.length; j++) {
				fAllRepos.add(currentRepos[j]);
			}
		}
		return Status.OK_STATUS;
	}

	private IStatus loadExplicitRepos(IProgressMonitor monitor) throws CoreException {
		IMetadataRepositoryManager registry = (IMetadataRepositoryManager) fAgent.getService(IMetadataRepositoryManager.SERVICE_NAME);
		if (registry == null) {
			throw new CoreException(new Status(IStatus.ERROR, PDECore.PLUGIN_ID, PDECoreMessages.P2Utils_UnableToAcquireP2Service));
		}

		MultiStatus result = new MultiStatus(PDECore.PLUGIN_ID, 0, "Unable to load repositories", null);

		URI[] explicit = fTarget.getRepositories();
		SubMonitor subMon = SubMonitor.convert(monitor, explicit.length * 3);
		for (int i = 0; i < explicit.length; i++) {
			try {
				fAllRepos.add(registry.loadRepository(explicit[i], subMon.newChild(3)));
			} catch (ProvisionException e) {
				result.add(e.getStatus());
			}
		}
		if (!result.isOK()) {
			if (result.getChildren().length == 1) {
				return result.getChildren()[0];
			}
			return result;
		}
		return Status.OK_STATUS;
	}

	private IStatus collectRootIUs(IProgressMonitor monitor) throws CoreException {
		IBundleContainer[] containers = fTarget.getBundleContainers();
		SubMonitor subMon = SubMonitor.convert(monitor, containers.length);
		for (int i = 0; i < containers.length; i++) {
			InstallableUnitDescription[] currentIUs = containers[i].getRootIUs();
			for (int j = 0; j < currentIUs.length; j++) {
				fRootIUs.add(currentIUs[j]);
			}
			subMon.worked(1);
		}
		return Status.OK_STATUS;
	}

	private IStatus collectAllIUs(IProgressMonitor monitor) {
		if (fAllRepos.size() == 0) {
			fAvailableIUs = new ArrayList(0);
			return Status.OK_STATUS;
		}

		SubMonitor subMon = SubMonitor.convert(monitor, 60);

		// Combine the repositories into a single queryable object
		IQueryable allRepos;
		if (fAllRepos.size() == 1) {
			allRepos = (IMetadataRepository) fAllRepos.get(0);
		} else {
			allRepos = new CompoundQueryable(fAllRepos);
		}

		MultiStatus status = new MultiStatus(PDECore.PLUGIN_ID, 0, "Problems collecting installable units", null);

		// Get the list of root IUs as actual installable units
		// TODO A compound query to save performance here wasn't working correctly, see IUDescriptionQuery
		InstallableUnitDescription[] rootDescriptions = (InstallableUnitDescription[]) fRootIUs.toArray(new InstallableUnitDescription[fRootIUs.size()]);
		List rootUnits = new ArrayList();
		for (int i = 0; i < rootDescriptions.length; i++) {
			InstallableUnitQuery query = new InstallableUnitQuery(rootDescriptions[i].getId(), rootDescriptions[i].getVersion());
			IQueryResult result = allRepos.query(query, null);
			if (result.isEmpty()) {
				status.add(new Status(IStatus.ERROR, PDECore.PLUGIN_ID, NLS.bind("Could not find unit {0} {1} in repositories.", new String[] {rootDescriptions[i].getId(), rootDescriptions[i].getVersion().toString()})));
			}
			rootUnits.add(result.iterator().next());
		}
		subMon.worked(10);

//		IUDescriptionQuery rootIUQuery = new IUDescriptionQuery(rootDescriptions);
//		IQueryResult result = allRepos.query(rootIUQuery, subMon.newChild(10));
//		IInstallableUnit[] rootUnits = (IInstallableUnit[]) result.toArray(IInstallableUnit.class);
//		if (rootDescriptions.length != rootUnits.length) {
//			// TODO Return a warning status?
//		}

		// Create slicer to calculate requirements
		PermissiveSlicer slicer = null;
		Properties props = new Properties();
		// TODO How to handle platform specific problems
//		props.setProperty("osgi.os", fTarget.getOS() != null ? fTarget.getOS() : Platform.getOS()); //$NON-NLS-1$
//		props.setProperty("osgi.ws", fTarget.getWS() != null ? fTarget.getWS() : Platform.getWS()); //$NON-NLS-1$
//		props.setProperty("osgi.arch", fTarget.getArch() != null ? fTarget.getArch() : Platform.getOSArch()); //$NON-NLS-1$
//		props.setProperty("osgi.nl", fTarget.getNL() != null ? fTarget.getNL() : Platform.getNL()); //$NON-NLS-1$
		slicer = new PermissiveSlicer(allRepos, props, true, false, true, true, false);
		subMon.worked(10);

		// Run the slicer and collect units from the result
		IQueryable slice = slicer.slice((IInstallableUnit[]) rootUnits.toArray(new IInstallableUnit[rootUnits.size()]), subMon.newChild(30));
		if (slice == null) {
			status.add(slicer.getStatus());
		} else {
			IQueryResult collector = slice.query(InstallableUnitQuery.ANY, subMon.newChild(10));
			fAvailableIUs = collector.toSet();
		}

		if (!status.isOK()) {
			if (status.getChildren().length == 1) {
				return status.getChildren()[0];
			}
			return status;
		}
		return Status.OK_STATUS;
	}

	public Collection calculateMissingIUs(IProgressMonitor monitor) {
		// TODO Copy logic from other method?
		return null;
	}

	/**
	 * @return List of IMetadataRepositories that were loaded during the resolve
	 */
	public List getResolvedRepositories() {
		return fAllRepos;
	}

	public Collection calculateIncludedIUs() {
		// TODO Move to TargetDefinition to allow cacheing 
		// TODO We no longer support returning a status for missing included bundles
		// VERSION DOES NOT EXIST
//		int sev = IStatus.ERROR;
//		String message = NLS.bind(Messages.AbstractBundleContainer_1, new Object[] {info.getVersion(), info.getSymbolicName()});
//		if (optional) {
//			sev = IStatus.INFO;
//			message = NLS.bind(Messages.AbstractBundleContainer_2, new Object[] {info.getVersion(), info.getSymbolicName()});
//		}
//		return new ResolvedBundle(info, parentContainer, new Status(sev, PDECore.PLUGIN_ID, IResolvedBundle.STATUS_VERSION_DOES_NOT_EXIST, message, null), null, optional, false);

		InstallableUnitDescription[] included = fTarget.getIncluded();
		InstallableUnitDescription[] optional = fTarget.getOptional();
		if (included == null && optional == null) {
			return fAvailableIUs;
		}

		// Map unit names to associated unit
		Map bundleMap = new HashMap(fAvailableIUs.size());
		for (Iterator iterator = fAvailableIUs.iterator(); iterator.hasNext();) {
			IInstallableUnit unit = (IInstallableUnit) iterator.next();
			List list = (List) bundleMap.get(unit.getId());
			if (list == null) {
				list = new ArrayList(3);
				bundleMap.put(unit.getId(), list);
			}
			list.add(unit);
		}

		List includedIUs = new ArrayList();

		// Add included bundles
		if (included == null) {
			includedIUs.addAll(fAvailableIUs);
		} else {
			for (int i = 0; i < included.length; i++) {
				InstallableUnitDescription include = included[i];
				IInstallableUnit bestUnit = determineBestUnit(bundleMap, include);
				if (bestUnit != null) {
					includedIUs.add(bestUnit);
				}
			}
		}

		// Add optional bundles
		if (optional != null) {
			for (int i = 0; i < optional.length; i++) {
				InstallableUnitDescription option = optional[i];
				IInstallableUnit bestUnit = determineBestUnit(bundleMap, option);
				if (bestUnit != null && !includedIUs.contains(bestUnit)) {
					includedIUs.add(bestUnit);
				}
			}
		}

		return includedIUs;
	}

	public IInstallableUnit getUnit(InstallableUnitDescription unit) {
		// Combine the repositories into a single queryable object
		IQueryable allRepos;
		if (fAllRepos.size() == 1) {
			allRepos = (IMetadataRepository) fAllRepos.get(0);
		} else {
			allRepos = new CompoundQueryable(fAllRepos);
		}

		// Look for the requested unit
		InstallableUnitQuery query = new InstallableUnitQuery(unit.getId(), unit.getVersion());
		IQueryResult result = allRepos.query(query, null);
		if (!result.isEmpty()) {
			return (IInstallableUnit) result.iterator().next();
		}
		return null;
	}

	private static IInstallableUnit determineBestUnit(Map unitMap, InstallableUnitDescription info) {
		// TODO We no longer have a way to return a status if a specific included bundle cannot be found
		List list = (List) unitMap.get(info.getId());
		if (list != null) {
			// If there is a version set, select the specific version if available, select newest otherwise 
			if (info.getVersion() != null) {
				Iterator iterator = list.iterator();
				while (iterator.hasNext()) {
					IInstallableUnit unit = (IInstallableUnit) iterator.next();
					if (info.getVersion().equals(unit.getVersion())) {
						return unit;
					}
				}
			}

			// If there is no version set, select newest available
			if (list.size() > 1) {
				// sort the list
				Collections.sort(list, new Comparator() {
					public int compare(Object o1, Object o2) {
						Version v1 = ((IInstallableUnit) o1).getVersion();
						Version v2 = ((IInstallableUnit) o2).getVersion();
						return v1.compareTo(v2);
					}
				});
			}
			// select the last one
			return (IInstallableUnit) list.get(list.size() - 1);
		}
		return null;
	}
}