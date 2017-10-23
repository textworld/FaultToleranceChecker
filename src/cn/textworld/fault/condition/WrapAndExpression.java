package cn.textworld.fault.condition;

import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.osate.xtext.aadl2.errormodel.errorModel.AndExpression;
import org.osate.xtext.aadl2.errormodel.errorModel.ConditionElement;
import org.osate.xtext.aadl2.errormodel.errorModel.ConditionExpression;
import org.osate.xtext.aadl2.errormodel.errorModel.QualifiedErrorBehaviorState;

public class WrapAndExpression implements WrapExpression{
	private AndExpression _expr;
	
	public WrapAndExpression(AndExpression andExpression){
		System.out.println("WrapAndExpression constructer");
		this._expr = andExpression;
	}
	
	public boolean cal(Set<QualifiedErrorBehaviorState> ebs_set){
		boolean ans = true;
		EList<ConditionExpression> elist = ((AndExpression) _expr).getOperands();
		for(ConditionExpression ce : elist){
			WrapExpression we = WrapExpressionFactory.wrap(ce);
			System.out.println(we);
			ans = ans && we.cal(ebs_set);
		}
		return ans;
	}
}
