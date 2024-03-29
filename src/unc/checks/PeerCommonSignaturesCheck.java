package unc.checks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import unc.symbolTable.AnAbstractSTType;
import unc.symbolTable.AnSTType;
import unc.symbolTable.STMethod;
import unc.symbolTable.STNameable;
import unc.symbolTable.STType;
import unc.symbolTable.SymbolTableFactory;

// not really using methods of superclass
public class PeerCommonSignaturesCheck extends ExpectedSignaturesCheck {
  // public static final String MSG_KEY = "peerCommonSignatures";
  public static final String MSG_KEY_DUPLICATED_SIGNATURES = "peerDuplicatedSignatures";
  public static final String MSG_KEY_OVERRIDDEN_SIGNATURES = "peerOverriddingSignatures";
  public static final String MSG_KEY_DELEGATOR = "peerIsDelegator";
  public static final String MSG_KEY_NON_INHERITING_DELEGATOR = "peerIsNonInheritingDelegator";

  List<STMethod> includeSignatures = new ArrayList();
  List<STMethod> excludeSignatures = new ArrayList();
  protected boolean includeStaticMethods = false;

  // public void doVisitToken(DetailAST ast) {
  // // System.out.println("Check called:" + MSG_KEY);
  // switch (ast.getType()) {
  // case TokenTypes.PACKAGE_DEF:
  // visitPackage(ast);
  // return;
  // case TokenTypes.CLASS_DEF:
  // case TokenTypes.INTERFACE_DEF:
  // visitType(ast);
  // return;
  // default:
  // System.err.println("Unexpected token");
  // }
  // }
  @Override
  protected String msgKeyInfo() {
    return MSG_KEY_OVERRIDDEN_SIGNATURES;
  }

  @Override
  protected String msgKeyWarning() {
    return MSG_KEY_DUPLICATED_SIGNATURES;
  }

  @Override
  public int[] getDefaultTokens() {
    return new int[] { TokenTypes.PACKAGE_DEF, TokenTypes.CLASS_DEF,
        // TokenTypes.INTERFACE_DEF

    };
  }

  public void setIncludeStaticMethods(boolean aFlag) {
    includeStaticMethods = aFlag;
  }

  public boolean isIncludeStaticMethods() {
    return includeStaticMethods;
  }

  public void setIncludeSignatures(String[] aSignatures) {
    for (String aSignature : aSignatures) {
      includeSignatures.add(signatureToMethod(aSignature));
    }
  }

  public void setExcludeSignatures(String[] aSignatures) {
    for (String aSignature : aSignatures) {
      excludeSignatures.add(signatureToMethod(aSignature));
    }
  }

  @Override
  protected String msgKey() {
    return MSG_KEY;
  }

  public void doFinishTree(DetailAST ast) {

    // maybeAddToPendingTypeChecks(ast);
    super.doFinishTree(ast);

  }

  protected void logPeerSignatureNotMatched(DetailAST aTreeAST, String aSignature,
          String aRemoteTypeName) {
    String aSourceName = shortFileName(astToFileContents.get(aTreeAST).getFileName());
    String aTypeName = getName(getEnclosingTypeDeclaration(aTreeAST));
    log(aTreeAST, aSignature, aTypeName, aRemoteTypeName);

    // if (aTreeAST == currentTree) {
    //// DetailAST aLoggedAST = matchedTypeOrTagAST == null?aTreeAST:matchedTypeOrTagAST;
    //
    // log(aTreeAST.getLineNo(), msgKey(), aSignature, aTypeName, aRemoteTypeName, aSourceName);
    // } else {
    // log(0, msgKey(), aSignature, aTypeName, aRemoteTypeName, aSourceName);
    // }

  }

  public Boolean compareCommonSignatures(STType anSTType, String aPeerType, DetailAST aTree) {
    Boolean result = true;
    List<String> aCommonSignatures = anSTType.signaturesCommonWith(aPeerType);
    if (aCommonSignatures == null)
      return null;
    System.err.println(
            anSTType.getName() + " common signaures " + aPeerType + " = " + aCommonSignatures);
    List<String> aCommonSuperTypes = anSTType.namesOfSuperTypesInCommonWith(aPeerType);
    if (aCommonSuperTypes == null)
      return null;
    // System.out.println (anSTType.getName() + " common supertypes with " + aPeerType + " =" +
    // aCommonSuperTypes);

    for (String aSignature : aCommonSignatures) {
      Boolean aHasSignature = AnSTType.containsSignature(aCommonSuperTypes, aSignature);
      if (aHasSignature == null)
        return null;
      if (aHasSignature)
        continue;
      result = false;
      logPeerSignatureNotMatched(aTree, aSignature, aPeerType);
    }
    return result;
  }

