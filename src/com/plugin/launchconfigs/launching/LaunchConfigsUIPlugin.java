package com.plugin.launchconfigs.launching;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class LaunchConfigsUIPlugin extends AbstractUIPlugin {

	private static LaunchConfigsUIPlugin plugin;

	public LaunchConfigsUIPlugin() {
		super();

	}

	public static LaunchConfigsUIPlugin getDefault() {
		return plugin;
	}

	public static String getPluginId() {
		return getDefault().getBundle().getSymbolicName();
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

}
