/**
 * 
 */
package com.plugin.launchconfigs.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;

import com.plugin.launchconfigs.utils.UIConstants;

public abstract class ButtonsPanel extends Composite implements SelectionListener {

	Button upButton;
	Button downButton;
	Button addButton;
	Button deleteButton;

	public ButtonsPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout());
		upButton = createPushButton(this, UIConstants.UP_BUTTON_TEXT);
		downButton = createPushButton(this, UIConstants.DOWN_BUTTON_TEXT);
		addButton = createPushButton(this, UIConstants.ADD_BUTTON_TEXT);
		deleteButton = createPushButton(this, UIConstants.DELETE_BUTTON_TEXT);
	}

	protected Button createPushButton(Composite parent, String key) {
		Button button = new Button(parent, SWT.PUSH);
		button.setFont(parent.getFont());
		if (key != null) {
			button.setText(key);
		}
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		button.setLayoutData(gd);
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