  public Boolean matchMethodWithIncludeAndExcludeSignatures(STMethod anSTMethod) {
    Boolean retVal = true;
    if (includeSignatures.size() > 0) {
      retVal = matchMethod(includeSignatures, anSTMethod);
    }
    if (excludeSignatures.size() > 0) {
      retVal = !matchMethod(excludeSignatures, anSTMethod);
    }
    return retVal;
  }

  public List<STMethod> filterByIncludeAndExcludeSignatures(List<STMethod> aMethods) {
    if (aMethods == null)
      return null;
    List<STMethod> result = new ArrayList();
    for (STMethod aMethod : aMethods) {
      if (matchMethodWithIncludeAndExcludeSignatures(aMethod)) {
        result.add(aMethod);
      }
    }
    return result;

  }

  public void checkIfNonInheritingDelegator(DetailAST aTree, STType aDelegator, STType aDelegate) {
    STNameable aSuperClass = aDelegator.getSuperClass();
    if (aSuperClass == null || aSuperClass.getName().equals("java.lang.Object")
            || aSuperClass.getName().equals("Object")) {
      log(MSG_KEY_NON_INHERITING_DELEGATOR, aTree, aTree, aDelegator.getName(),
              aDelegate.getName());
    }

  }

  // public void checkIfDelegate (DetailAST aTree, STType anSTType, STType aPeerType, List<STMethod>
  // anOverriddenMethods ) {
  // if (anOverriddenMethods == null || anOverriddenMethods.size() == 0) {
  // return;
  // }
  // if (aPeerType.getReferenceTypes().contains(this)) { // assuming overridden method is referenced
  // aPeerType.addDelegator(anSTType);
  // anSTType.addDelegate(aPeerType);
  // log (MSG_KEY_DELEGATOR, aTree, aTree, anSTType.getName(), aPeerType.getName() );
  // checkIfNonInheritingDelegator(aTree, anSTType, aPeerType);
  //
  // }
  // if (anSTType.getReferenceTypes().contains(aPeerType)) {
  // anSTType.addDelegator(aPeerType);
  // aPeerType.addDelegate(anSTType);
  // log (MSG_KEY_DELEGATOR, aTree, aTree, aPeerType.getName(), anSTType.getName() );
  // checkIfNonInheritingDelegator(aTree, aPeerType, anSTType);
  //
  // }
  //
  //
  //// if ()
  //// aPeerType.getReferenceTypes();
  //// List<STMethod> anOverriddenPublicMethods = extractPublicMethods(anOverriddenMethods);
  //// List<STMethod> aMyPublicMethods = extractPublicMethods(getDeclaredMethods());
  //// List<STMethod> aPeerPublicMethods = extractPublicMethods(aPeerType.getDeclaredMethods());
  //// if (anOverriddenPublicMethods.size() == aMyPublicMethods.size()) {
  ////
  //// }
  // }
  public Boolean compareCommonMethods(STType anSTType, String aPeerType, DetailAST aTree) {
    Boolean result = true;
    STType aPeerSTType = SymbolTableFactory.getOrCreateSymbolTable()
            .getSTClassByFullName(aPeerType);
    if (aPeerType == null)
      return null;
    if (System.identityHashCode(anSTType) > System.identityHashCode(aPeerSTType)) {
      return true;
    }

    // List<STMethod> aCommonMethods =
    // filterByIncludeAndExcludeSignatures(anSTType.methodsCommonWith(aPeerType));
    List<STMethod> aCommonMethods = filterByIncludeAndExcludeSignatures(
            anSTType.methodsCommonWith(aPeerSTType));

    if (aCommonMethods == null)
      return null;
    // System.out.println (anSTType.getName() + " common methods " + aPeerType + " = " +
    // aCommonMethods);
    // List<String> aCommonSuperTypes = anSTType.namesOfSuperTypesInCommonWith(aPeerType);
    List<String> aCommonSuperTypes = anSTType.namesOfTypesInCommonWith(aPeerType);

    if (aCommonSuperTypes == null)
      return null;
    // System.out.println (anSTType.getName() + " common supertypes with " + aPeerType + " =" +
    // aCommonSuperTypes);
    List<STMethod> anOverriddenMethods = new ArrayList();
    List<STMethod> aDuplicatedMethods = new ArrayList();
    Set<STType> anOverriddenTypes = new HashSet();
    for (STMethod aMethod : aCommonMethods) {
      if (!aMethod.isInstance() && !isIncludeStaticMethods()) {
        break;
      }
      // Boolean aHasSignature = AnSTType.containsMethod(aCommonSuperTypes, aMethod);
      // Boolean aHasSignature = AnSTType.containsDeclaredMethod(aCommonSuperTypes, aMethod);
      STType aHasSignature = AnSTType.containsDeclaredMethod(aCommonSuperTypes, aMethod);
      
      if (aHasSignature == SymbolTableFactory.getOrCreateSymbolTable().getAndMaybePutObjectType()) {
        continue;
      }

      if (aHasSignature == null) {
        aDuplicatedMethods.add(aMethod);
        if (!isInfo()) {
          log(aMethod.getAST(), aTree, aPeerType, aMethod.toString());

        }
      } else {
        anOverriddenMethods.add(aMethod);
        anOverriddenTypes.add(aHasSignature);
        if (isInfo()) {
          log(aMethod.getAST(), aTree,  aPeerType, aMethod.toString(), anOverriddenTypes.toString());

        }
      }

    }
//    if (aDuplicatedMethods.size() > 0) {
//
//      log(MSG_KEY_DUPLICATED_SIGNATURES, aDuplicatedMethods.get(0).getAST(), aTree, aPeerType,
//              aDuplicatedMethods.toString());
//
//    }
//    if (anOverriddenMethods.size() > 0) {
//
//      log(MSG_KEY_OVERRIDDEN_SIGNATURES, anOverriddenMethods.get(0).getAST(), aTree, aPeerType,
//              anOverriddenMethods.toString(), anOverriddenTypes.toString());
//
//    }

    return result;
  }

