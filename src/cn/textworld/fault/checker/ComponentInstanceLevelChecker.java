package cn.textworld.fault.checker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.osate.aadl2.Property;
import org.osate.aadl2.PropertyExpression;
import org.osate.aadl2.instance.AnnexInstance;
import org.osate.aadl2.instance.ComponentInstance;
import org.osate.aadl2.instance.SystemInstance;
import org.osate.aadl2.instance.util.InstanceSwitch;
import org.osate.aadl2.modelsupport.WriteToFile;
import org.osate.aadl2.modelsupport.errorreporting.AnalysisErrorReporterManager;
import org.osate.aadl2.modelsupport.modeltraversal.AadlProcessingSwitch;
import org.osate.aadl2.properties.InvalidModelException;
import org.osate.xtext.aadl2.errormodel.errorModel.AndExpression;
import org.osate.xtext.aadl2.errormodel.errorModel.CompositeState;
import org.osate.xtext.aadl2.errormodel.errorModel.ConditionElement;
import org.osate.xtext.aadl2.errormodel.errorModel.ConditionExpression;
import org.osate.xtext.aadl2.errormodel.errorModel.EMV2PropertyAssociation;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorBehaviorEvent;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorBehaviorState;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorBehaviorTransition;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorEvent;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorModelFactory;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorModelSubclause;
import org.osate.xtext.aadl2.errormodel.errorModel.EventOrPropagation;
import org.osate.xtext.aadl2.errormodel.errorModel.OrExpression;
import org.osate.xtext.aadl2.errormodel.errorModel.QualifiedErrorBehaviorState;
import org.osate.xtext.aadl2.errormodel.errorModel.SubcomponentElement;
import org.osate.xtext.aadl2.errormodel.errorModel.impl.ConditionElementImpl;
import org.osate.xtext.aadl2.errormodel.errorModel.impl.ErrorModelFactoryImpl;
import org.osate.xtext.aadl2.errormodel.util.EMV2Properties;
import org.osate.xtext.aadl2.errormodel.util.EMV2Util;

import cn.textworld.fault.condition.WrapExpression;
import cn.textworld.fault.condition.WrapExpressionFactory;

public class ComponentInstanceLevelChecker extends AadlProcessingSwitch{
	public static final String NON_WORKING = "NonWorking";
	/** The property to check, Must be an aadlinteger-valued property. */
	private final Property property;
	Map<SystemInstance, Boolean> visitedMap = new HashMap<SystemInstance, Boolean>();
	
	/** Construct function */
	public ComponentInstanceLevelChecker(final AnalysisErrorReporterManager errMgr, final Property pd){
		super(PROCESS_POST_ORDER_ALL, errMgr);
		property = pd;
	}	
	
	public static boolean expr(ConditionExpression expr, ErrorBehaviorEvent happen_evt){
		if(expr instanceof AndExpression){
			EList<ConditionExpression> elist = ((AndExpression) expr).getOperands();
			boolean ans = true;
			for(ConditionExpression ce : elist){
				ans = ans && expr(ce, happen_evt); 
			}
			return ans;
		}
		if(expr instanceof OrExpression){
			boolean ans = false;
			for(ConditionExpression ce : ((OrExpression)expr).getOperands()){
				ans = ans || expr(ce, happen_evt);
			}
			return ans;
		}
		if(expr instanceof ConditionElement){
			ConditionElement ele = (ConditionElement)expr;
			EventOrPropagation eop = EMV2Util.getErrorEventOrPropagation(ele);
			if(eop != null){
				// ele is event or propagation
				if(eop instanceof ErrorBehaviorEvent){
					ErrorBehaviorEvent errorEvt = (ErrorBehaviorEvent) eop;
					System.out.println("Error Event:" + errorEvt.toString());
					if(errorEvt.equals(happen_evt)){
						return true;
					}else{
						return false;
					}
				}else{
					/*
					 * eop is propagation
					 */
					return false;
				}
			}else{
				// expr is not event or propagation
				
				return false;
			}
		}
		return true;
	}
	
