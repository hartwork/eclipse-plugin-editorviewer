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

package org.un4given.editorviewer.views;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.EditorPart;


/**
 * Listener class between the model and the Viewer<p>
 * @author Sean Ruff
 */
public class EditorViewerListener implements IPartListener2,
											 IPageListener,
											 IPerspectiveListener,
											 IPropertyListener {

	/**
	 * Instance of EditorViewer for Callbacks
	 */
	private EditorViewer _viewer = null;
	
	/**
	 * Constructor
	 * @param helper EditorViewer instance for Callbacks
	 */
	public EditorViewerListener(EditorViewer helper){
		_viewer = helper;
	}

	public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
	}
	
	public void perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {
		if ( IWorkbenchPage.CHANGE_EDITOR_CLOSE.equals(changeId) ){
			if ( page == null )
				return;
			IEditorReference[] refs = page.getEditorReferences();
			if ( refs != null && refs.length <= 0 && _viewer.hasVisibleElements() ){
				_viewer.refresh();
			}
		}
	}
	
	public void partOpened(IWorkbenchPartReference partRef) { 
		if  ( !(partRef instanceof IEditorReference) )
			return;
		IEditorPart part = ((IEditorReference)partRef).getEditor(true);
		if ( part != null )
			part.addPropertyListener(this);
		_viewer.addEditor((IEditorReference)partRef);
	}
	
	
	public void partClosed(IWorkbenchPartReference partRef) { 
		if  ( !(partRef instanceof IEditorReference) )
			return;
		IEditorPart part = ((IEditorReference)partRef).getEditor(true);
		if ( part != null )
			part.removePropertyListener(this);
		_viewer.removeEditor((IEditorReference)partRef);
	}

	public void partActivated(IWorkbenchPartReference partRef) {}
	public void partDeactivated(IWorkbenchPartReference partRef) {}
	public void partBroughtToTop(IWorkbenchPartReference partRef) {}
	public void partHidden(IWorkbenchPartReference partRef) {}
	public void partInputChanged(IWorkbenchPartReference partRef) {}
	public void partVisible(IWorkbenchPartReference partRef) {}

	
	/**
	 * Listen for any Editors to become "DIRTY" so that the Label can dictate the change
	 * @see org.eclipse.ui.IPropertyListener#propertyChanged(java.lang.Object, int)
	 */
	public void propertyChanged(Object source, int propId) {
		switch (propId){
			case EditorPart.PROP_DIRTY          :
			case EditorPart.PROP_TITLE          :
				if ( source instanceof IEditorPart ){
					IFile file  = (IFile)((IEditorPart)source).getEditorInput().getAdapter(IFile.class);
					if ( file != null )
						_viewer.refresh(file);
				}
				break;
		}  
	}

	
	public void pageActivated(IWorkbenchPage page) {}
	public void pageClosed(IWorkbenchPage page) {}
	public void pageOpened(IWorkbenchPage page) {}
	
}
