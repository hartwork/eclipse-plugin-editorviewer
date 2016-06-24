package org.un4given.editorviewer.views;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.views.navigator.ResourceNavigator;
import org.un4given.EditorViewerPlugin;
import org.un4given.editorviewer.action.EditorViewerActionGroup;


public class EditorViewer extends ResourceNavigator {

	private EditorViewerContentProvider contentProvider = null;
	private EditorViewerListener listener = null;
	private EditorViewerLabelProvider labelProvider = null;
	private IMemento memento =  null;
	private EditorViewerActionGroup actionGroup = null;
	
	public EditorViewer(){
	}
	
    protected void initContextMenu() {
    	super.initContextMenu();
    }	
	
    public void saveState(IMemento memento) {
    	super.saveState(memento);
    	((EditorViewerActionGroup)getActionGroup()).saveState(memento);
    }
    

    public void init(IViewSite site, IMemento memento) throws PartInitException {
    	this.memento = memento;
    	super.init(site, memento);
    }
    
    /**
     * Creates the viewer.
     * 
     * @param parent the parent composite
     * @since 2.0
     */
    protected TreeViewer createViewer(Composite parent) {
        TreeViewer viewer = new EditorViewerTreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
        viewer.setUseHashlookup(true);
        initContentProvider(viewer);
        initLabelProvider(viewer);
        initFilters(viewer);
        initListeners(viewer);
        return viewer;
    }    
    	
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		
		EditorViewerPlugin.getDefault().initCache(getSite());
		if ( getSite().getPage().getActiveEditor() != null ){
			getSite().getPage().getActiveEditor().addPropertyListener(this.listener);
		}
		refresh();
	}
	
	protected void initContentProvider(TreeViewer viewer) {
		this.contentProvider = new EditorViewerContentProvider(this);
		viewer.setContentProvider(this.contentProvider);
	}
	
	protected void initLabelProvider(TreeViewer viewer) {
		super.initLabelProvider(viewer);
		this.labelProvider = new EditorViewerLabelProvider(this,(ILabelProvider)viewer.getLabelProvider());
		viewer.setLabelProvider(labelProvider);
	}
	
	protected void initListeners(TreeViewer viewer) {
		super.initListeners(viewer);
		this.listener = new EditorViewerListener(this);
		getSite().getPage().addPartListener(this.listener);
		getSite().getWorkbenchWindow().addPerspectiveListener(this.listener);
	}
	
	
	public void makeActions(){
		this.actionGroup = new EditorViewerActionGroup(this);
		this.actionGroup.restoreState(memento);
        setActionGroup(actionGroup);
	}
	

	public EditorViewerContentProvider getContentProvider() {
		return contentProvider;
	}

	public void setDirtyFileFilter(boolean b) {
		contentProvider.setFilterDirty(b);
		contentProvider.refresh();
		refresh();
		selectActiveEditor();
	}

	protected void selectActiveEditor(){
		IWorkbenchPage page = getSite().getPage();
		if ( page == null )
			return;
		IEditorPart part = page.getActiveEditor();
		if ( part == null )
			return;
		IFile file = (IFile)part.getEditorInput().getAdapter(IFile.class);
		if ( file != null ){
			getViewer().expandToLevel(file, 1);
			getViewer().setSelection(new StructuredSelection(file));
		}
	}
	
	public void setShowExtensions(boolean b) {
		labelProvider.setShowExtensions(b);
		getViewer().refresh();
	}

	public void saveSelected() {
		IStructuredSelection sel = (IStructuredSelection)getViewer().getSelection();

		NullProgressMonitor monitor = new NullProgressMonitor();
		for ( Iterator it = sel.iterator() ; it.hasNext() ;){
			IResource res = (IResource)it.next();
			if ( res.getType() == IResource.FILE ){
				IEditorReference ref = EditorViewerPlugin.getDefault().getReference(getSite(),(IFile)res);
				if ( ref != null && ref.isDirty() ){
					ref.getEditor(true).doSave(monitor);
					if ( contentProvider.isFilterDirty() )
						removeEditor(ref);
					else
						getViewer().update(res, null);
				}
			} else {
				List desc = new ArrayList();
				getDescendants(desc, res, true);
				for ( int i = 0 ; i < desc.size() ; i++ ){
					IFile file = (IFile)desc.get(i);
					IEditorReference fref = EditorViewerPlugin.getDefault().getReference(getSite(),file);
					if ( fref != null && fref.isDirty() ){
						fref.getEditor(true).doSave(monitor);
						if ( contentProvider.isFilterDirty() )
							removeEditor(fref);
						else
							getViewer().update(file, null);
					}
				}
			}
		}
	}

	public void saveAll() {
		IStructuredSelection sel = (IStructuredSelection)getViewer().getSelection();
		selectAll();
		saveSelected();
		getViewer().setSelection(sel);
	}

	public void selectAll() {
		Object root = getViewer().getInput();
		List selected = new ArrayList();
		getDescendants(selected, root, true);
		getViewer().setSelection(new StructuredSelection(selected));
	}

	public void getDescendants(List list, Object element, boolean onlyEditors){
		Object[] children = contentProvider.getChildren(element);
		if ( children.length > 0 ){
			for ( int i = 0 ; i < children.length ; i++ ){
				getDescendants(list,children[i],onlyEditors);
				if ( !onlyEditors || children[i] instanceof IFile )
					list.add(children[i]);
			}
		}
	}
	
	
	public void setLayout(int mode) {
		this.contentProvider.setLayout(mode);
		if ( isLinkingEnabled() )
			selectActiveEditor();
		refresh();
	}


	public boolean hasVisibleElements() {
		return getViewer().getTree().getItemCount() > 0;
	}

	public boolean hasDirtyEditors() {
		IWorkbenchSite site = getSite();
		if ( site == null )
			return false;
		IWorkbenchPage page = site.getPage();
		if ( page == null )
			return false;
		IEditorPart[] parts = page.getDirtyEditors();
		if ( parts == null || parts.length < 1)
			return false;
		return true;
	}

	public void expandSelection() {
		IStructuredSelection selection = (IStructuredSelection)getViewer().getSelection();
		getViewer().getTree().setRedraw(false);	
		for ( Iterator it = selection.iterator() ; it.hasNext() ;){
			getViewer().expandToLevel(it.next(),TreeViewer.ALL_LEVELS);
		}
		getViewer().getTree().setRedraw(true);			
	}

	public void expandAll() {
		getViewer().getTree().setRedraw(false);
		getViewer().expandAll();
		getViewer().getTree().setRedraw(true);
	}
	
	public void collapseSelection(){
		IStructuredSelection selection = (IStructuredSelection)getViewer().getSelection();
		getViewer().getTree().setRedraw(false);	
		for ( Iterator it = selection.iterator() ; it.hasNext() ;){
			getViewer().collapseToLevel(it.next(),TreeViewer.ALL_LEVELS);
		}
		getViewer().getTree().setRedraw(true);		
	}

	public void closeAll() {
		selectAll();
		closeSelected();
	}
	
	public void closeSelected(){
		getSite().getPage().removePartListener(this.listener);
		
		try {
			IStructuredSelection sel = (IStructuredSelection)getViewer().getSelection();
			getViewer().setSelection(new StructuredSelection());
			
			IWorkbenchPage page = this.getSite().getPage();
			List refs = new ArrayList();
			for ( Iterator it = sel.iterator() ; it.hasNext() ;){
				IResource res = (IResource)it.next();
				if ( res.getType() == IResource.FILE ){
					IEditorReference ref = EditorViewerPlugin.getDefault().getReference(getSite(),(IFile)res);
					if ( ref != null ){
						refs.add(ref);
						removeEditor(ref);
					}
				} else {
//					((EditorViewerTreeViewer)getTreeViewer()).remove(res);
					List desc = new ArrayList();
					getDescendants(desc, res, true);
					for ( int i = 0 ; i < desc.size() ; i++ ){
						IResource de = (IResource)desc.get(i);
						IEditorReference ref = EditorViewerPlugin.getDefault().getReference(getSite(),(IFile)de);
						if ( ref != null ){
							refs.add(ref);
							removeEditor(ref);
						}
					}
				}
			}
			boolean res = page.closeEditors((IEditorReference[])refs.toArray(new IEditorReference[refs.size()]), true);
			//FIXME remove them after they cancel instead of before
			//if they cancel add them back.. hack to remove them first then add them back if they cancel b/c
			//I can't get to the file after the editor reference is closed
			if ( !res ){
				for ( int i = 0 ; i < refs.size() ; i++ ){
					IEditorReference ref = (IEditorReference)refs.get(i);
					if ( ref != null )
						addEditor(ref);
				}
			}
			
		} catch (Exception ex){
		}
		getSite().getPage().addPartListener(this.listener);
		selectActiveEditor();
	}
	
	public void addEditor(IEditorReference ref){
		if ( contentProvider.isFilterDirty() && !ref.isDirty() )
			return;
		getViewer().getTree().setRedraw(false);	
		IFile file = EditorViewerPlugin.getDefault().getFileForReference(ref);
		if ( file != null ){
			contentProvider.add(ref);
			((EditorViewerTreeViewer)getTreeViewer()).add(getContentProvider().getParent(file),file);
			this.actionGroup.updateActionBars();				
		}
		getViewer().getTree().setRedraw(true);	
	}

	public void removeEditor(IEditorReference ref){
		getViewer().getTree().setRedraw(false);	
		IFile file = EditorViewerPlugin.getDefault().getFileForReference(ref);
		if ( file != null ){
			EditorViewerPlugin.getDefault().removeReference(file);
			contentProvider.remove(ref);
			((EditorViewerTreeViewer)getTreeViewer()).remove(file);
			this.actionGroup.updateActionBars();
		}
		getViewer().getTree().setRedraw(true);	
	}
	
	public void dispose() {
		if ( getSite() != null ){
			//remove perspective listener
			if ( getSite().getWorkbenchWindow() != null )
				getSite().getWorkbenchWindow().removePerspectiveListener(this.listener);
			
			//remove the part listener from any editors that are active
			if ( getSite().getPage() != null ){
				getSite().getPage().removePartListener(this.listener);
				IEditorReference[] refs = getSite().getPage().getEditorReferences();
				for ( int i = 0 ; i < refs.length ; i++ ){
					IEditorPart part = refs[i].getEditor(false);
					if ( part != null )
						part.removePropertyListener(this.listener);
				}
			}
		}
	}

	public EditorViewerLabelProvider getLabelProvider() {
		return labelProvider;
	}

	public EditorViewerListener getListener() {
		return listener;
	}
	
	public void refresh(){
		Object[] obj = getViewer().getVisibleExpandedElements();
		contentProvider.refresh();
		getViewer().refresh();
		for ( int i = 0 ; i < obj.length ; i++ )
			getViewer().expandToLevel(obj[i], 1);
		if ( isLinkingEnabled() )
			selectActiveEditor();
		this.actionGroup.updateActionBars();
	}
	

	public void refresh(IFile res){
		if ( contentProvider.isFilterDirty() ){
			IEditorReference ref = EditorViewerPlugin.getDefault().getReference(getSite(),(IFile)res);
			if ( ref.isDirty() )
				addEditor(ref);
			else 
				removeEditor(ref);
			selectActiveEditor();
		} else {
			getViewer().update(res, null);
			this.actionGroup.updateActionBars();
		}
	}
	
}