package unc.symbolTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import unc.checks.ComprehensiveVisitCheck;
import unc.checks.STBuilderCheck;
import unc.checks.STTypeVisited;
import unc.checks.TagBasedCheck;
import unc.checks.TypeVisitedCheck;
import unc.tools.checkstyle.PostProcessingMain;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.checks.naming.AccessModifier;

public abstract class AnAbstractSTMethod extends AnSTNameable implements STMethod {
  private static  boolean doNotFollowCallsIntoTaggedTypes = false;
private static final String PARAMETERS_RETURN_VALUE_SEPARATOR = "->";
private static final String NAME_PARAMETER_SEPARATOR = ":";
//	final String declaringClass;
//	final String[] parameterTypes;
//	final boolean isPublic;
//	final boolean isProcedure;
//	final boolean isInstance;
//	final boolean isVisible;
	STType declaringSTType;
	int numParameters;

	protected boolean ambiguouslyOverloadedMethods;
	protected boolean unresolvedMethod;

	
	Set<STMethod> allDirectlyOrIndirectlyCalledMethods;
	Set<STMethod> allInternallyCalledMethods;
	
	// to avoid duplicates, set
//	Set<STMethod> allCallingMethods;
	Set<STMethod> allInternallyCallingMethods;
	Set<STMethod> callingMethods;
	Set<STMethod> internallyCallingMethods;
	Set<STType> callingTypes;



	protected  boolean isPublicGetter;
	protected  boolean isPublicSetter;
	protected  boolean isGetter;
	protected  boolean isSetter;
	protected  boolean isInit;
	protected  boolean isSynchronized;
	protected  String signature;
//	protected AccessModifier accessModifier = AccessModifier.PACKAGE;
	protected boolean isAbstract = false;
//	final STNameable[] tags;
//	final boolean assignsToGlobal;
//	final String[][] methodsCalled;
	public  static final String GET = "get";
	public  static final String SET = "set";
	public static final String INIT = "init";
	protected List<AccessModifierUsage> accessModifierUsage;
  protected List<AccessModifierUsage> inheritedAccessModifierUsage;

  
	protected List<STMethod> overridingSubtypeMethods;
	 protected List<STMethod> overridenSupertypeMethods;
	 protected List<STType> overridenSupertypes;
	 protected List<STType> overridingSubtypes;

	 

	@Override
  public void addOverridingSubtypeMethod(STMethod anSTMethod) {
	  initializedOverrides = true;
     if (overridingSubtypeMethods == null) {
       overridingSubtypeMethods = new ArrayList<>();
       overridingSubtypes = new ArrayList();
     }
//     if (overridingSubtypeMethods.contains(anSTMethod)) {
//       return;
//     }
     overridingSubtypeMethods.add(anSTMethod);
     overridingSubtypes.add(anSTMethod.getDeclaringSTType());
  }
	
  @Override
  public void addOverridenSupertypeMethod(STMethod anSTMethod) {
    initializedOverriddens = true;
     if (overridenSupertypeMethods == null) {
       overridenSupertypeMethods = new ArrayList<>();
       overridenSupertypes = new ArrayList();
     }
//     if (overridingSubtypeMethods.contains(anSTMethod)) {
//       return;
//     }
     overridenSupertypeMethods.add(anSTMethod);
     overridenSupertypes.add(anSTMethod.getDeclaringSTType());
  }
	
	public static boolean isSubtype (String aPotentialSubtype, String aPotentialSupertype) {
	  
	  if (aPotentialSubtype == aPotentialSupertype ||	          
	          ((aPotentialSubtype != null ) && aPotentialSubtype.equals(aPotentialSupertype))) {
	    return true;
	  }	  
	  if (aPotentialSubtype == null || aPotentialSupertype == null ) {
	    return false;
	  }
	  STType aPotentialSTSubtype = 
	          SymbolTableFactory.getOrCreateSymbolTable().
	            getSTClassByFullName(aPotentialSubtype);
	
	  if (aPotentialSTSubtype == null ) {
	    return false;
	  }
	  List<String> aPotentialSubtypeSupertypes = aPotentialSTSubtype.getSuperTypeNames();
	  return (aPotentialSubtypeSupertypes != null && aPotentialSubtypeSupertypes.contains(aPotentialSupertype));
	   
	}
	@Override
	public boolean overrides(STMethod aSuperMethod) {
	  if (!aSuperMethod.getName().equals(getName()) ||
	          aSuperMethod.getNumParameters() != getNumParameters()) {
	    return false;
	  } 	  	  
	  String myReturnType = getReturnType();
	  String superMethodReturnType = aSuperMethod.getReturnType();
	  if (!isSubtype(myReturnType, superMethodReturnType)) {
	    return false;
	  }
	  for (int aParameterIndex = 0; aParameterIndex < getNumParameters(); aParameterIndex++ ) {
	    String myParameterType = getParameterTypes()[aParameterIndex];
	    String superParameterType = aSuperMethod.getParameterTypes()[aParameterIndex];
	    if (!isSubtype(superParameterType, myParameterType)) {
	      return false;
	    }
	  }
	  return true;
	    
	  
	  
	}
	protected boolean initializedOverrides = false;
	
