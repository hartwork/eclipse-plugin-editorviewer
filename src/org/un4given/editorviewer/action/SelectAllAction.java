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
import org.eclipse.ui.ISharedImages;
import org.un4given.EditorViewerPlugin;
import org.un4given.Messages;
import org.un4given.editorviewer.views.EditorViewer;

/**
 * Action to select all the leaf items in the view 
 * ( Note: only leaf items, IViewerElements which return true from isLeaf method, will be selected )
 * <p>
 * @author Sean Ruff
 */

public class SelectAllAction extends Action {

	private EditorViewer _viewer = null;
	
	public SelectAllAction(EditorViewer viewer){
		_viewer = viewer;
		setImageDescriptor(EditorViewerPlugin.getImageDescriptor(ISharedImages.IMG_DEF_VIEW));                 
		setText(Messages.getString("SelectAllAction.0")); //$NON-NLS-1$
		setToolTipText(Messages.getString("SelectAllAction.1")); //$NON-NLS-1$
		setDescription(Messages.getString("SelectAllAction.2")); //$NON-NLS-1$
	}

	/**
	 * Select All
	 */
	public void run(){
		_viewer.selectAll();
	}
	
}
