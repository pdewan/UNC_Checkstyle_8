package unc.symbolTable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class EmptyHashMap extends HashMap implements Map {
  public EmptyHashMap() {
    super(0);
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  
  @Override
  public Object put(Object arg0, Object arg1) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void putAll(Map arg0) {
    throw new UnsupportedOperationException();
    
  }

  @Override
  public Object remove(Object arg0) {
    throw new UnsupportedOperationException();
  }

  

}
