package unc.cs.symbolTable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.checks.naming.AccessModifier;

import unc.cs.checks.STBuilderCheck;

public class AccessModifierUsage {
	STNameable subject;
	AccessModifier declared;
	AccessModifier used;
	int difference;
	Collection<STMethod> methodsReferencing;
	
	STType typeReferencing;

	public AccessModifierUsage(STNameable aSubject, AccessModifier declared, AccessModifier used, STType aTypeReferencing, Collection<STMethod> aMethodsReferencing) {
		super();
		this.declared = declared;
		this.used = used;
		subject = aSubject;
		typeReferencing = aTypeReferencing;
//		methodsReferencing = aMethodsReferencing;
		if (typeReferencing != null && aMethodsReferencing != null) {
			methodsReferencing = new HashSet();
			for (STMethod anSTMethod:aMethodsReferencing) {
				if (anSTMethod.getDeclaringSTType() == typeReferencing) {
					methodsReferencing.add(anSTMethod);
				}
			}

		}
		
	}
	public STType getTypeReferencing() {
		return typeReferencing;
	}
	public STNameable getSubject() {
		return subject;
	}
	public AccessModifier getDeclared() {
		return declared;
	}
	public AccessModifier getUsed() {
		return used;
	}
	public int getDifference() {
		if (used == null) {
			return APropertyInfo.INFINITE_ACCESS_DIFFERENCE;
		}
		return (used.ordinal() - declared.ordinal());
	}
	public Collection<STMethod> getMethodsReferencing() {
		return methodsReferencing;
	}
	public String toString() {
		return "(" + subject + ", " +  declared + ", " + used  + ", " + getDifference() + ", " + typeReferencing + ", " +  methodsReferencing + " )";
	}
	

}
