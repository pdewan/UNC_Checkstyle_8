package unc.testables.commonSignatures;

public class ADelegatorClass extends Object  implements InterfaceSignature1{
  InterfaceSignature1 delegate = new ADelegateClass();
  public int commonSignature1(boolean aBoolean, int anInteger, String aString) {
    return delegate.commonSignature1(aBoolean, anInteger, aString);
  }
  void commonSignature2() {
    
  }
  static void commonSignatureStatic(double aDouble) {
    
  }
  void unique1Signature(char aCharacter) {
    
  }

}
