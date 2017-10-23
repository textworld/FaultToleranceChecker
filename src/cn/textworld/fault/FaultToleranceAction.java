package cn.textworld.fault;

import org.eclipse.core.runtime.IProgressMonitor;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.Element;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.Property;
import org.osate.aadl2.instance.SystemInstance;
import org.osate.analysis.architecture.ArchitecturePlugin;
import org.osate.contribution.sei.names.SEI;
import org.osate.ui.actions.AaxlReadOnlyActionAsJob;
import org.osate.ui.dialogs.Dialog;
import org.osgi.framework.Bundle;
import org.osate.ui.dialogs.Dialog;

import cn.textworld.fault.checker.ComponentInstanceLevelChecker;
import cn.textworld.fault.checker.ComponentLevelChecker;

public class FaultToleranceAction extends AaxlReadOnlyActionAsJob{
	/** the tag for initialization */
	private boolean initialized = false;
	
	/** The property we are checking */
	private Property theProperty;
	
	/** Unkonwn */
	protected String getLevelPropertyPropertySet() {
		return SEI._NAME;
	}
	/** Unkonwn */
	protected String getLevelPropertyName() {
		return SEI.SAFETY_CRITICALITY;
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
	
	/** Unkonwn */
	protected Bundle getBundle(){
		return ArchitecturePlugin.getDefault().getBundle();
	}
	
	@Override
	protected void doAaxlAction(IProgressMonitor monitor, Element obj) {
		final Element root = obj.getElementRoot();
		if(root instanceof SystemInstance){
			monitor.beginTask(getActionName(), IProgressMonitor.UNKNOWN);
			final ComponentInstanceLevelChecker checker = new ComponentInstanceLevelChecker(getErrorManager(), theProperty);
			checker.processPostOrderAll(root);
			monitor.done();
		}else{
			/** Some additional parameters can be added through overrinding the method boolean initializeAnalysis(NamedElement object) */
			if(obj instanceof NamedElement && initializeAnalysis((NamedElement) obj)){
				final Element as;
				/** this method return void */
				initializeAnalysis();
				if(!(obj instanceof ComponentImplementation)){
					/** the condition of that obj is not an instance of ComponentImplementation */
					as = obj.getElementRoot();
				}else{
					as = obj;
				}
				
				final ComponentLevelChecker checker = new ComponentLevelChecker(getErrorManager(), theProperty);
				monitor.beginTask(getActionName(), IProgressMonitor.UNKNOWN);
				if(as instanceof ComponentImplementation){
					checker.processBottomUpComponentImpl((ComponentImplementation) as);
				}else{
					checker.processBottomUpComponentImpl();
				}
				monitor.done();
				
				finalizeAnalysis();
			}
		}

	}

	@Override
	protected String getActionName() {
		return "CheckFaultTolerance";
	}

}
