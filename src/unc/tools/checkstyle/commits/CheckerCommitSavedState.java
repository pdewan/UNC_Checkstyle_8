package unc.tools.checkstyle.commits;

import java.util.List;
import java.util.Map;
import java.util.List;

public interface CheckerCommitSavedState {

  Map<String, List<String>> getClassNameToFileAndCommit();

  void setClassNameToFileAndCommit(Map<String, List<String>> classNameToFileAndCommit);

//  Map<String, List<String>> getCommitToClassAndFilesDeleted();
//
//  void setCommitToClassAndFilesDeleted(Map<String, List<String>> commitToClassAndFilesDeleted);
//
//  Map<String, List<String>> getCommitToClassAndFilesAdded();
//
//  void setCommitToClassAndFilesAdded(Map<String, List<String>> commitToClassAndFilesAdded);
//
//  Map<String, List<String>> getCommitToClassAndFilesChanged();
//
//  void setCommitToClassAndFilesChanged(Map<String, List<String>> commitToClassAndFilesChanged);
//
//  List<String> getAllFilesSeen();
//
//  void setAllFilesSeen(List<String> allFilesSeen);
//
//  List<String> getAllClassesSeen();
//
//  void setAllClassesSeen(List<String> allClassesSeen);
//
//  List<String> getAllFilesModified();
//
//  void setAllFilesModified(List<String> allFilesModified);

}