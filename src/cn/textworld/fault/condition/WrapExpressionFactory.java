package cn.textworld.fault.condition;

import org.osate.xtext.aadl2.errormodel.errorModel.AndExpression;
import org.osate.xtext.aadl2.errormodel.errorModel.ConditionElement;
import org.osate.xtext.aadl2.errormodel.errorModel.ConditionExpression;
import org.osate.xtext.aadl2.errormodel.errorModel.OrExpression;
import org.osate.xtext.aadl2.errormodel.errorModel.SConditionElement;

public class WrapExpressionFactory {
	public static WrapExpression wrap(ConditionExpression expr){
		if(expr instanceof AndExpression){
			return new WrapAndExpression((AndExpression)expr);
		}
		if(expr instanceof OrExpression){
			return new WrapOrExpression((OrExpression) expr);
		}
		if(expr instanceof SConditionElement){
			return new WrapSConditionElement((SConditionElement) expr);
		}
		if(expr instanceof ConditionElement){
			return new WrapConditionElement((ConditionElement) expr);
		}
		return null;
	}
}
