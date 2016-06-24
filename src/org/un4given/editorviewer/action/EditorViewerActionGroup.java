/*
 * Created on Jun 5, 2004
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.un4given.editorviewer.action;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.views.navigator.MainActionGroup;
import org.un4given.editorviewer.views.EditorViewer;
import org.un4given.editorviewer.views.EditorViewerContentProvider;

/**
 * @author NBK4BSX
 */
public class EditorViewerActionGroup extends MainActionGroup  {
	/**
	 * IActions
	 */
	protected CloseAction       _closeItemAction        	= null;
	protected CloseAllAction    _closeAllItemsAction    	= null;
	protected ExpandAllAction   _expandAllItemsAction   	= null;
	protected ShowExtensionsAction _showExtensionsAction	= null;
	protected SaveAction 		_saveAction 				= null;
	protected SaveAllAction		_saveAllAction				= null;
	protected LayoutActionGroup _layoutItemsAction 			= null;
	protected FilterDirtyAction _filterDirty = null;
	

	public EditorViewerActionGroup(EditorViewer viewer){
		super(viewer);
		makeActions();
	}
	
	public int getLayoutMode(){
		return _layoutItemsAction.getLayoutMode();
	}
	
	/**
	 * Adds the actions in this group and its subgroups to the action bars.
	 */
	public void fillActionBars(IActionBars actionBars) {
		IMenuManager menu = actionBars.getMenuManager();
		_layoutItemsAction.fillActionBars(actionBars);
		menu.add(new Separator());
		menu.add(_closeAllItemsAction);
		menu.add(_saveAllAction);
		menu.add(_expandAllItemsAction);
		menu.add(new Separator());
		menu.add(_showExtensionsAction);
		menu.add(_filterDirty);
		menu.add(new Separator());

		IToolBarManager manager = actionBars.getToolBarManager();
		manager.add(_saveAllAction);
		manager.add(_closeAllItemsAction);
		super.fillActionBars(actionBars);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.ActionGroup#fillContextMenu(org.eclipse.jface.action.IMenuManager)
	 */
	public void fillContextMenu(IMenuManager menu) {
		boolean hasVisible = ((EditorViewer)getNavigator()).hasVisibleElements();
		if ( !hasVisible )
			return;
		
		IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();
		if ( selection.size() >= 1 )
			menu.add(_closeItemAction);

		menu.add(_closeAllItemsAction);
		
		if ( ((EditorViewer)getNavigator()).hasDirtyEditors() )
			menu.add(_saveAction);
		
		menu.add(new Separator());
		super.fillContextMenu(menu);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.actions.ActionGroup#updateActionBars()
	 */
	public void updateActionBars() {
		if ( getContext() == null )
			return;
		super.updateActionBars();
		boolean hasVisible = ((EditorViewer)getNavigator()).hasVisibleElements();
		if ( !hasVisible ){
			_closeAllItemsAction.setEnabled(false);
			_saveAllAction.setEnabled(false);
		} else {
			_closeAllItemsAction.setEnabled(true);
			_saveAllAction.setEnabled(((EditorViewer)getNavigator()).hasDirtyEditors());
		}
	}	

	/**
	 * Makes the actions contained directly in this action group.
	 */
	protected void makeActions() {
		super.makeActions();
		_closeItemAction        = new CloseAction((EditorViewer)getNavigator());
		_closeAllItemsAction    = new CloseAllAction((EditorViewer)getNavigator());
		_expandAllItemsAction   = new ExpandAllAction((EditorViewer)getNavigator());
		_showExtensionsAction	= new ShowExtensionsAction((EditorViewer)getNavigator());
		_layoutItemsAction      = new LayoutActionGroup(this,(EditorViewer)getNavigator());
		_saveAction				= new SaveAction((EditorViewer)getNavigator());
		_saveAllAction			= new SaveAllAction((EditorViewer)getNavigator());
		_filterDirty			= new FilterDirtyAction((EditorViewer)getNavigator(),0);
	}	
	
	/**
	 * Extends the superclass implementation to dispose the subgroups.
	 */
	public void dispose() {
//		_layoutItemsAction.dispose();
		super.dispose();
	}

	/**
	 * Extends the superclass implementation to set the context in the subgroups.
	 */
	public void setContext(ActionContext context) {
		super.setContext(context);
		_layoutItemsAction.setContext(context);
	}	

	public void saveState(IMemento memento){
		memento.putInteger("TAG_LAYOUT",_layoutItemsAction.getLayoutMode());
		memento.putString("TAG_DFILTER",String.valueOf(_filterDirty.isChecked()));
	}
	
	public void restoreState(IMemento memento){
		restoreLayout(memento);
		if ( memento == null )
			return;
		this._filterDirty.setChecked("true".equals(memento.getString("TAG_DFILTER")));
		this._filterDirty.run();
	}


	/**
	 * Initializes the sorter
	 */
	private void restoreLayout(IMemento memento) {
		int layoutType = EditorViewerContentProvider.SEMI_LAYOUT;
		try {
			if ( memento != null ){
				int layoutInt = 0;
				String layoutStr = memento.getString("TAG_LAYOUT");
				if (layoutStr != null)
					layoutInt = new Integer(layoutStr).intValue();
				if (layoutInt == EditorViewerContentProvider.FLAT_LAYOUT || 
					layoutInt == EditorViewerContentProvider.HIER_LAYOUT )
					layoutType = layoutInt;
			}
		} catch (NumberFormatException e) {}
		_layoutItemsAction.setSelection(layoutType);
	}		
}
