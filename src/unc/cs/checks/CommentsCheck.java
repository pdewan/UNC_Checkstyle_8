package unc.cs.checks;

import java.util.List;
import java.util.Map;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TextBlock;

public class CommentsCheck extends AbstractCheck {
    public static final String MSG_KEY = "comments";
    @Override
    public int[] getDefaultTokens() {
        return new int[0];
    }

	@Override
    public void beginTree(DetailAST rootAST) {
//        final Map<Integer, TextBlock> cppComments = getFileContents().get
//                .getCppComments();
        final Map<Integer, List<TextBlock>> blockComments = getFileContents().getBlockComments();
               
        final Map<Integer, TextBlock> singleLine = getFileContents()
                .getSingleLineComments();
//                .
//       final Map<Integer, List<TextBlock>> cComments = getFileContents()
//                        .getCComments();
//        int aCppLength = cppComments.size();
//        int aCCommentsLength = cComments.size();
	}

  @Override
  public int[] getAcceptableTokens() {
    // TODO Auto-generated method stub
    return getDefaultTokens();
  }

  @Override
  public int[] getRequiredTokens() {
    // TODO Auto-generated method stub
    return null;
  }

}