	protected void initializeOverrides() {
	  if (initializedOverrides)
	    return;
	  initializedOverrides = true;
	  STType aDeclaringSTType = getDeclaringSTType();
    if (aDeclaringSTType != null) {
      aDeclaringSTType.initializeOverrides();
    }
	}
	 protected boolean initializedOverriddens = false;

	 protected void initializeOverriddens() {
	    if (initializedOverriddens)
	      return;
	    initializedOverriddens = true;
	    STType aDeclaringSTType = getDeclaringSTType();
	    aDeclaringSTType.getSubSTTypes();
	  }

  @Override
  public List<STType> getOverridingSubtypes() {
    initializeOverrides();
    return overridingSubtypes;
  }
  @Override
  public List<STMethod> getOverridingSubtypeMethods() {
    initializeOverrides();
    return overridingSubtypeMethods;
  }  
  @Override
  public List<STType> getOverridenSupertypes() {
    initializeOverriddens();
    
    return overridenSupertypes;
  }
  @Override
  public List<STMethod> getOverridenSupertypeMethods() {
    initializeOverriddens();
    return overridenSupertypeMethods;
  }
	public AnAbstractSTMethod(DetailAST ast, String name) {
		super(ast, ComprehensiveVisitCheck.toShortTypeOrVariableName(name));
		
//		if (name != null && name.equals("connect")) {
//			System.err.println("found connect");
//		}
//		isSetter = computeIsSetter();
//		isGetter = computeIsGetter();
//		isInit = computeIsInit();
//		signature = displayMethod();
	}
	protected void introspect() {
//		isPublicSetter = computeIsPublicSetter();
//		isPublicGetter = computeIsPublicGetter();
		computeIsSetter();
		computeIsGetter();
		isInit = computeIsInit();
		signature = toStringMethod();
	}
	
//	public String getDeclaringClass() {
//		return declaringClass;
//	}
//	
//	public String[] getParameterTypes() {
//		return parameterTypes;
//	}
//	public String getReturnType() {
//		return returnType;
//	}
//	@Override
//	public boolean isPublic() {
//		return isPublic;
//	}
//	@Override
//	public boolean isInstance() {
//		return isInstance;
//	}
//	
//	@Override
//	public boolean assignsToGlobal() {
//		return assignsToGlobal;
//	}
//	@Override
//	public String[][] methodsCalled() {
//		return methodsCalled;
//	}
//
//	@Override
//	public boolean isVisible() {
//		return isVisible;
//	}
//
//	@Override
//	public STNameable[] getTags() {
//		return tags;
//	}
//
//	@Override
//	public boolean isProcedure() {
//		return isProcedure;
//	}
	@Override
	public boolean isPublicSetter() {
		return isPublicSetter;
	}
	@Override
	public boolean isPublicGetter() {
		return isPublicGetter;
	}
	@Override
	public boolean isSetter() {
		return isSetter;
	}
	@Override
	public boolean isGetter() {
		return isGetter;
	}
	
//	protected boolean computeIsPublicSetter() {
//		if (getParameterTypes() == null) {
//			return false;
//		}
//		return getName() != null && getName().startsWith(SET) &&
//				getName().length() > SET.length() &
//				isPublic() &&
//				getParameterTypes().length == 1 &&
//				isProcedure();
//	}
	protected void computeIsSetter() {
		if (getParameterTypes() == null) {
			return ;
		}
		isSetter = getName() != null && getName().startsWith(SET) &&
				getName().length() > SET.length() &
//				isPublic() &&
				getParameterTypes().length == 1 &&
				isProcedure();
		isPublicSetter = isSetter && isPublic();
	}
//	protected boolean computeIsPublicGetter() {
//		if (getParameterTypes() == null) {
//			return false;
//		}
//		return getName() != null && getName().startsWith(GET) &&
//				getName().length() > GET.length() &&
//				isPublic() &&
//				getParameterTypes().length == 0 &&
//				!isProcedure();
//	}
	protected void computeIsGetter() {
		if (getParameterTypes() == null) {
			return ;
		}
		isGetter = getName() != null && getName().startsWith(GET) &&
				getName().length() > GET.length() &&
//				isPublic() &&
				getParameterTypes().length == 0 &&
				!isProcedure();
		isPublicGetter = isGetter && isPublic();
	}
	protected boolean computeIsInit() {
		 return isInit(getName());
	 }
	 String toStringParameterTypes() {
		 if (getParameterTypes() == null)
			 return "null";
		 StringBuilder result = new StringBuilder();
		 
		 for (int i = 0; i < getParameterTypes().length; i++) {
			 if (i > 0) {
				 result.append(PARAMETER_SEPARATOR);
			 }
			 result.append(getParameterTypes()[i]);
		 }
		 return result.toString();
	 }
	 String toStringParameterTaggedTypes() {
		 
		 if (getParameterTypes() == null)
			 return "null";
		 StringBuilder result = new StringBuilder();
		 
		 for (int i = 0; i < getParameterTypes().length; i++) {
			 if (i > 0) {
				 result.append(PARAMETER_SEPARATOR);
			 }
//			 result.append(PostProcessingMain.toTaggedType(getParameterTypes()[i]));
       result.append(PostProcessingMain.toOutputType(getParameterTypes()[i]));

		 }
		 return result.toString();
	 }
	 String toStringMethod() {
		 StringBuilder result = new StringBuilder();
		 String aStatic = isInstance()?
		         "":
		           "static ";
//		 String aPublic = isPublic()?"public":"";
		 result.append(aStatic);
		 String aSynchronized = isSynchronized()?"synchronized ": "";
		 result.append(aSynchronized);


		 result.append(ComprehensiveVisitCheck.toAccessString(getAccessToken()));
	    result.append(" ");


		 result.append(name);
		 result.append(NAME_PARAMETER_SEPARATOR);
		 result.append(toStringParameterTypes());
		 result.append(PARAMETERS_RETURN_VALUE_SEPARATOR);
//		 result.append(TypeVisitedCheck.toShortTypeName(getReturnType()));
	    result.append(getReturnType());

		 return result.toString();

	 }
	 public static String getMatchAnyHeader(String aName) {
		 return aName + NAME_PARAMETER_SEPARATOR + 
				 TagBasedCheck.MATCH_ANYTHING+ 
				PARAMETERS_RETURN_VALUE_SEPARATOR + 
				TagBasedCheck.MATCH_ANYTHING_REGULAR_EXPERSSION;

	 }
	 protected String checksSignature;
	 @Override
	 public String getSimpleChecksSignature() {
		 if (checksSignature == null) {
		 StringBuilder result = new StringBuilder();


		 result.append(name);
		 result.append(NAME_PARAMETER_SEPARATOR);
		 result.append(toStringParameterTypes());
		 result.append(PARAMETERS_RETURN_VALUE_SEPARATOR);
		 result.append(TypeVisitedCheck.toShortTypeName(getReturnType()));
		 checksSignature = result.toString();
		 }
		 return checksSignature;

	 }
	 
