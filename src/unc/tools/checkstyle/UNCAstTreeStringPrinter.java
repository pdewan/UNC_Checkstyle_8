package unc.tools.checkstyle;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

import com.puppycrawl.tools.checkstyle.AstTreeStringPrinter;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.utils.TokenUtil;

public class UNCAstTreeStringPrinter {
  /** Newline pattern. */
  private static final Pattern NEWLINE = Pattern.compile("\n");
  /** Return pattern. */
  private static final Pattern RETURN = Pattern.compile("\r");
  /** Tab pattern. */
  private static final Pattern TAB = Pattern.compile("\t");

  /** OS specific line separator. */
  private static final String LINE_SEPARATOR = System.lineSeparator();
  protected static Method printTreeMethod;

  public static String printConcreteTree(DetailAST anAST) {
    try {
      if (printTreeMethod == null) {
        printTreeMethod = AstTreeStringPrinter.class.getDeclaredMethod("printTree",
                new Class[] { DetailAST.class });
        printTreeMethod.setAccessible(true);
      }

      String aResult = (String) printTreeMethod.invoke(AstTreeStringPrinter.class,
              new Object[] { anAST });
      return aResult;
    } catch (Exception e) {
      e.printStackTrace();
      return "Could not print tree";
    }

  }

  /**
   * Replace all control chars with escaped symbols.
   *
   * @param text
   *          the String to process.
   * @return the processed String with all control chars escaped.
   */
  private static String escapeAllControlChars(String text) {
    final String textWithoutNewlines = NEWLINE.matcher(text).replaceAll("\\\\n");
    final String textWithoutReturns = RETURN.matcher(textWithoutNewlines).replaceAll("\\\\r");
    return TAB.matcher(textWithoutReturns).replaceAll("\\\\t");
  }

  public static String getAbstractNodeInfo(DetailAST node) {
    return TokenUtil.getTokenName(node.getType()) + " -> " + escapeAllControlChars(node.getText());
    // + " [" + node.getLineNo() + ':' + node.getColumnNo() + ']';
  }
  public static String getNodeInfo(DetailAST node) {
    return  TokenUtil.getTokenName(node.getType()) + " -> " + escapeAllControlChars(node.getText());
    // + " [" + node.getLineNo() + ':' + node.getColumnNo() + ']';
  }
  public static String getLineInfo(DetailAST node) {
    return  " [" + node.getLineNo() + ':' + node.getColumnNo() + ']';
  }

  /**
   * Print AST.
   *
   * @param ast
   *          the root AST node.
   * @return string AST.
   */
  public static String printAbstractTree(DetailAST ast) {
    final StringBuilder messageBuilder = new StringBuilder(1024);
    addAbstractNodeInfo(messageBuilder, ast);
    addAbstractDescendents(messageBuilder, ast.getFirstChild());
    return messageBuilder.toString();
  }

  public static String printTree(DetailAST aRoot, boolean addLineInfo) {
    final StringBuilder messageBuilder = new StringBuilder(1024);
//    addAbstractNodeInfo(messageBuilder, ast);
//    if (addLineInfo) {
//      addLineInfo(messageBuilder, ast);
//    }
    addNodeInfo(messageBuilder, aRoot, addLineInfo);

    addDescendents(messageBuilder, aRoot.getFirstChild(), addLineInfo);
    return messageBuilder.toString();
  }
  public static void addAbstractDescendents(StringBuilder messageBuilder, DetailAST ast) {
    DetailAST node = ast;
    while (node != null) {
      addAbstractNodeInfo(messageBuilder, node);
      addAbstractDescendents(messageBuilder, node.getFirstChild());

      node = node.getNextSibling();
    }
  }
  public static void addDescendents(StringBuilder messageBuilder, DetailAST ast, boolean addLineInfo) {
    DetailAST node = ast;
    while (node != null) {
      addNodeInfo(messageBuilder, node, addLineInfo);
      addDescendents(messageBuilder, node.getFirstChild(), addLineInfo);

      node = node.getNextSibling();
    }
  }
  public static void addNodeInfo(StringBuilder messageBuilder, DetailAST node, boolean addLineInfo) {

      addAbstractNodeInfo(messageBuilder, node);
      if (addLineInfo) {
        addLineInfo(messageBuilder, node);
      }
      messageBuilder.append(LINE_SEPARATOR);
     
  }

  public static void addConcreteNodeInfo(StringBuilder messageBuilder, DetailAST node) {
    messageBuilder.append(getIndentation(node)).append(getConcreteNodeInfo(node))
            .append(LINE_SEPARATOR);
  }

  public static void addAbstractNodeInfo(StringBuilder messageBuilder, DetailAST node) {
//    messageBuilder.append(getIndentation(node)).append(getAbstractNodeInfo(node))
//            .append(LINE_SEPARATOR);
    messageBuilder.append(getIndentation(node)).append(getAbstractNodeInfo(node));
//    .append(LINE_SEPARATOR);
  }
  public static void addLineInfo(StringBuilder messageBuilder,DetailAST node) {
    messageBuilder.append(" [").append(node.getLineNo()).append(':').append(node.getColumnNo()).append(']');
  }

  private static String getConcreteNodeInfo(DetailAST node) {
    return TokenUtil.getTokenName(node.getType()) + " -> " + escapeAllControlChars(node.getText())
            + " [" + node.getLineNo() + ':' + node.getColumnNo() + ']';
  }

  /**
   * Get indentation for an AST node.
   *
   * @param ast
   *          the AST to get the indentation for.
   * @return the indentation in String format.
   */
  private static String getIndentation(DetailAST ast) {
    final boolean isLastChild = ast.getNextSibling() == null;
    DetailAST node = ast;
    final StringBuilder indentation = new StringBuilder(1024);
    while (node.getParent() != null) {
      node = node.getParent();
      if (node.getParent() == null) {
        if (isLastChild) {
          // only ASCII symbols must be used due to
          // problems with running tests on Windows
          indentation.append("`--");
        } else {
          indentation.append("|--");
        }
      } else {
        if (node.getNextSibling() == null) {
          indentation.insert(0, "    ");
        } else {
          indentation.insert(0, "|   ");
        }
      }
    }
    return indentation.toString();
  }
}
