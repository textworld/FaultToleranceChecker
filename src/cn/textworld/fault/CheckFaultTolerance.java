package cn.textworld.fault;

import org.eclipse.core.runtime.IProgressMonitor;
import org.osate.aadl2.instance.SystemInstance;
import org.osate.aadl2.instance.SystemOperationMode;
import org.osate.aadl2.modelsupport.errorreporting.AnalysisErrorReporterManager;
import org.osate.contribution.sei.names.SEI;

public class CheckFaultTolerance extends AbstractLevelChecker{

	@Override
	protected String getLevelPropertyPropertySet() {
		// TODO Auto-generated method stub
		return SEI._NAME;
	}

	@Override
	protected String getLevelPropertyName() {
		// TODO Auto-generated method stub
		return SEI.SAFETY_CRITICALITY;
	}

	@Override
	protected String getActionName() {
		// TODO Auto-generated method stub
		return "CheckFaultTolerance";
	}
	
	public void invoke(IProgressMonitor monitor, SystemInstance root, SystemOperationMode som) {
		invoke(monitor, null, root, som);
	}
	
	public void invoke(final IProgressMonitor monitor, final AnalysisErrorReporterManager errManager,
			final SystemInstance root, final SystemOperationMode som){
		System.out.println("Mode name: " + som.getFullName());
		this.errManager = errManager != null ? errManager
				: new AnalysisErrorReporterManager(getAnalysisErrorReporterFactory());
		analyzeInstanceModel(monitor, this.errManager, root, som);
	}

}