	 protected String checksTaggedSignature;

	 @Override
	 public String getSimpleChecksTaggedSignature() {
		 if (checksTaggedSignature == null) {
		 StringBuilder result = new StringBuilder();


		 result.append(name);
		 result.append(NAME_PARAMETER_SEPARATOR);
		 result.append(toStringParameterTaggedTypes());

//		 result.append(toStringParameterTypes());
		 result.append(PARAMETERS_RETURN_VALUE_SEPARATOR);
//		 result.append(TypeVisitedCheck.toShortTypeName(getReturnType()));
//		 result.append(TypeVisitedCheck.toShortTypeName(PostProcessingMain.toTaggedType(getReturnType())));
		 String aReturnType = getReturnType();
//		 if (aReturnType.contains("Param")) {
//		   System.err.println("Found untagged type");
//		 }
// 	    result.append(TypeVisitedCheck.toShortTypeName(PostProcessingMain.toOutputType(getReturnType())));
//	     result.append(TypeVisitedCheck.toShortTypeName(PostProcessingMain.toOutputType(aReturnType)));
		 // why short name for return type?
       result.append(PostProcessingMain.toOutputType(aReturnType));




		 checksTaggedSignature = result.toString();
		 }
		 return checksTaggedSignature;

	 }
	 @Override
	 public  boolean isInit() {
			return isInit;
		}
		public static boolean isInit(String aMethodName) {
			return aMethodName != null && aMethodName.startsWith(INIT);
		}
		public String toString() {
			return signature;
		}
		@Override
		public String getSignature() {
			if (signature == null) {
				signature = toStringMethod();
			}
			return signature;
		}
		public boolean equals (Object anotherObject) {
			if (anotherObject instanceof STMethod) {
				STMethod anotherSTMethod = (STMethod) anotherObject;
//				return getSignature().equals(anotherSTMethod.getSignature());
        return getSimpleChecksSignature().equals(anotherSTMethod.getSimpleChecksSignature());

			}
			return super.equals(anotherObject);
		}
		
