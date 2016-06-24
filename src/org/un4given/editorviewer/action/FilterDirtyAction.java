/*
 * Created on Jan 19, 2004
 *
 * Copyright (C) 2004  Sean Ruff
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

import org.eclipse.jface.action.Action;
import org.un4given.EditorViewerPlugin;
import org.un4given.editorviewer.views.EditorViewer;

/**
 * Action to link/unlink the view selection with the Active Editor
 * <p>
 * @author Sean Ruff
 */

public class FilterDirtyAction extends Action {
	
	/**
	 * EditorViewer for callbacks
	 */
	private final EditorViewer _viewer;
	
	/**
	 * Linked/Unlinked mode
	 */
	private int _filterMode = EditorViewerPlugin.NONE_FILTER_MODE;

	/**
	 * Constructor with the default specified linkMode
	 * @param viewer
	 * @param linkMode
	 */
	public FilterDirtyAction(EditorViewer viewer, int filterMode){
		_filterMode = filterMode;
		_viewer = viewer;
		setChecked(_filterMode == EditorViewerPlugin.DIRTY_FILTER_MODE);
		if ( isChecked() )
			_viewer.setDirtyFileFilter(true);
	}

	public void setChecked(boolean checked){
		super.setChecked(checked);
		setImageDescriptor(EditorViewerPlugin.getImageDescriptor("icons/filter_tsk.gif"));                  //$NON-NLS-1$
		setText("Filter Dirty Editors");
		setToolTipText("Filter Dirty Editors");
		setDescription("Filter Dirty Editors");
	}
	
	
	/**
	 * Link/unlink the View selection depending on the previous mode
	 */
	public void run(){
		_viewer.setDirtyFileFilter(isChecked());
	}
	
	
}
