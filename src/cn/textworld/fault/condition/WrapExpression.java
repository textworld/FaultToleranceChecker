package cn.textworld.fault.condition;

import java.util.Set;

import org.osate.xtext.aadl2.errormodel.errorModel.QualifiedErrorBehaviorState;

public interface WrapExpression {
	public boolean cal(Set<QualifiedErrorBehaviorState> ebs_set);
}