		@Override
		public STType getDeclaringSTType() {
			String aDeclaringClass = getDeclaringClass();
			if (aDeclaringClass == null) {
				return null;
			}
			if (declaringSTType == null) {
				declaringSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(aDeclaringClass);
			}
			return declaringSTType;
			
		}
		@Override
		public void setDeclaringType (STType newVal) {
			declaringSTType = newVal;
		}
		static STMethod[] emptySTMethods = {};
		protected STMethod[] stMethods;
		
		//should check length of argument list
		public static STMethod[] toSTMethods (CallInfo aCallInfo) {
			STType aCalledMethodClass = aCallInfo.getCalledSTType();
			String[] aCalledMethod = aCallInfo.getNormalizedCall();
			String aCalledMethodClassName;
			if (aCalledMethodClass != null) {
				aCalledMethodClassName = aCalledMethodClass.getName();
			}
			if (aCalledMethodClass == null) {
				aCalledMethodClassName = aCallInfo.getCalledType();
				if (aCalledMethodClassName == null) {
					aCalledMethodClassName = aCalledMethod[0];
				}
				if (aCalledMethodClassName == null) {
					return null;
				}
				 aCalledMethodClass = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByFullName(aCalledMethodClassName);
				 if (aCalledMethodClass == null) {
					 return null;
				 }

			}
			String aCalledMethodName = aCallInfo.getCallee();
			if (aCalledMethodName == null) {
				 aCalledMethodName = aCalledMethod[1];
			}
//			String aCalledMethodClassName = aCallInfo.getCalledType();
//			String[] aCalledMethod = aCallInfo.getNormalizedCall();
//			String aCalledMethodName = aCalledMethod[1];
//			String aCalledMethodClassName = aCalledMethod[0];
//			if (aCalledMethod.length > 2 )
////			if (aCalledMethod.length > 2 || aCalledMethodClassName == null || TagBasedCheck.isExternalClass(aCalledMethodClassName))
//				return emptySTMethods;
//			STType aCalledMethodClass = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aCalledMethodClassName);
//			if (aCalledMethodClass == null) {
////				System.err.println("Null called method class:" + aCalledMethodClassName);
//				return null;
//			}
			STMethod[] aCalledOverloadedMethods = aCalledMethodClass.getMethods(aCalledMethodName, aCallInfo.getActuals().size());	
			return aCalledOverloadedMethods;
			
		}
		
		@Override
		public Set<STMethod> getAllDirectlyOrIndirectlyCalledMethods() {
			if (allDirectlyOrIndirectlyCalledMethods == null || isIndirectMethodsNotFullProcessed()) {
//			  if (name.equals("main")) {
//          int i = 0;       
//        }
				allDirectlyOrIndirectlyCalledMethods = computeAllDirectlyOrIndirectlyCalledMethods(new HashSet(), this);
//				if (name.equals("main")) {
//          System.err.println(" main methods of " + declaringSTType + " are " + allDirectlyOrIndirectlyCalledMethods);
//          if (!allDirectlyOrIndirectlyCalledMethods.toString().contains("fire")) {
//            System.err.println("Did not find fire");
//          }          
//        }
			}
			if (allDirectlyOrIndirectlyCalledMethods != null)
				addCallerMethod(this, allDirectlyOrIndirectlyCalledMethods);
			return allDirectlyOrIndirectlyCalledMethods;
		}
		
//		@Override
//		public Set<STMethod> getAllCallingMethods() {
//			return allCallingMethods;
//		}
		@Override
		public Set<STMethod> getCallingMethods() {
			return callingMethods;
		}
		
