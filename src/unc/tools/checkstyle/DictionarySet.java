package unc.tools.checkstyle;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import unc.tools.checkstyle.dictionary.DictionaryA;
import unc.tools.checkstyle.dictionary.DictionaryB;
import unc.tools.checkstyle.dictionary.DictionaryC;
import unc.tools.checkstyle.dictionary.DictionaryD;
import unc.tools.checkstyle.dictionary.DictionaryE;
import unc.tools.checkstyle.dictionary.DictionaryF;
import unc.tools.checkstyle.dictionary.DictionaryG;
import unc.tools.checkstyle.dictionary.DictionaryH;
import unc.tools.checkstyle.dictionary.DictionaryI;
import unc.tools.checkstyle.dictionary.DictionaryJ;
import unc.tools.checkstyle.dictionary.DictionaryK;
import unc.tools.checkstyle.dictionary.DictionaryL;
import unc.tools.checkstyle.dictionary.DictionaryM;
import unc.tools.checkstyle.dictionary.DictionaryN;
import unc.tools.checkstyle.dictionary.DictionaryO;
import unc.tools.checkstyle.dictionary.DictionaryP;
import unc.tools.checkstyle.dictionary.DictionaryQ;
import unc.tools.checkstyle.dictionary.DictionaryR;
import unc.tools.checkstyle.dictionary.DictionaryS;
import unc.tools.checkstyle.dictionary.DictionarySPlus;
import unc.tools.checkstyle.dictionary.DictionaryT;
import unc.tools.checkstyle.dictionary.DictionaryU;
import unc.tools.checkstyle.dictionary.DictionaryV;
import unc.tools.checkstyle.dictionary.DictionaryW;
import unc.tools.checkstyle.dictionary.DictionaryX;
import unc.tools.checkstyle.dictionary.DictionaryY;
import unc.tools.checkstyle.dictionary.DictionaryZ;

public class DictionarySet {
  static Set<String> dictionarySet;
  static String[] numbers = {
      "one",
      "two",
      "three",
      "four",
      "five",
      "six",
      "seven",
      "eight",
      "nine",
      "ten",
      "eleven",
      "twelve",
      "thirteen",
      "fourteen",
      "fifteen",
      "sixteen",
      "seventeen",
      "eighteen",
      "nineteen",
      "twenty",
      "thirty",
      "forty",
      "fifty",
      "sixty",
      "seventy",
      "eighty",
      "ninety",
      "hundred",
      "thousand",
      "million",
      "billion"
  };
  static Set<String> numberSet;

  static String[] extraWords = {
      "io", 
      "cpu",
      "util",
      "num",
      "retval",
      "val",
      "weka",
      "nio,",
      "xml",
      "durations"
  };
  
  public static String[] toLowerCase(String[] aMixedCaseArray) {
    for (int i = 0; i < aMixedCaseArray.length; i++) {
      aMixedCaseArray[i] = aMixedCaseArray[i].toLowerCase();
    }
    return aMixedCaseArray;
    
  }
  public static Set<String> getNumbers() {
    if (numberSet == null) {
      numberSet = new HashSet();
      numberSet.addAll(Arrays.asList(numbers));
    }
    return numberSet;
  }
  public static Set<String> getDictionary() {
	  if (dictionarySet == null) {
		  dictionarySet = new HashSet();
		  dictionarySet.addAll(Arrays.asList(toLowerCase(DictionaryA.dictionary)));
		  dictionarySet.addAll(Arrays.asList(DictionaryB.dictionary));
		  dictionarySet.addAll(Arrays.asList(toLowerCase(DictionaryC.dictionary)));
		  dictionarySet.addAll(Arrays.asList(toLowerCase(DictionaryD.dictionary)));
		  dictionarySet.addAll(Arrays.asList(toLowerCase(DictionaryE.dictionary)));
		  dictionarySet.addAll(Arrays.asList(toLowerCase(DictionaryF.dictionary)));
		  dictionarySet.addAll(Arrays.asList(toLowerCase(DictionaryG.dictionary)));
		  dictionarySet.addAll(Arrays.asList(toLowerCase(DictionaryH.dictionary)));
		  dictionarySet.addAll(Arrays.asList(toLowerCase(DictionaryI.dictionary)));
		  dictionarySet.addAll(Arrays.asList(toLowerCase(DictionaryJ.dictionary)));
		  dictionarySet.addAll(Arrays.asList(toLowerCase(DictionaryK.dictionary)));
		  dictionarySet.addAll(Arrays.asList(toLowerCase(DictionaryL.dictionary)));
		  dictionarySet.addAll(Arrays.asList(toLowerCase(DictionaryM.dictionary)));
		  dictionarySet.addAll(Arrays.asList(toLowerCase(DictionaryN.dictionary)));
		  dictionarySet.addAll(Arrays.asList(toLowerCase(DictionaryO.dictionary)));
		  dictionarySet.addAll(Arrays.asList(toLowerCase(DictionaryP.dictionary)));
		  dictionarySet.addAll(Arrays.asList(toLowerCase(DictionaryQ.dictionary)));
		  dictionarySet.addAll(Arrays.asList(toLowerCase(DictionaryR.dictionary)));
		  dictionarySet.addAll(Arrays.asList(toLowerCase(DictionaryS.dictionary)));
		  dictionarySet.addAll(Arrays.asList(toLowerCase(DictionarySPlus.dictionary)));
		  dictionarySet.addAll(Arrays.asList(toLowerCase(DictionaryT.dictionary)));
		  dictionarySet.addAll(Arrays.asList(toLowerCase(DictionaryU.dictionary)));
		  dictionarySet.addAll(Arrays.asList(toLowerCase(DictionaryV.dictionary)));
		  dictionarySet.addAll(Arrays.asList(toLowerCase(DictionaryW.dictionary)));
		  dictionarySet.addAll(Arrays.asList(toLowerCase(DictionaryX.dictionary)));
		  dictionarySet.addAll(Arrays.asList(toLowerCase(DictionaryY.dictionary)));
		  dictionarySet.addAll(Arrays.asList(toLowerCase(DictionaryZ.dictionary)));		  
		  dictionarySet.addAll(Arrays.asList(extraWords));  
	  }
	  return dictionarySet;
  }
	

}
