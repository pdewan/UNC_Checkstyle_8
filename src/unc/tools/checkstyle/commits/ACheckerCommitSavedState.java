package unc.tools.checkstyle.commits;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.List;

public class ACheckerCommitSavedState implements CheckerCommitSavedState  {
  protected  Map<String, List<String>> classNameToFileAndCommit;
  protected  Map<String, List<String>> commitToClassAndFilesDeleted;
  protected  Map<String, List<String>> commitToClassAndFilesAdded;;
  protected  Map<String, List<String>> commitToClassAndFilesChanged;;
  protected List<String> allFilesSeen ;
  protected List<String> allClassesSeen;
  protected List<String> allFilesModified;;
  public ACheckerCommitSavedState() {
    
  }

  public ACheckerCommitSavedState(Map<String, List<String>> classNameToFileAndCommit,
          Map<String, List<String>> commitToClassAndFilesDeleted,
          Map<String, List<String>> commitToClassAndFilesAdded,
          Map<String, List<String>> commitToClassAndFilesChanged, List<String> allFilesSeen,
          List<String> allClassesSeen, List<String> allFilesModified) {
    super();
    this.classNameToFileAndCommit = classNameToFileAndCommit;
    this.commitToClassAndFilesDeleted = commitToClassAndFilesDeleted;
    this.commitToClassAndFilesAdded = commitToClassAndFilesAdded;
    this.commitToClassAndFilesChanged = commitToClassAndFilesChanged;
    this.allFilesSeen = allFilesSeen;
    this.allClassesSeen = allClassesSeen;
    this.allFilesModified = allFilesModified;
  }
  /* (non-Javadoc)
   * @see unc.tools.checkstyle.commits.CheckerCommitSavedState#getClassNameToFileAndCommit()
   */
  @Override
  public Map<String, List<String>> getClassNameToFileAndCommit() {
    return classNameToFileAndCommit;
  }
  /* (non-Javadoc)
   * @see unc.tools.checkstyle.commits.CheckerCommitSavedState#setClassNameToFileAndCommit(java.util.Map)
   */
  @Override
  public void setClassNameToFileAndCommit(Map<String, List<String>> classNameToFileAndCommit) {
    this.classNameToFileAndCommit = classNameToFileAndCommit;
  }
  /* (non-Javadoc)
   * @see unc.tools.checkstyle.commits.CheckerCommitSavedState#getCommitToClassAndFilesDeleted()
   */
  @Override
  public Map<String, List<String>> getCommitToClassAndFilesDeleted() {
    return commitToClassAndFilesDeleted;
  }
  /* (non-Javadoc)
   * @see unc.tools.checkstyle.commits.CheckerCommitSavedState#setCommitToClassAndFilesDeleted(java.util.Map)
   */
  @Override
  public void setCommitToClassAndFilesDeleted(
          Map<String, List<String>> commitToClassAndFilesDeleted) {
    this.commitToClassAndFilesDeleted = commitToClassAndFilesDeleted;
  }
  /* (non-Javadoc)
   * @see unc.tools.checkstyle.commits.CheckerCommitSavedState#getCommitToClassAndFilesAdded()
   */
  @Override
  public Map<String, List<String>> getCommitToClassAndFilesAdded() {
    return commitToClassAndFilesAdded;
  }
  /* (non-Javadoc)
   * @see unc.tools.checkstyle.commits.CheckerCommitSavedState#setCommitToClassAndFilesAdded(java.util.Map)
   */
  @Override
  public void setCommitToClassAndFilesAdded(Map<String, List<String>> commitToClassAndFilesAdded) {
    this.commitToClassAndFilesAdded = commitToClassAndFilesAdded;
  }
  /* (non-Javadoc)
   * @see unc.tools.checkstyle.commits.CheckerCommitSavedState#getCommitToClassAndFilesChanged()
   */
  @Override
  public Map<String, List<String>> getCommitToClassAndFilesChanged() {
    return commitToClassAndFilesChanged;
  }
  /* (non-Javadoc)
   * @see unc.tools.checkstyle.commits.CheckerCommitSavedState#setCommitToClassAndFilesChanged(java.util.Map)
   */
  @Override
  public void setCommitToClassAndFilesChanged(
          Map<String, List<String>> commitToClassAndFilesChanged) {
    this.commitToClassAndFilesChanged = commitToClassAndFilesChanged;
  }
  /* (non-Javadoc)
   * @see unc.tools.checkstyle.commits.CheckerCommitSavedState#getAllFilesSeen()
   */
  @Override
  public List<String> getAllFilesSeen() {
    return allFilesSeen;
  }
  /* (non-Javadoc)
   * @see unc.tools.checkstyle.commits.CheckerCommitSavedState#setAllFilesSeen(java.util.List)
   */
  @Override
  public void setAllFilesSeen(List<String> allFilesSeen) {
    this.allFilesSeen = allFilesSeen;
  }
  /* (non-Javadoc)
   * @see unc.tools.checkstyle.commits.CheckerCommitSavedState#getAllClassesSeen()
   */
  @Override
  public List<String> getAllClassesSeen() {
    return allClassesSeen;
  }
  /* (non-Javadoc)
   * @see unc.tools.checkstyle.commits.CheckerCommitSavedState#setAllClassesSeen(java.util.List)
   */
  @Override
  public void setAllClassesSeen(List<String> allClassesSeen) {
    this.allClassesSeen = allClassesSeen;
  }
  /* (non-Javadoc)
   * @see unc.tools.checkstyle.commits.CheckerCommitSavedState#getAllFilesModified()
   */
  @Override
  public List<String> getAllFilesModified() {
    return allFilesModified;
  }
  /* (non-Javadoc)
   * @see unc.tools.checkstyle.commits.CheckerCommitSavedState#setAllFilesModified(java.util.List)
   */
  @Override
  public void setAllFilesModified(List<String> allFilesModified) {
    this.allFilesModified = allFilesModified;
  }
  

}