		@Override
		public Set<STType> getCallingTypes() {
			return callingTypes;
		}
		
		protected void amplifyCallsToMe(
		        List<AccessModifierUsage> anInheritedAccessModifiersUsed) {
		  List<AccessModifierUsage> anAccessModifiersUsed = getAccessModifiersUsed();
		  if (anAccessModifiersUsed == null) {
		    return;
		  }
		  for (AccessModifierUsage anAccessModifierUsage:anAccessModifiersUsed) {
		    anInheritedAccessModifiersUsed.add(anAccessModifierUsage.amplifyToProtectedIfPrivate());
	      
		  }
		}
		
	  protected void addCallsToOverridenByMeMethod(
            List<AccessModifierUsage> anInheritedAccessModifiersUsed, STMethod anOverridenMethod) {
	    List<AccessModifierUsage> anAccessModifiersUsed = anOverridenMethod.getInheritedAccessModifiersUsed();
	    if (anAccessModifiersUsed == null) return;
	    for (AccessModifierUsage anAccessModifierUsage:anAccessModifiersUsed) {
        anInheritedAccessModifiersUsed.add(anAccessModifierUsage);        
      }
    }
	  
	  protected void addCallsToOverridenByMeMethods(
            List<AccessModifierUsage> anInheritedAccessModifiersUsed) {
	    if (getOverridenSupertypeMethods() == null) return;
	    for (STMethod anOverridenMethod:getOverridenSupertypeMethods()) {
	      addCallsToOverridenByMeMethod(anInheritedAccessModifiersUsed, anOverridenMethod);
	    }
    }
		
	  @Override
    public List<AccessModifierUsage> getInheritedAccessModifiersUsed() {
//      if (STBuilderCheck.isFirstPass()) {
//        return null;
//      }
//		  if ((overridingSubtypeMethods == null || overridingSubtypeMethods.size() == 0) &&
//		       (overridenSupertypeMethods == null || overridenSupertypeMethods.size() == 0)) {
//		    return getAccessModifiersUsed();
//		  }	
		  if ((getOverridingSubtypeMethods() == null || getOverridingSubtypeMethods().size() == 0) &&
		           (getOverridenSupertypeMethods() == null || getOverridenSupertypeMethods().size() == 0)) {
		        return getAccessModifiersUsed();
		      }
      if (inheritedAccessModifierUsage == null) {
        inheritedAccessModifierUsage = new ArrayList();
        amplifyCallsToMe(inheritedAccessModifierUsage);
        addCallsToOverridenByMeMethods(inheritedAccessModifierUsage);        
      }
      return inheritedAccessModifierUsage;
//      return AnSTVariable.getAccessModifiersUsed (this, getAccessModifier(), this.getDeclaringSTType(), callingTypes, getCallingMethods());
      
    }
		
	
		@Override
		public List<AccessModifierUsage> getAccessModifiersUsed() {
//		  if (STBuilderCheck.isFirstPass()) {
//		    return null;
//		  }
			if (accessModifierUsage == null) {
				accessModifierUsage = AnSTVariable.getAccessModifiersUsed (this, getAccessModifier(), this.getDeclaringSTType(), callingTypes, getCallingMethods());
			}
			return accessModifierUsage;
//			return AnSTVariable.getAccessModifiersUsed (this, getAccessModifier(), this.getDeclaringSTType(), callingTypes, getCallingMethods());
			
		}
		
		@Override	
		public Set<STMethod> getInternallyCallingMethods() {
			if (allInternallyCalledMethods == null) {
				Set<STMethod> result = new HashSet();
				allInternallyCalledMethods = computeAllInternallyDirectlyOrIndirectlyCalledMethods(result, this);
			}
			if (allInternallyCalledMethods != null)
				addCallerMethod(this, allInternallyCalledMethods);
			return internallyCallingMethods;
		}
//		static Set<STMethod> visitedInternalMethods = new HashSet();
		static Set<STMethod> visitedInternalOrExternalMethods = new HashSet();

