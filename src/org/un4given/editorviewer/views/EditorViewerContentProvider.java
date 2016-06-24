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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections.map.MultiValueMap;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.un4given.EditorViewerPlugin;

/**
 * Content Provider for the TreeViewer
 * <p>
 * @author Sean Ruff
 */
public class EditorViewerContentProvider extends WorkbenchContentProvider {
	
	public static final int FLAT_LAYOUT = 0;
	public static final int HIER_LAYOUT = 1;
	public static final int SEMI_LAYOUT = 2;
	
	private int layout = HIER_LAYOUT;
	private EditorViewer _viewer = null;
	
	private MultiValueMap cache = null;
	private boolean filterDirty = false;
	
	public EditorViewerContentProvider(EditorViewer viewer){
		this.cache = MultiValueMap.decorate(new HashMap(), HashSet.class);
		this._viewer = viewer;
		refresh();
	}
	
	public void refresh(){
		cache.clear();
		IEditorReference[] refs = _viewer.getSite().getPage().getEditorReferences();
		for ( int i = 0 ; i < refs.length ; i ++ )
			add(refs[i]);
	}
	
	public void add(IEditorReference ref){
		if ( ref == null )
			return;
		if ( filterDirty && !ref.isDirty() )
			return;
		IFile res = EditorViewerPlugin.getDefault().getFileForReference(ref);
		if ( res == null )
			return;
		
		IResource child = res;
		IResource parent = res.getParent();
		if ( layout == HIER_LAYOUT ){
			while ( parent != null ){
				cache.put(parent,child);
				child = parent;
				parent = parent.getParent();
			}
		} else if ( layout == SEMI_LAYOUT ){
			if ( parent.getType() == IResource.FOLDER ){
				cache.put(parent, child);
				child = parent;
				parent = parent.getProject();
				cache.put(parent, child);
			} else if ( parent.getType() == IResource.PROJECT ){
				cache.put(parent, child);
			} 
			cache.put(child.getProject().getParent(), child.getProject());
		} else if ( layout == FLAT_LAYOUT ){
			cache.put(child.getProject(), child);
			cache.put(child.getProject().getParent(), child.getProject());
		}
	}
	
	public void remove(IEditorReference ref){
		if ( ref == null )
			return;
		IFile res = EditorViewerPlugin.getDefault().getFileForReference(ref);
		if ( res == null )
			return;

		IResource child = res;
		IResource parent = res.getParent();
		if ( layout == HIER_LAYOUT ){
			while ( parent != null ){
				Set set = (Set)cache.get(parent);
				set.remove(child);
				if ( set.size() > 0 )
					return;
				child = parent;
				parent = parent.getParent();
			}
		} else if ( layout == SEMI_LAYOUT ){
			if ( parent.getType() == IResource.FOLDER ){
				Set set = (Set)cache.get(parent);
				set.remove(child);
				if ( set.size() > 0 )
					return;

				child = parent;
				parent = parent.getProject();
				set = (Set)cache.get(parent);
				set.remove(child);
				if ( set.size() > 0 )
					return;
				
				parent = child.getProject().getParent();
				child = child.getProject();
				set = (Set)cache.get(parent);
				set.remove(child);
				
			} else if ( parent.getType() == IResource.PROJECT ){
				Set set = (Set)cache.get(parent);
				set.remove(child);
				if ( set.size() > 0 )
					return;
				
				parent = child.getProject().getParent();
				child = child.getProject();
				set = (Set)cache.get(parent);
				set.remove(child);
			} 
		} else if ( layout == FLAT_LAYOUT ){
			parent = child.getProject();

			Set set = (Set)cache.get(parent);
			set.remove(child);
			if ( set.size() > 0 )
				return;
			
			parent = child.getProject().getParent();
			child = child.getProject();
			set = (Set)cache.get(parent);
			set.remove(child);
		}
	}

	
	public Object[] getChildren(Object element) {
		Set elements = (Set)cache.get(element);
		if ( elements == null )
			return new Object[]{};
		return elements.toArray();
	}
	
	public Object[] getElements(Object element) {
		return getChildren(element);
	}
	
	public Object getParent(Object element) {
		if ( layout == FLAT_LAYOUT && element instanceof IFile )
			return ((IFile)element).getProject();
		else if ( layout == SEMI_LAYOUT ){
			if ( element instanceof IFile )
				return ((IResource)element).getParent();
			else if ( element instanceof IFolder )
				return ((IFolder)element).getProject();
		}
		return super.getParent(element);
	}
	
	public boolean hasChildren(Object element) {
		return cache.containsKey(element);
	}
	
	public int getLayout() {
		return layout;
	}
	public void setLayout(int layout) {
		this.layout = layout;
		_viewer.getViewer().refresh();
	}

	public boolean isFilterDirty() {
		return filterDirty;
	}

	public void setFilterDirty(boolean filterDirty) {
		this.filterDirty = filterDirty;
	}
	
	
	
	
}
