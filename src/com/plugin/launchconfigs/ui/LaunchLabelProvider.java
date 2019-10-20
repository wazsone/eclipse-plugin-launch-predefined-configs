package com.plugin.launchconfigs.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.internal.core.groups.GroupLaunchElement;
import org.eclipse.debug.internal.ui.DebugPluginImages;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

public class LaunchLabelProvider extends BaseLabelProvider implements ITableLabelProvider {

	public LaunchLabelProvider() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (!(element instanceof GroupLaunchElement)) {
			return null;
		}
		if (columnIndex == 0) {
			GroupLaunchElement el = (GroupLaunchElement) element;
			if (el.data == null) {
				Image errorImage = PlatformUI.getWorkbench().getSharedImages()
						.getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
				return errorImage;
			}

			try {
				String key = el.data.getType().getIdentifier();
				return DebugPluginImages.getImage(key);
			} catch (CoreException e) {
				Image errorImage = PlatformUI.getWorkbench().getSharedImages()
						.getImage(ISharedImages.IMG_OBJS_ERROR_TSK);
				return errorImage;
			}
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (!(element instanceof GroupLaunchElement)) {
			return null;
		}
		GroupLaunchElement el = (GroupLaunchElement) element;

		// launch name
		if (columnIndex == 0) {
			try {
				return (el.data != null) ? el.data.getType().getName() + "::" + el.name : el.name; //$NON-NLS-1$
			} catch (CoreException e) {
				return el.name;
			}
		}

		return null;
	}

}