  // public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
  // String aTypeName = getName(getEnclosingTypeDeclaration(aTree));
  // STType anSTType = SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aTypeName);
  //
  // List<String> aPeerTypes = filterTypes(anSTType.getPeerTypes(), aTypeName);
  // if (aPeerTypes == null)
  // return null;
  // System.out.println("Peer Types" + aPeerTypes);
  //
  // for (String aPeerType:aPeerTypes) {
  // List<String> aCommonSignatures = anSTType.signaturesCommonWith(aPeerType);
  // if (aCommonSignatures == null)
  // return null;
  // compareCommonSignatures(anSTType, aPeerType, aTree);
  //
  // }
  // return true;
  //
  // }
  public Boolean doPendingCheck(DetailAST anAST, DetailAST aTree) {
    // String aTypeName = getName(getEnclosingTypeDeclaration(aTree));

    // STType anSTType =
    // SymbolTableFactory.getOrCreateSymbolTable().getSTClassByShortName(aTypeName);
    STType anSTType = getSTType(aTree);

    if (anSTType.isEnum() || anSTType.isAnnotation() || anSTType.isInterface()
            || anSTType.isExternal()) // wen default tokens do not include this why is this executed
      return true;

    // List<String> aPeerTypes = filterTypes(anSTType.getPeerTypes(), aTypeName);
    List<String> aPeerTypes = filterTypes(anSTType.getPeerTypes(), anSTType.getName());

    if (aPeerTypes == null)
      return null;
    // System.out.println("Peer Types" + aPeerTypes);

    for (String aPeerType : aPeerTypes) {
      // List<STMethod> aCommonMethods =
      // filterByIncludeAndExcludeSignatiures(anSTType.methodsCommonWith(aPeerType));
      // if (aCommonMethods == null)
      // return null;
      if (anSTType.getName().contains(aPeerType))
        continue;
      STType aPeerSTType = SymbolTableFactory.getOrCreateSymbolTable()
              .getSTClassByFullName(aPeerType);
      if (aPeerSTType != null && aPeerSTType.isExternal()) {
        continue;
      }
      // if (compareCommonMethods(anSTType, aPeerType, aTree) == null)
      // return null;
      compareCommonMethods(anSTType, aPeerType, aTree);

    }
    return true;

  }

}
