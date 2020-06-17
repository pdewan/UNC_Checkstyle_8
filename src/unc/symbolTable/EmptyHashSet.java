package unc.symbolTable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class EmptyHashSet extends HashSet implements Set {
  public EmptyHashSet() {
    super(0);
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean add(Object e) {
    throw new UnsupportedOperationException();

  }

  @Override
  public boolean addAll(Collection c) {
    throw new UnsupportedOperationException();

  }

  

  @Override
  public boolean remove(Object o) {
    throw new UnsupportedOperationException();

  }

  @Override
  public boolean removeAll(Collection c) {
    throw new UnsupportedOperationException();

  }



  
  
  

}
