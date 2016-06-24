package org.un4given;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class EditorViewerPlugin extends AbstractUIPlugin {
	

	private Map editorCache = null;
	private boolean using31 = false;
	
	////////////////////////////////////////////////////////////////
	//////////////            FILTER MODE        /////////////////////
	////////////////////////////////////////////////////////////////
	
	/**
	 * Indicates no filtering will take place
	 */
	public static final int NONE_FILTER_MODE = 0;
	
	/**
	 * Indicates the filter should be performed, showing only dirty editors
	 */
	public static final int DIRTY_FILTER_MODE = 1;	
	
	
	//The shared instance.
	private static EditorViewerPlugin plugin;
	
	/**
	 * The constructor.
	 */
	public EditorViewerPlugin() {
		plugin = this;
		this.editorCache = new HashMap();
		try {
			using31 = IEditorReference.class.getMethod("getEditorInput",null) != null;
		} catch (Exception ex){}
		
	}

	public void initCache(IWorkbenchSite site){
		IEditorReference[] refs = site.getPage().getEditorReferences();
		for ( int i = 0 ; i < refs.length ; i++ ){
			try { editorCache.put(getFileForReference(refs[i]),refs[i]); } catch (Exception ex){}
		}
	}
	
	public IEditorReference getReference(IWorkbenchSite site, IFile file) {
		try {
			IEditorReference ref = (IEditorReference)editorCache.get(file);
			if ( ref != null )
				return ref;
			if ( site == null )
				return null;
			IWorkbenchPage page = site.getPage();
			if ( page == null )
				return null;
			IEditorReference[] refs = page.getEditorReferences();
			for ( int i = 0 ; refs != null && i < refs.length ; i++ ){
				IFile refFile = getFileForReference(refs[i]);
				if ( refFile != null && refFile.equals(file) ){
					editorCache.put(file,refs[i]);
					return refs[i];
				}
			}
		} catch (Exception ex){}
		return null;
	}
	
	public void removeReference(IFile file){
		editorCache.remove(file);
	}
	
	
	public IFile getFileForReference(IEditorReference ref){
		if ( ref == null )
			return null;
		try {
			IFile file = null;
			IEditorInput input = null;
			if ( using31 )
				input = ref.getEditorInput();
			if ( input != null )
				file = (IFile)input.getAdapter(IFile.class);
			if ( file == null ){
				IEditorPart part = ref.getEditor(true);
				if ( part != null )
					input = part.getEditorInput();
				if ( input != null )
					file = (IFile)input.getAdapter(IFile.class);
			}
			return file;
		} catch (Exception ex){
			return null;
		}
	}	
	
	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}
	
	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static EditorViewerPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("EditorViewer", path);
	}
}
