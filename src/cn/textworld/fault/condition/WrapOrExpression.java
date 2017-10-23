package cn.textworld.fault.condition;

import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.osate.xtext.aadl2.errormodel.errorModel.AndExpression;
import org.osate.xtext.aadl2.errormodel.errorModel.ConditionElement;
import org.osate.xtext.aadl2.errormodel.errorModel.ConditionExpression;
import org.osate.xtext.aadl2.errormodel.errorModel.OrExpression;
import org.osate.xtext.aadl2.errormodel.errorModel.QualifiedErrorBehaviorState;

public class WrapOrExpression implements WrapExpression{
	private OrExpression _expr;
	
	public WrapOrExpression(OrExpression expr){
		System.out.println("WrapOrExpression constructer");
		this._expr = expr;
	}
	
	@Override
	public boolean cal(Set<QualifiedErrorBehaviorState> ebs_set) {
		boolean ans = false;
		EList<ConditionExpression> elist = ((OrExpression) _expr).getOperands();
		for(ConditionExpression ce : elist){
			WrapExpression we = WrapExpressionFactory.wrap(ce);
			ans = ans || we.cal(ebs_set);
		}
		return ans;
	}

}
