package cn.textworld.fault.condition;

import java.util.Set;

import org.osate.xtext.aadl2.errormodel.errorModel.ConditionElement;
import org.osate.xtext.aadl2.errormodel.errorModel.QualifiedErrorBehaviorState;

public class WrapConditionElement implements WrapExpression{
	private ConditionElement _ele;
	
	public WrapConditionElement(ConditionElement ele){
		System.out.println("WrapConditionElement constructer");
		this._ele = ele;
	}
	@Override
	public boolean cal(Set<QualifiedErrorBehaviorState> ebs_set) {
		return false;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "WrapCondtionElement: " + _ele.toString();
	}
	
	

}