	@Override
	protected void initSwitches() {
		/** instanceSwitch is a variable in the super class */
		instanceSwitch = new InstanceSwitch<String>(){
			@SuppressWarnings("unused")
			public String caseComponentInstance(final ComponentInstance ci){
				System.out.println("In method caseComponentInstance find ComponentInstance " + ci.getFullName());
				/*try{
					System.out.println("In method caseComponentInstance find ComponentInstance " + ci.getFullName());
					final EList<ComponentInstance> subs = ci.getComponentInstances();
					
					final EList<AnnexInstance> annexInstances = ci.getAnnexInstances();
					System.out.println("There are " + annexInstances.size() + " annexInstances");
					for(AnnexInstance annexInstance : annexInstances){
						System.out.println("AnnexInstance name:" + annexInstance.getFullName());
					}
				}catch(InvalidModelException e){
					error(e.getElement(), e.getMessage());
				}*/
				/*
				final EList<CompositeState> compositeStates = EMV2Util.getAllCompositeStates(ci.getComponentClassifier());
				
				
				for(CompositeState state : compositeStates){
					System.out.println("CompositeState: " + state.getFullName());
				}*/
				
				if(ci instanceof SystemInstance){
					SystemInstance si = (SystemInstance)ci;
					
					WriteToFile report = new WriteToFile("FaultTolerance", si);
					reportHeading(report);
					
					if(!visitedMap.containsKey(si)){
						visitedMap.put(si, true);
						/** 
						 *  map_evt_ci store the mapping relation from the ErrorBehaviorEvent to ComponentInstance 
						 *  map_evt_ci:   ErrorBehaviorEvent == map to ==> ComponentInstance
						 * */
						Map<ErrorBehaviorEvent, ComponentInstance> map_evt_ci = new HashMap<ErrorBehaviorEvent, ComponentInstance>();
						/* 
						 * map_ci_is store the initial state of each sub componentInstance
						 */
						Map<ComponentInstance, ErrorBehaviorState> map_ci_is = new HashMap<ComponentInstance, ErrorBehaviorState>();
						/** iterate the sub components and collect all the ErrorBehaviorEvent */
						final EList<ComponentInstance> subs = si.getComponentInstances();
						for(ComponentInstance sub : subs){
							//System.out.println("ComponentInstance Full Name: " + sub.getFullName());
							//System.out.println("ComponentInstance Path:"       + sub.getComponentInstancePath());
							
							/** iterate the errorBehaviorState for each subcomponentInstance*/
							for(ErrorBehaviorState st : EMV2Util.getAllErrorBehaviorStates(sub.getComponentClassifier())){
								/** 
								 * then store the initial state of each sub componentInstance to the map_ci_is
								 */
								if(st.isIntial()){
									map_ci_is.put(sub, st);	
									break;
								}
							}
						}// end of for(ComponentInstance sub : subs)
						
						Map<ComponentInstance, Set<ErrorBehaviorState>> map_ci_st = new HashMap<ComponentInstance, Set<ErrorBehaviorState>>();
						
						Set<QualifiedErrorBehaviorState> qebs_set = new HashSet<QualifiedErrorBehaviorState>();
						
						for(ComponentInstance sub : subs){
							Set<ErrorBehaviorState> reached_states = new HashSet<ErrorBehaviorState>();
							/* add the initial state to the set */
							for(ErrorBehaviorState st : EMV2Util.getAllErrorBehaviorStates(sub.getComponentClassifier())){
								if(st.isIntial()){
									reached_states.add(st);
								}
							}
							/* assume that ErrorBehaviorEvent evt happens. And there are only one error event happening in one time */
							for(ErrorBehaviorEvent evt : EMV2Util.getAllErrorBehaviorEvents(sub)){
								/* build a set for each component instance */
								map_ci_st.put(sub, new HashSet<ErrorBehaviorState>());
								
								/* deduce the states that can be reach after the ErrorBehaviorEvent evt happens */
								Collection<ErrorBehaviorTransition> trs = EMV2Util.getAllErrorBehaviorTransitions(sub);
								for(ErrorBehaviorTransition _tr : trs){
									if(reached_states.contains(_tr.getSource())){
										ConditionExpression ce = _tr.getCondition();
										if(ComponentInstanceLevelChecker.expr(ce, evt)){
											map_ci_st.get(sub).add(_tr.getTarget());
											reached_states.add(_tr.getTarget());
											reached_states.remove(_tr.getSource());
										}
									}
								}// end for for(ErrorBehaviorTransition _tr : trs)
								
								ErrorModelFactory factory = ErrorModelFactoryImpl.init();
								for(ErrorBehaviorState state :reached_states){
									QualifiedErrorBehaviorState tmp = factory.createQualifiedErrorBehaviorState();
									SubcomponentElement sce = factory.createSubcomponentElement();
									sce.setSubcomponent(sub.getSubcomponent());
									tmp.setState(state);
									tmp.setSubcomponent(sce);
									qebs_set.add(tmp);
								}
								
								for(ComponentInstance other_sub : subs){
									if(!other_sub.equals(sub)){
										for(ErrorBehaviorState st : EMV2Util.getAllErrorBehaviorStates(other_sub.getComponentClassifier())){
											if(st.isIntial()){
												QualifiedErrorBehaviorState tmp = factory.createQualifiedErrorBehaviorState();
												SubcomponentElement sce = factory.createSubcomponentElement();
												sce.setSubcomponent(other_sub.getSubcomponent());
												tmp.setState(st);
												tmp.setSubcomponent(sce);
												qebs_set.add(tmp);
											}
										}
									}
								}
								//TODO: to handler the case where the error can be propagated through connection
								
								/* ========================================== 
								 * to check the composite error state machine 
								   ========================================== */
								
								if(EMV2Util.hasCompositeErrorBehavior(si)){
									EList<CompositeState> cs_states = EMV2Util.getAllCompositeStates(si);
									for(CompositeState stt : cs_states){
										ConditionExpression expr = stt.getCondition();
										WrapExpression we = WrapExpressionFactory.wrap(expr);
										if(we.cal(qebs_set)){
											System.out.println("ConditionExpression " + expr + " got transited!");
											ErrorBehaviorState reachState = stt.getState();
											List<EMV2PropertyAssociation> stateKinds = EMV2Properties.getProperty("EMV2::StateKind", si, reachState, reachState.getTypeSet());
											if(!stateKinds.isEmpty()){
												EMV2PropertyAssociation association_kind = stateKinds.get(0);
												PropertyExpression kindValue =  EMV2Properties.getPropertyValue(association_kind);
												String kind_string = EMV2Properties.getEnumerationOrIntegerPropertyConstantPropertyValue(kindValue);
												
												if(kind_string.equals(ComponentInstanceLevelChecker.NON_WORKING)){
													System.out.println("KindValue: " + kind_string);
													reportEntry(report, si, sub, evt, reachState);
												}
												//TODO: ≈–∂œ÷µ
//												protected void reportEnumerationOrIntegerPropertyConstantPropertyValue(EList<BasicPropertyAssociation> fields,
//														String fieldName, WriteToFile report, PropertyExpression alternativeValue) {
//													// added code to handle integer value and use of property constant instead of enumeration literal
//													PropertyExpression val = alternativeValue;
//													BasicPropertyAssociation xref = GetProperties.getRecordField(fields, fieldName);
//													if (xref != null) {
//														val = xref.getOwnedValue();
//													}
//													report.addOutput(EMV2Properties.getEnumerationOrIntegerPropertyConstantPropertyValue(val));
//												}
												
											}
											for(EMV2PropertyAssociation association : stateKinds){
												System.out.println(association);
											}
										}else{
											
										}										
									}
								}else{
									/*
									 * the system instance doesn't have composite error behavior
									 * TODO: the messages should be output to the csv file.
									 */
									System.out.println("The system instance " + si.getFullName() + " doesn't have composite error behavior");
								}
								
							}// end of "for(ErrorBehaviorEvent evt : EMV2Util.getAllErrorBehaviorEvents(sub))"
						}//end of "for(ComponentInstance sub : subs)"
					}//end of "if(ci instanceof SystemInstance)"
					report.saveToFile();
				}
				return DONE;
			}
		};
	}
	
	protected void reportHeading(WriteToFile report){
		report.addOutputNewline("System Component, Subcomponent, Error Event, Non Working State");
	}
	
	protected void reportEntry(WriteToFile report, SystemInstance si, ComponentInstance ci, ErrorBehaviorEvent ebe, ErrorBehaviorState nonWorkingState){
		report.addOutput(si.getFullName()+",");
		report.addOutput(ci.getFullName()+",");
		report.addOutput(ebe.getFullName()+",");
		report.addOutput(nonWorkingState.getFullName()+"\n");
	}

}
