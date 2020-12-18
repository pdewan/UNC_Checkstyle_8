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
  static String[] extraWords = {
      "io", 
      "cpu",
      "util",
      "num",
      "retval",
      "val",
      "weka",
      "nio,",
      "xml"
  };
  public static Set<String> getDictionary() {
	  if (dictionarySet == null) {
		  dictionarySet = new HashSet();
		  dictionarySet.addAll(Arrays.asList(DictionaryA.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryB.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryC.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryD.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryE.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryF.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryG.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryH.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryI.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryJ.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryK.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryL.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryM.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryN.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryO.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryP.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryQ.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryR.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryS.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionarySPlus.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryT.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryU.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryV.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryW.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryX.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryY.dictionary));
		  dictionarySet.addAll(Arrays.asList(DictionaryZ.dictionary));		  
		  dictionarySet.addAll(Arrays.asList(extraWords));  
	  }
	  return dictionarySet;
  }
	

}
