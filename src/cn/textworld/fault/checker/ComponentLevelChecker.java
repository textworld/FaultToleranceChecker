package cn.textworld.fault.checker;

import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.Property;
import org.osate.aadl2.instance.util.InstanceSwitch;
import org.osate.aadl2.modelsupport.errorreporting.AnalysisErrorReporterManager;
import org.osate.aadl2.modelsupport.modeltraversal.AadlProcessingSwitch;

public class ComponentLevelChecker extends AadlProcessingSwitch{
	/** The property to check, Must be an aadlinteger-valued property. */
	private final Property property;
	
	/** Construct function */
	public ComponentLevelChecker(final AnalysisErrorReporterManager errMgr, final Property pd){
		super(PROCESS_POST_ORDER_ALL, errMgr);
		property = pd;
	}
	
	@Override
	protected void initSwitches() {
		// TODO Auto-generated method stub
		instanceSwitch = new InstanceSwitch<String>(){
			@SuppressWarnings("unused")
			public String caseComponentImplementation(final ComponentImplementation ci){
				printComponentImpl(ci);
				return DONE;
			}
		};
	}
	
	private void printComponentImpl(final ComponentImplementation ci){
		System.out.println("Print ComponentImplementation: " + ci.getFullName());
	}
}
