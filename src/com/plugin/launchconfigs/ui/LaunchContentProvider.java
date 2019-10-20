package com.plugin.launchconfigs.ui;

import java.util.List;

import org.eclipse.debug.internal.core.groups.GroupLaunchElement;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

class LaunchContentProvider implements IStructuredContentProvider, ITreeContentProvider {
	protected List<GroupLaunchElement> input;

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public void dispose() {
		input = null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (newInput instanceof List<?>) {
			input = (List<GroupLaunchElement>) newInput;
		}
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		return (parentElement == input) ? input.toArray() : null;
	}

	@Override
	public Object getParent(Object element) {
		return (element == input) ? null : input;
	}

	@Override
	public boolean hasChildren(Object element) {
		return (element == input) ? (input.size() > 0) : false;
	}
}
