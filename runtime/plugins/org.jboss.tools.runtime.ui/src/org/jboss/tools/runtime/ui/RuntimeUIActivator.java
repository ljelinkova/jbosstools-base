/*************************************************************************************
 * Copyright (c) 2010-2011 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 ************************************************************************************/
package org.jboss.tools.runtime.ui;

import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.osgi.service.debug.DebugOptions;
import org.eclipse.osgi.service.debug.DebugOptionsListener;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.foundation.core.plugin.log.IPluginLog;
import org.jboss.tools.foundation.ui.plugin.BaseUIPlugin;
import org.jboss.tools.foundation.ui.plugin.BaseUISharedImages;
import org.jboss.tools.runtime.core.RuntimeCoreActivator;
import org.jboss.tools.runtime.core.model.IRuntimeDetector;
import org.jboss.tools.runtime.core.model.RuntimeDefinition;
import org.jboss.tools.runtime.core.model.RuntimeModel;
import org.jboss.tools.runtime.core.model.RuntimePath;
import org.jboss.tools.runtime.core.util.RuntimeModelUtil;
import org.jboss.tools.runtime.ui.dialogs.SearchRuntimePathDialog;
import org.jboss.tools.runtime.ui.download.DownloadRuntimes;
import org.jboss.tools.runtime.ui.internal.Trace;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author snjeza
 * 
 */
public class RuntimeUIActivator extends BaseUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.jboss.tools.runtime.ui"; //$NON-NLS-1$

	// The shared instance
	private static RuntimeUIActivator plugin;
	private BundleContext context;
	private RuntimeModel runtimeModel;
	
	/**
	 * The constructor
	 */
	public RuntimeUIActivator() {
	}
	
	public BundleContext getBundleContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		this.context = context;
		RuntimeCoreActivator.getDefault().setDownloader(new DownloadRuntimes());
		
		// register the debug options listener
		final Hashtable<String, String> props = new Hashtable<String, String>(4);
		props.put(DebugOptions.LISTENER_SYMBOLICNAME, PLUGIN_ID);
		context.registerService(DebugOptionsListener.class.getName(), new Trace(), props);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		RuntimeCoreActivator.getDefault().setDownloader(null);
		saveRuntimePreferences();
		runtimeModel = null;
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static RuntimeUIActivator getDefault() {
		return plugin;
	}

	public RuntimeModel getModel() {
		if (runtimeModel == null) {
			runtimeModel = new RuntimeModel(ConfigurationScope.INSTANCE.getNode(PLUGIN_ID));
		}
		return runtimeModel;
	}

	@Deprecated
	public static SearchRuntimePathDialog launchSearchRuntimePathDialog(Shell shell, 
			final RuntimePath[] runtimePaths, boolean needRefresh, int heightHint) {
		return SearchRuntimePathDialog.launchSearchRuntimePathDialog(shell, runtimePaths, needRefresh, heightHint);
	}
	
	public static IPluginLog pluginLog() {
		return getDefault().pluginLogInternal();
	}
	

	public void saveRuntimePreferences() {
		getModel().saveRuntimePaths();
		RuntimeCoreActivator.getDefault().saveEnabledDetectors();
	}
	
	@Override
	public RuntimeSharedImages getSharedImages() {
		return RuntimeSharedImages.getDefault();
	}
	
	protected BaseUISharedImages createSharedImages() {
		return getSharedImages();
	}

	
	/*
	 *  These are all deprecated and shouldn't be used.
	 *  I tried to remove them, but some test classes 
	 *  in javaee are still using them.  
	 */
	@Deprecated
	public List<RuntimeDefinition> getServerDefinitions() {
		return RuntimeModelUtil.getRuntimeDefinitions(getModel().getRuntimePaths());
	}

	@Deprecated
	public Set<IRuntimeDetector> getRuntimeDetectors() {
		return RuntimeCoreActivator.getDefault().getRuntimeDetectors();
	}

	@Deprecated
	public static void setTimestamp(RuntimePath[] runtimePaths2) {
		RuntimeModelUtil.updateTimestamps(runtimePaths2);
	}

	@Deprecated
	public static boolean runtimeCreated(RuntimeDefinition runtimeDefinition) {
		return RuntimeModelUtil.verifyRuntimeDefinitionCreated(runtimeDefinition);
	}
	
	@Deprecated
	public static RuntimePath[] getRuntimePaths() {
		return getDefault().getModel().getRuntimePaths();
	}
	
}