		@Override
		public Set<STMethod> getAllInternallyDirectlyAndIndirectlyCalledMethods() {
			if (allInternallyCalledMethods == null) {
//				allInternallyCalledMethods = computeAllInternallyCalledMethods(this);
				Set<STMethod> result = new HashSet();
				allInternallyCalledMethods = computeAllInternallyDirectlyOrIndirectlyCalledMethods(result, this);
			}
			return allInternallyCalledMethods;
//			allInternallyCalledMethods = 
//			computeAllInternallyDirectlyOrIndirectlyCalledMethods(this);
			

//			return allInternallyCalledMethods;
		}
		@Override
		public Set<STMethod> getAllInternallyCallingMethods() {
			return allInternallyCallingMethods;
		}
		

		public void addCaller(STMethod aMethod) {
		  if (aMethod == null) {
		    System.err.println("Null caller");
		    return;
		  }
			if (callingMethods == null)
				callingMethods = new HashSet();
			if (callingTypes == null) {
				callingTypes = new HashSet();
			}
			callingMethods.add(aMethod);
			callingTypes.add(aMethod.getDeclaringSTType());
//			allCallingMethods.add(aMethod); // what is the difference?
			if (aMethod.getDeclaringClass() == null) {
			  System.err.println("Null declaring class for:" + aMethod);
			  return;
			}
			if (aMethod.getDeclaringClass().equals(getDeclaringClass())) {
				if (internallyCallingMethods == null) {
					internallyCallingMethods = new HashSet();
				}
				internallyCallingMethods.add(aMethod);
			}
		}
		
