package cn.textworld.fault.condition;

import java.util.Set;

import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.Subcomponent;
import org.osate.xtext.aadl2.errormodel.errorModel.ConditionElement;
import org.osate.xtext.aadl2.errormodel.errorModel.ConditionExpression;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorBehaviorState;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorModelFactory;
import org.osate.xtext.aadl2.errormodel.errorModel.QualifiedErrorBehaviorState;
import org.osate.xtext.aadl2.errormodel.errorModel.SConditionElement;
import org.osate.xtext.aadl2.errormodel.errorModel.impl.ErrorModelFactoryImpl;

public class WrapSConditionElement implements WrapExpression{
	SConditionElement _ele;
	
	public WrapSConditionElement(ConditionExpression expr){
		System.out.println("WrapSConditionElement Constructor");
		this._ele = (SConditionElement) expr;
	}
	@Override
	public boolean cal(Set<QualifiedErrorBehaviorState> ebs_set) {
		QualifiedErrorBehaviorState qebs = this._ele.getQualifiedState();
		Subcomponent _ele_subcomponent = qebs.getSubcomponent().getSubcomponent();
		ComponentImplementation _ele_impl = _ele_subcomponent.getComponentImplementation();
		ErrorBehaviorState _ele_state = qebs.getState();
		
		for(QualifiedErrorBehaviorState state : ebs_set){
			Subcomponent target_component = state.getSubcomponent().getSubcomponent();
			ComponentImplementation target_impl = target_component.getComponentImplementation();
			ErrorBehaviorState target_state = state.getState();
			
			if(_ele_impl.equals(target_impl)){
				System.out.println(_ele_impl.getName() + " equals " + target_impl.getName());
				if(target_state.equals(_ele_state)){
					System.out.println(target_state.getName() + " equals " + _ele_state.getName());
					return true;
				}else{
					System.out.println(target_state.getName() + " not equals " + _ele_state.getName());
				}
			}else{
				System.out.println(_ele_impl.getName() + " not equals " + target_impl.getName());
			}
		}
		return false;
	}

}
