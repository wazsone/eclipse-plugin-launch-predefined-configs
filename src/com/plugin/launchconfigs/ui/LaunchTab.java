package com.plugin.launchconfigs.ui;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.core.groups.GroupLaunchElement;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import com.plugin.launchconfigs.launching.LaunchConfigurationDelegate;
import com.plugin.launchconfigs.utils.UIConstants;

public class LaunchTab extends AbstractLaunchConfigurationTab {

	private CheckboxTreeViewer treeViewer;
	private List<GroupLaunchElement> input = new ArrayList<>();
	private ILaunchConfiguration self;

	@Override
	public void createControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		setControl(comp);

		comp.setLayout(new GridLayout(2, false));

		treeViewer = new CheckboxTreeViewer(comp,
				SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		Tree table = treeViewer.getTree();
		table.setFont(parent.getFont());
		treeViewer.setContentProvider(new LaunchContentProvider());
		treeViewer.setLabelProvider(new LaunchLabelProvider());
		treeViewer.setCheckStateProvider(new ICheckStateProvider() {
			public boolean isChecked(Object element) {
				return (element instanceof GroupLaunchElement) ? ((GroupLaunchElement) element).enabled : false;
			}

			@Override
			public boolean isGrayed(Object element) {
				return false;
			}
		});
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		TreeColumn column = new TreeColumn(table, SWT.NONE);
		column.setText(UIConstants.LAUNCH_TAB_CONFIGS_COLUMN_TITLE);
		column.setWidth(UIConstants.LAUNCH_TAB_CONFIGS_COLUMN_WIDTH);

		treeViewer.setInput(input);
		final ButtonsPanel btns_panel = new ButtonsPanel(comp, SWT.NONE) {
			@Override
			protected void addPressed() {
				LaunchDialog dialog = new LaunchDialog(getShell(), self);
				dialog.create();
				if (dialog.open() == Window.OK) {
					ILaunchConfiguration config = dialog.getSelectedLaunchConfig();
					if (config == null) {
						return;
					}
					GroupLaunchElement element = new GroupLaunchElement();
					input.add(element);
					element.index = input.size() - 1;
					element.enabled = true;
					element.name = config.getName();
					element.data = config;
					treeViewer.refresh(true);
					treeViewer.setChecked(element, element.enabled);
				}
				updateButtons();
				updateLaunchConfigurationDialog();
			}

			@Override
			protected void updateButtons() {
				downButton.setEnabled(isDownEnabled());
				upButton.setEnabled(isUpEnabled());

				int selectionCount = getSelectionCount();
				deleteButton.setEnabled(selectionCount > 0);
			}

			@Override
			protected void deletePressed() {
				int[] indices = getMultiSelectionIndices();
				if (indices.length < 1) {
					return;
				}
				for (int i = indices.length - 1; i >= 0; i--) {
					input.remove(indices[i]);
				}
				treeViewer.refresh(true);
				updateButtons();
				updateLaunchConfigurationDialog();
			}

			private int getSingleSelectionIndex() {
				StructuredSelection sel = (StructuredSelection) treeViewer.getSelection();
				if (sel.size() != 1) {
					return -1;
				}
				GroupLaunchElement el = ((GroupLaunchElement) sel.getFirstElement());
				return input.indexOf(el);
			}

			private int[] getMultiSelectionIndices() {
				StructuredSelection sel = (StructuredSelection) treeViewer.getSelection();
				List<Integer> indices = new ArrayList<>();

				for (Iterator<?> iter = sel.iterator(); iter.hasNext();) {
					GroupLaunchElement el = (GroupLaunchElement) iter.next();
					indices.add(input.indexOf(el));

				}
				int[] result = new int[indices.size()];
				for (int i = 0; i < result.length; i++) {
					result[i] = indices.get(i);
				}
				return result;
			}

			private int getSelectionCount() {
				return ((StructuredSelection) treeViewer.getSelection()).size();
			}

			@Override
			protected void downPressed() {
				if (!isDownEnabled()) {
					return;
				}
				int index = getSingleSelectionIndex();

				GroupLaunchElement x = input.get(index);
				input.set(index, input.get(index + 1));
				input.set(index + 1, x);
				treeViewer.refresh(true);
				updateButtons();
				updateLaunchConfigurationDialog();
			}

			protected boolean isDownEnabled() {
				final int index = getSingleSelectionIndex();
				return (index >= 0) && (index != input.size() - 1);
			}

			protected boolean isUpEnabled() {
				return getSingleSelectionIndex() > 0;
			}

			@Override
			protected void upPressed() {
				if (!isUpEnabled()) {
					return;
				}
				int index = getSingleSelectionIndex();
				GroupLaunchElement x = input.get(index);
				input.set(index, input.get(index - 1));
				input.set(index - 1, x);
				treeViewer.refresh(true);
				updateButtons();
				updateLaunchConfigurationDialog();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		};

		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				btns_panel.updateButtons();
			}
		});

		treeViewer.addCheckStateListener(new ICheckStateListener() {
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				((GroupLaunchElement) event.getElement()).enabled = event.getChecked();
				updateLaunchConfigurationDialog();
			}
		});
		btns_panel.updateButtons();
		GridData layoutData = new GridData(GridData.GRAB_VERTICAL);
		layoutData.verticalAlignment = SWT.BEGINNING;
		btns_panel.setLayoutData(layoutData);

	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			self = configuration.copy(configuration.getName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		input = LaunchConfigurationDelegate.createLaunchConfigs(configuration);
		if (treeViewer != null) {
			treeViewer.setInput(input);
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		LaunchConfigurationDelegate.saveLaunchConfigs(configuration, input);
	}

	@Override
	public String getName() {
		return "Launch Configs Tab";
	}

}
