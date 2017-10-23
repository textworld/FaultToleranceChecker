package cn.textworld.fault;

import org.eclipse.core.runtime.IProgressMonitor;
import org.osate.aadl2.Element;
import org.osate.aadl2.Property;
import org.osate.aadl2.instance.SystemInstance;
import org.osate.aadl2.instance.SystemOperationMode;
import org.osate.aadl2.modelsupport.errorreporting.AnalysisErrorReporterManager;
import org.osate.ui.actions.AbstractInstanceOrDeclarativeModelModifyActionAction;
import org.osate.ui.dialogs.Dialog;

import cn.textworld.fault.checker.ComponentInstanceLevelChecker;

public abstract class AbstractLevelChecker extends AbstractInstanceOrDeclarativeModelModifyActionAction{
	/** the tag for initialization */
	private boolean initialized = false;
	
	/** The property we are checking */
	private Property theProperty;
	
	/**
	 * Return the name of the property set in which the level property
	 * is declared.
	 */
	protected abstract String getLevelPropertyPropertySet();
	
	/**
	 * Return the name of the property to analyse.  This property must
	 * be of type aadlinteger.
	 */
	protected abstract String getLevelPropertyName();
	
	/**
	 * Make this abstract to force subclasses to differentiate there
	 * action noame.
	 */
	protected abstract String getActionName();
	
	@Override
	protected void analyzeDeclarativeModel(IProgressMonitor monitor, AnalysisErrorReporterManager errMgr, Element root) {
		// TODO Auto-generated method stub
		
	}
	
	protected boolean initializeAnalysis(){
		if(initialized){
			return true;
		}
		theProperty = lookupOptionalPropertyDefinition(getLevelPropertyPropertySet(), getLevelPropertyName());
		if(theProperty == null){
			final String propName = getLevelPropertyPropertySet() + "::" + getLevelPropertyName();
			Dialog.showError("Error", "Cannot find property " + propName + ".");
			return false;
		}else{
			initialized = true;
			return true;
		}
	}
	
	@Override
	protected void analyzeInstanceModel(IProgressMonitor monitor, AnalysisErrorReporterManager errMgr, SystemInstance root,
			SystemOperationMode som) {
		initializeAnalysis();
		monitor.beginTask(getActionName(), IProgressMonitor.UNKNOWN);
		System.out.println("Mode name: " + som.getFullName());
		final ComponentInstanceLevelChecker checker = new ComponentInstanceLevelChecker(errMgr, theProperty);
		checker.processPostOrderAll(root);
		monitor.done();
		
	}
	
	

}
