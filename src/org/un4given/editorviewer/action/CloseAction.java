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
import org.un4given.Messages;
import org.un4given.editorviewer.views.EditorViewer;

/**
 * Action to close the Selected Editors<p>
 * <p>
 * @author Sean Ruff
 */

public class CloseAction extends Action {

	protected EditorViewer _viewer;
	
	/**
	 * Constructor
	 * @param helper EditorViewer instance for callback method
	 */
	public CloseAction(EditorViewer viewer){
		_viewer = viewer;
		setImageDescriptor(EditorViewerPlugin.getImageDescriptor(Messages.getString("CloseAction.0")));                  //$NON-NLS-1$
		setToolTipText(Messages.getString("CloseAction.1")); //$NON-NLS-1$
		setText(Messages.getString("CloseAction.2")); //$NON-NLS-1$
		setDescription(Messages.getString("CloseAction.3")); //$NON-NLS-1$
	}
	
	/**
	 * @see org.eclipse.jface.action.Action#run()
	 */
	public void run(){
		_viewer.closeSelected();
	}
	
}