		public static void addCallerMethod(STMethod aCallingMethod, Set<STMethod> aCalledMethods) {
			for (STMethod aCalledMethod:aCalledMethods) {
				aCalledMethod.addCaller(aCallingMethod);
			}
		}
		protected boolean indirectMethodsNotFullProcessed = false;
		// duplicates in MethodEffectCheck: 	 protected Boolean checkCalledMethodsOf (STMethod aMethod) {	

// deprecated by getAllLocalMethods and getAllMethods
		@Override
		public boolean isIndirectMethodsNotFullProcessed() {
			return indirectMethodsNotFullProcessed;
		}
		public  Set<STMethod> computeAllDirectlyOrIndirectlyCalledMethods (Set<STMethod> result, STMethod aMethod) {
//			Set<STMethod> result = new HashSet();
//			if (visitedInternalOrExternalMethods.contains(aMethod))
//				return aMethod.getAllDirectlyOrIndirectlyCalledMethods();
//			visitedInternalOrExternalMethods.add(aMethod); // prevent recursion
//			STType aDeclaringType = aMethod.getDeclaringSTType();
//			if (aDeclaringType == null) {
//				System.err.println("Declaring type should not be null");
//				return null;
//			}
		  
//			if (this.getName().contains("reduce") && this.getDeclaringSTType().getName().contains("lient")) {
//				System.err.println("found problemantic method");
//			}
			if (result.contains(this)) {
				return result; // recursive call
			}
			
			CallInfo[] aCalledMethods = aMethod.getCallInfoOfMethodsCalled();
			
			for (CallInfo aCallInfo:aCalledMethods) {
//				STMethod[] anAllDirectlyCalledMethods = toSTMethods(aCallInfo);
//				STMethod[] anAllDirectlyCalledMethods = aCallInfo.getCalledSTMethods();
			  
				Set<STMethod> anAllDirectlyCalledMethods = aCallInfo.getMatchingCalledMethods();
				

				if (anAllDirectlyCalledMethods == null) {
//					return null;
					indirectMethodsNotFullProcessed = true;
					continue;
				}
					
//				result.addAll(Arrays.asList(anAllDirectlyCalledMethods));
				for (STMethod aDirectlyCalledMethod:anAllDirectlyCalledMethods) {
					if (result.contains(aDirectlyCalledMethod)) {
						continue;
					}
//					aDirectlyCalledMethod.addCaller(aMethod);
					
					if (!aDirectlyCalledMethod.isConstructor()) {
					  result.add(aDirectlyCalledMethod);
					} 
					
					
//					if (aDirectlyCalledMethod.getName().contains("export")) {
//						System.err.println("Export");
//					}
//					Set<STMethod> anAllIndirectlyCalledMethods = aDirectlyCalledMethod.getAllDirectlyOrIndirectlyCalledMethods();
					if (aDirectlyCalledMethod.isUnresolvedMethod()) {
						continue;
					}
	       STType aCalledSTType = aDirectlyCalledMethod.getDeclaringSTType();
         if (aCalledSTType != aMethod.getDeclaringSTType() ) {
          
      
        String aTaggedType = PostProcessingMain.toTaggedType(aCalledSTType);
        if (aTaggedType != null && doNotFollowCallsIntoTaggedTypes ) {
          // we do not need to follow its called methods for practical purposes
          continue;
        }
        }
					
					Set<STMethod> anAllIndirectlyCalledMethods = computeAllDirectlyOrIndirectlyCalledMethods(result, aDirectlyCalledMethod);

					if (anAllIndirectlyCalledMethods == null) {
//						return null;
						indirectMethodsNotFullProcessed = true;

						continue;
					}
					result.addAll(anAllIndirectlyCalledMethods);
				}
			}
			return result;			
		}
		/*
		 * got to think this through, recirsion problems
		 */
		public static Set<STMethod> computeAllInternallyCalledMethods (STMethod aMethod, Set<STMethod> result) {
//			Set<STMethod> result = new HashSet();
//			STType aDeclaringType = aMethod.getDeclaringSTType();
//			if (aDeclaringType == null) {
//				System.err.println("Declaring type should not be null");
//				return null;
//			}
			if (result.contains(aMethod))
				return result; // recursion
			CallInfo[] aCalledMethods = aMethod.getCallInfoOfMethodsCalled();
			for (CallInfo aCallInfo:aCalledMethods) {
				if (!aMethod.getDeclaringClass().contains(aCallInfo.getCalledType()))
						continue;
				String aCalledTypeShortName = ComprehensiveVisitCheck.toShortTypeOrVariableName(aCallInfo.getCalledType());
				// we did not capture the type
				if (Character.isLowerCase(aCalledTypeShortName.charAt(0)))
					continue;
//				STMethod[] anAllDirectlyCalledMethods = toSTMethods(aCallInfo);
				STMethod[] anAllDirectlyCalledMethods = aCallInfo.getCalledSTMethods();
//				Set<STMethod> anAllDirectlyCalledMethods = aCallInfo.getMatchingCalledMethods();


				if (anAllDirectlyCalledMethods == null) { // probbaly a call to a inner variable
//					System.err.println ("directly called methods should not be null");
					continue;
//					return null;
				}
				result.addAll(Arrays.asList(anAllDirectlyCalledMethods)); // these are in my class
//				result.addAll(anAllDirectlyCalledMethods); // these are in my class

				for (STMethod aDirectlyCalledMethod:anAllDirectlyCalledMethods) {
					
					Set<STMethod> anAllIndirectlyCalledMethods = aDirectlyCalledMethod.getAllInternallyDirectlyAndIndirectlyCalledMethods();
					if (anAllIndirectlyCalledMethods == null) {
						return null;
					}
					result.addAll(anAllIndirectlyCalledMethods);
				}
			}
			
			return result;
		}

		
		public static Set<STMethod> computeAllInternallyDirectlyOrIndirectlyCalledMethods (Set<STMethod> result, STMethod aMethod) {
			
//			Set<STMethod> result = new HashSet();
//			if (visitedInternalMethods.contains(aMethod))
//				return aMethod.getAllInternallyDirectlyAndIndirectlyCalledMethods();
//			visitedInternalMethods.add(aMethod); // prevent recursion
//			STType aDeclaringType = aMethod.getDeclaringSTType();
//			if (aDeclaringType == null) {
//				System.err.println("Declaring type should not be null");
//				return null;
//			}
//		  if (aMethod.getDeclaringSTType().getName().contains("ontroller")) {
//		    System.err.println("found method class");
//		  }
			CallInfo[] aCalledMethods = aMethod.getCallInfoOfMethodsCalled();
			for (CallInfo aCallInfo:aCalledMethods) {
				if (!aMethod.getDeclaringClass().contains(aCallInfo.getCalledType()))
						continue;
				String aCalledTypeShortName = ComprehensiveVisitCheck.toShortTypeOrVariableName(aCallInfo.getCalledType());
				// we did not capture the type
				if (aCalledTypeShortName.length() == 0) {
//					System.err.println("Null string for short type name");
					continue;
				}
				if (Character.isLowerCase(aCalledTypeShortName.charAt(0)))
					continue;
//				STType aCalledSTType = aCallInfo.getCalledSTType();
//				if (aCalledSTType != aMethod.getDeclaringSTType() ) {
//				  
//			
//				String aTaggedType = PostProcessingMain.toTaggedType(aCalledSTType);
//        if (aTaggedType != null ) {
//          // we do not need to follow its called methods for practical purposes
//          continue;
//        }
//				}
//				STMethod[] anAllDirectlyCalledMethods = toSTMethods(aCallInfo);
				STMethod[] anAllDirectlyCalledMethods = aCallInfo.getCalledSTMethods();
				
//				Set<STMethod> anAllDirectlyCalledMethods = aCallInfo.getMatchingCalledMethods();

				if (anAllDirectlyCalledMethods == null) { // probbaly a call to a inner variable
//					System.err.println ("directly called methods should not be null");
					continue;
//					return null;
				}
//				result.addAll(Arrays.asList(anAllDirectlyCalledMethods)); // these are in my class
//				visitedInternalMethods.add(aMethod); // prevent recursion

//				visitedInternalMethods.addAll(result);
				for (STMethod aDirectlyCalledMethod:anAllDirectlyCalledMethods) {
					if (result.contains(aDirectlyCalledMethod)) {
						continue;
					}
					result.add(aDirectlyCalledMethod);
//					STType aCalledSType = aDirectlyCalledMethod.getDeclaringSTType();
//					String aTaggedType = PostProcessingMain.toTaggedType(aCalledSType);
//					if (aTaggedType != null && aDirectlyCalledMethod.getDeclaringSTType() != aCalledSType) {
//					  // we do not need to follow its called methods for practical purposes
//					  continue;
//					}
					
					Set<STMethod> anAllIndirectlyCalledMethods = computeAllInternallyDirectlyOrIndirectlyCalledMethods(result, aDirectlyCalledMethod);
//							aDirectlyCalledMethod.getAllInternallyDirectlyAndIndirectlyCalledMethods();
					if (anAllIndirectlyCalledMethods == null) {
						return null;
					}
//					result.addAll(anAllIndirectlyCalledMethods); // add empty if already visited
//					visitedInternalMethods.addAll(anAllIndirectlyCalledMethods);
//					visitedInternalMethods.add(aMethod); // do not wnat to recurse

				}
			}
//			visitedInternalMethods.add(aMethod); // this is when the visit ends
			return result;
			
			
		}
		@Override
		public Boolean callsInternally(STMethod anSTMethod) {
//			if (getAllInternallyCalledMethods() == null)
//				return null;
//			return getAllInternallyCalledMethods().contains(anSTMethod);
			if (getLocalCallClosure() == null)
				return null;
			return getLocalCallClosure().contains(anSTMethod);
			
		}
		@Override
		public Boolean calls(STMethod anSTMethod) {
			if (getAllDirectlyOrIndirectlyCalledMethods() == null)
				return null;
			return getAllDirectlyOrIndirectlyCalledMethods().contains(anSTMethod);			
		}
		@Override
		public void setDeclaringSTType(STType declaringSTType) {
			this.declaringSTType = declaringSTType;
			
		}
		static List<STNameable> emptyNameableList = new ArrayList();

		@Override
		public List<STNameable> getTypesInstantiated() {
			return emptyNameableList;
		}
		@Override
		public boolean isAbstract() {
			return isAbstract;
		}

		@Override
		public AccessModifier getAccessModifier() {
			return accessModifier;
		}
		@Override
		public boolean isAmbiguouslyOverloadedMethods() {
			return ambiguouslyOverloadedMethods;
		}
		@Override
		public void setAmbiguouslyOverloadedMethods(boolean ambiguouslyOverloadedMethods) {
			this.ambiguouslyOverloadedMethods = ambiguouslyOverloadedMethods;
		}
		@Override
		public boolean isUnresolvedMethod() {
			return unresolvedMethod;
		}
		@Override

		public void setUnresolvedMethod(boolean unresolvedMethod) {
			this.unresolvedMethod = unresolvedMethod;
		}
		@Override
		public int getNumParameters() {
			return numParameters;
		}
		
		
		
//		@Override
//		public boolean isParsedMethod() {
//			return true;
//		}
}
