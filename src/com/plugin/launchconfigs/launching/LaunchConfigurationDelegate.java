package com.plugin.launchconfigs.launching;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.IStatusHandler;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.internal.core.IInternalDebugCoreConstants;
import org.eclipse.debug.internal.core.groups.GroupLaunchElement;

public class LaunchConfigurationDelegate implements ILaunchConfigurationDelegate {

	private static final String NAME_ATTR = "name";
	private static final String ENABLED_ATTR = "enabled";
	private static final IStatus CONFIGS_CYCLE = new Status(IStatus.ERROR, "org.eclipse.debug.core", 232,
			IInternalDebugCoreConstants.EMPTY_STRING, null);;

	public static String formatAttributes(int index, String string) {
		return LaunchConfigsUIPlugin.getPluginId() + "." + index + "." + string; //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		List<GroupLaunchElement> configs = createLaunchConfigs(configuration);
		for (GroupLaunchElement element : configs) {
			if (!element.enabled) {
				continue;
			}
			final ILaunchConfiguration config = findLaunchConfig(element.name);
			if (config == null) {
				continue;
			}
			if (configuration.getName().equals(config.getName())) {
				IStatusHandler cycleHandler = DebugPlugin.getDefault().getStatusHandler(CONFIGS_CYCLE);
				cycleHandler.handleStatus(CONFIGS_CYCLE, config.getName());
			} else {
				config.launch(mode, monitor);
			}
		}
	}

	public static List<GroupLaunchElement> createLaunchConfigs(ILaunchConfiguration config) {
		List<GroupLaunchElement> result = new ArrayList<>();
		try {
			Map<?, ?> attrs = config.getAttributes();
			Iterator<?> iterator = attrs.keySet().iterator();
			while (iterator.hasNext()) {
				String attr = (String) iterator.next();
				if (attr.startsWith(LaunchConfigsUIPlugin.getPluginId())) {
					String prop = attr.substring(LaunchConfigsUIPlugin.getPluginId().length() + 1);
					int dotIndex = prop.indexOf('.');
					String num = prop.substring(0, dotIndex);
					int index = Integer.parseInt(num);
					String name = prop.substring(dotIndex + 1);
					if (name.equals(NAME_ATTR)) {
						GroupLaunchElement element = new GroupLaunchElement();
						element.index = index;
						element.name = (String) attrs.get(attr);
						element.enabled = (Boolean) attrs.get(formatAttributes(index, ENABLED_ATTR));
						try {
							element.data = findLaunchConfig(element.name);
						} catch (Exception e) {
							element.data = null;
						}
						while (index >= result.size()) {
							result.add(null);
						}
						result.set(index, element);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	private static ILaunchConfiguration findLaunchConfig(String name) {
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfiguration[] launchConfigurations;
		try {
			launchConfigurations = launchManager.getLaunchConfigurations();
			for (ILaunchConfiguration config : launchConfigurations) {
				if (config.getName().equals(name)) {
					return config;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static void saveLaunchConfigs(ILaunchConfigurationWorkingCopy config, List<GroupLaunchElement> input) {
		int id = 0;
		removeLaunchConfigs(config);
		for (GroupLaunchElement el : input) {
			if (el == null) {
				continue;
			}
			config.setAttribute(formatAttributes(id, NAME_ATTR), el.name);
			config.setAttribute(formatAttributes(id, ENABLED_ATTR), el.enabled);
			id++;
		}
	}

	private static void removeLaunchConfigs(ILaunchConfigurationWorkingCopy config) {
		try {
			Map<?, ?> attrs = config.getAttributes();
			Iterator<?> iterator = attrs.keySet().iterator();
			while (iterator.hasNext()) {
				String attr = (String) iterator.next();
				try {
					if (attr.startsWith(LaunchConfigsUIPlugin.getPluginId())) {
						config.removeAttribute(attr);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
