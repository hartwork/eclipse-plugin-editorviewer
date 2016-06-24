package org.un4given.editorviewer.views;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

public class EditorViewerTreeViewer extends TreeViewer {

	public EditorViewerTreeViewer(Composite parent, int style){
		super(parent,style);
	}
	
	public EditorViewerTreeViewer(Composite parent){
		super(parent);
	}
	
	public boolean containsItem(Object element){
		if ( element == null )
			return false;
		return findItem(element) != null;
	}
	
	public void add(Object parentElement, Object childElement) {
		if ( parentElement == null || childElement == null )
			return;
		
		if ( !containsItem(parentElement) ){
			add(((EditorViewerContentProvider)getContentProvider()).getParent(parentElement),parentElement);
		}
		super.add(parentElement,childElement);
	}
	
	public void remove(Object childElement){
		Widget childW = findItem(childElement); 
		if ( childW == null || !(childW instanceof TreeItem) ){
			super.remove(childElement);
			return;
		}
		
		TreeItem parent = null;
		TreeItem item = ((TreeItem)childW).getParentItem();
		while ( item != null && item.getItemCount() <= 1 ){
			parent = item;
			item = item.getParentItem();
		}
		if ( parent != null )
			super.remove(parent.getData());
		else
			super.remove(childElement);
	}
	
}
