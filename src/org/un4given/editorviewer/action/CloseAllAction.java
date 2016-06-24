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

import org.un4given.EditorViewerPlugin;
import org.un4given.Messages;
import org.un4given.editorviewer.views.EditorViewer;

/**
 * Action to close all Editors listed in the view, not necessarily
 * all open editors, in case a filter is applied to the view<p>
 * <p>
 * @author Sean Ruff
 */

public class CloseAllAction extends CloseAction {

	/**
	 * Constructor
	 * @param helper EditorViewer instance for callback method
	 */
	public CloseAllAction(EditorViewer viewer){
		super(viewer);
		setImageDescriptor(EditorViewerPlugin.getImageDescriptor(Messages.getString("CloseAllAction.0")));                  //$NON-NLS-1$
		setToolTipText(Messages.getString("CloseAllAction.1")); //$NON-NLS-1$
		setText(Messages.getString("CloseAllAction.2")); //$NON-NLS-1$
		setDescription(Messages.getString("CloseAllAction.3")); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run(){
		_viewer.closeAll();
	}	
	
}
