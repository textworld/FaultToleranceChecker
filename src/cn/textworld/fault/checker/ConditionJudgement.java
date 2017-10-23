package cn.textworld.fault.checker;

import org.osate.xtext.aadl2.errormodel.errorModel.ConditionExpression;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorBehaviorState;

public class ConditionJudgement {
	private ConditionExpression _expr;
	public ConditionJudgement(ConditionExpression expr){
		this._expr = expr;
	}
	
	public boolean accept(ErrorBehaviorState state){
		return false;
	}
	
	public boolean accept(ConditionExpression expr, ErrorBehaviorState state){
		
		return false;
	}
}
