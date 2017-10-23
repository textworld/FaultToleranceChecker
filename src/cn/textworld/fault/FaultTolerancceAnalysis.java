package cn.textworld.fault;

import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.Property;
import org.osate.aadl2.instance.SystemInstance;
import org.osate.contribution.sei.names.SEI;
import org.osate.ui.actions.AbstractAnalysis;
import org.osate.xtext.aadl2.properties.util.GetProperties;

public class FaultTolerancceAnalysis extends AbstractAnalysis{

	@Override
	protected boolean readyToRunImpl() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean runImpl() {
		final Property theProperty = GetProperties.lookupPropertyDefinition(getParameter(), 
				getLevelPropertyPropertySet(), getLevelPropertyName());
		
		if(getParameter() instanceof SystemInstance){
			// TODO 暂时空着
		}else{
			if(getParameter() instanceof ComponentImplementation){
				// 如果是ComponentImplementation
			}else{
				
			}
			
		}
		return getErrorManager().getNumErrors() == 0;
	}
	
	protected String getLevelPropertyPropertySet() {
		return SEI._NAME;
	}
	
	protected String getLevelPropertyName() {
		return SEI.SAFETY_CRITICALITY;
	}

}
