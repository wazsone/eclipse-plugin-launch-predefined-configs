package com.plugin.launchconfigs.ui;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import com.plugin.launchconfigs.utils.UIConstants;

public class LaunchDialog extends TitleAreaDialog {

	private Tree availableLaunchers;

	private ILaunchConfiguration selectedConfig;

	private ILaunchConfiguration selfRef;

	public LaunchDialog(Shell parentShell, ILaunchConfiguration self) {
		super(parentShell);
		selfRef = self;
	}

	@Override
	public void create() {
		super.create();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite comp = (Composite) super.createDialogArea(parent);
		GridLayoutFactory.fillDefaults().margins(20, 20).applyTo(comp);

		getShell().setText(UIConstants.DIALOG_TEXT);
		setTitle(UIConstants.DIALOG_TITLE);

		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		ILaunchConfigurationType[] allTypes = manager.getLaunchConfigurationTypes();

		availableLaunchers = new Tree(comp, SWT.BORDER);
		availableLaunchers.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		try {
			for (ILaunchConfigurationType configType : allTypes) {
				ILaunchConfiguration[] configs = manager.getLaunchConfigurations(configType);

				if (configs.length > 0) {
					if (configs.length == 1 && selfRef.getName().equals(configs[0].getName())) {
						continue;
					}
					TreeItem typeItem = new TreeItem(availableLaunchers, SWT.NONE);
					typeItem.setText(configType.getName());

					Image icon = null;
					ImageDescriptor defaultImageDescriptor = DebugUITools.getDefaultImageDescriptor(configType);
					if (defaultImageDescriptor != null) {
						icon = new Image(parent.getDisplay(), defaultImageDescriptor.getImageData(100));
						typeItem.setImage(icon);
					}

					for (ILaunchConfiguration config : configs) {
						if (selfRef.getName().equals(config.getName())) {
							continue;
						}
						TreeItem confItem = new TreeItem(typeItem, SWT.NONE);
						confItem.setText(config.getName());
						confItem.setData(config);
						if (icon != null) {
							confItem.setImage(icon);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return comp;
	}

	@Override
	protected void okPressed() {
		boolean isOk = false;
		TreeItem[] selectedItems = availableLaunchers.getSelection();

		if (selectedItems.length > 0) {
			if (selectedItems[0].getData() != null) {
				selectedConfig = (ILaunchConfiguration) selectedItems[0].getData();
				isOk = true;
			}
		}

		if (isOk) {
			super.okPressed();
		}
	}

	public ILaunchConfiguration getSelectedLaunchConfig() {
		if (selectedConfig instanceof ILaunchConfiguration) {
			return selectedConfig;
		}

		return null;
	}

}
