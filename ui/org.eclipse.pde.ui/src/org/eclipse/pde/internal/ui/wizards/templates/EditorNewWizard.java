package org.eclipse.pde.internal.ui.wizards.templates;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.pde.internal.ui.PDEPlugin;
import org.eclipse.pde.ui.*;
import org.eclipse.pde.ui.templates.*;

public class EditorNewWizard extends NewPluginTemplateWizard {
	private static final String KEY_WTITLE = "EditorNewWizard.wtitle";

	/**
	 * Constructor for HelloWorldNewWizard.
	 */
	public EditorNewWizard() {
		super();
	}
	public void init(
		IProjectProvider provider,
		IPluginStructureData structureData,
		boolean fragment,
		IConfigurationElement config) {
		super.init(provider, structureData, fragment, config);
		setWindowTitle(PDEPlugin.getResourceString(KEY_WTITLE));
	}

	/*
	 * @see NewExtensionTemplateWizard#createTemplateSections()
	 */
	public ITemplateSection[] createTemplateSections() {
		return new ITemplateSection[] { new EditorTemplate()};
	}
}