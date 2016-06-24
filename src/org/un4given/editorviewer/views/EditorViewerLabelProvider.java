package org.un4given.editorviewer.views;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorReference;
import org.un4given.EditorViewerPlugin;

public class EditorViewerLabelProvider implements ILabelProvider  {

	private ILabelProvider provider = null;
	private EditorViewer _viewer = null;
	private boolean showExtensions = true;
	
	
	public EditorViewerLabelProvider(EditorViewer viewer, ILabelProvider provider){
		this._viewer = viewer;
		this.provider = provider;
	}
	
	public Image getImage(Object element) {
		return provider.getImage(element);
	}
	
	public String getText(Object element) {
		if ( _viewer.getContentProvider().getLayout() == EditorViewerContentProvider.SEMI_LAYOUT ){
			if ( element instanceof IFolder ){
				String name = ((IFolder)element).getName();
				while ( ((IResource)( element = ((IResource)element).getParent() )).getType() != IResource.PROJECT )
					name = ((IFolder)element).getName() + "/" + name;
				return name.substring(0,name.length());
			}
		}
		if ( element instanceof IFile ){
			if ( _viewer.getContentProvider() != null && _viewer.getContentProvider().isFilterDirty() ){
				return stripExtension(((IFile)element).getName()) + " *";
			} else {
				IEditorReference ref = EditorViewerPlugin.getDefault().getReference(_viewer.getSite(),(IFile)element);
				if ( ref != null && ref.isDirty() )
					return stripExtension(((IFile)element).getName()) + " *";				
			}
		}
		return stripExtension(provider.getText(element));
	}
	
	public boolean isLabelProperty(Object element, String property) {
		return this.provider.isLabelProperty(element, property);
	}
	
	private String stripExtension(String text){
		if ( !showExtensions ){
			int pos = -1;
			if ( text.length() > 1 && (pos = text.lastIndexOf(".") ) > 0 )
				text = text.substring(0,pos);
		}
		return text;
	}
	
	public void addListener(ILabelProviderListener listener) {
		this.provider.addListener(listener);
	}
	
	public void removeListener(ILabelProviderListener listener) {
		this.provider.removeListener(listener);
	}
	
	public void dispose() {
		this.provider.dispose();
	}

	public boolean isShowExtensions() {
		return showExtensions;
	}

	public void setShowExtensions(boolean showExtensions) {
		this.showExtensions = showExtensions;
	}
}
