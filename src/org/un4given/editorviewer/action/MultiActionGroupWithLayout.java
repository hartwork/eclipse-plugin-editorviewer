/*
 * Created on June 24, 2016
 *
 * Copyright (C) 2016  Sebastian Pipping
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.un4given.editorviewer.action;

import org.eclipse.jdt.internal.ui.actions.MultiActionGroup;
import org.eclipse.jface.action.IAction;

public class MultiActionGroupWithLayout extends MultiActionGroup {

	private int layout;

	public MultiActionGroupWithLayout(IAction[] createActions, int layout) {
		super(createActions, 0);
		this.layout = layout;
	}

	protected int getLayout() {
		return layout;
	}

	protected void setLayout(int layout) {
		this.layout = layout;
	}

}
