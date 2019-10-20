/**
 * 
 */
package com.plugin.launchconfigs.ui;

import org.eclipse.debug.internal.ui.DebugUIMessages;
import org.eclipse.debug.internal.ui.SWTFactory;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;

public abstract class ButtonsPanel extends Composite implements SelectionListener {

	Button upButton;
	Button downButton;
	Button addButton;
	Button deleteButton;

	public ButtonsPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout());
		upButton = createPushButton(this, DebugUIMessages.GroupLaunchConfigurationTabGroup_1);
		downButton = createPushButton(this, DebugUIMessages.GroupLaunchConfigurationTabGroup_2);
		addButton = createPushButton(this, DebugUIMessages.GroupLaunchConfigurationTabGroup_4);
		deleteButton = createPushButton(this, DebugUIMessages.GroupLaunchConfigurationTabGroup_5);
	}

	protected Button createPushButton(Composite parent, String key) {
		Button button = SWTFactory.createPushButton(parent, key, null);
		button.addSelectionListener(this);
		return button;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		Widget widget = e.widget;
		if (widget == upButton) {
			upPressed();
		} else if (widget == downButton) {
			downPressed();
		} else if (widget == addButton) {
			addPressed();
		} else if (widget == deleteButton) {
			deletePressed();
		}
	}

	protected abstract void upPressed();

	protected abstract void downPressed();

	protected abstract void addPressed();

	protected abstract void deletePressed();

	protected abstract void updateButtons();
}
