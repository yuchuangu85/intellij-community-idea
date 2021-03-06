/* The following code was generated by JFlex 1.7.0 tweaked for IntelliJ platform */

package org.toml.lang.lexer;
import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import static org.toml.lang.psi.TomlElementTypes.*;


/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.7.0
 * from the specification file <tt>TomlLexer.flex</tt>
 */
public class _TomlLexer implements FlexLexer {

  /** This character denotes the end of file */
  public static final int YYEOF = -1;

  /** initial size of the lookahead buffer */
  private static final int ZZ_BUFFERSIZE = 16384;

  /** lexical states */
  public static final int YYINITIAL = 0;

  /**
   * ZZ_LEXSTATE[l] is the state in the DFA for the lexical state l
   * ZZ_LEXSTATE[l+1] is the state in the DFA for the lexical state l
   *                  at the beginning of a line
   * l is of the form l = 2*k, k a non negative integer
   */
  private static final int ZZ_LEXSTATE[] = { 
     0, 0
  };

  /** 
   * Translates characters to character classes
   * Chosen bits are [8, 6, 7]
   * Total runtime size is 1040 bytes
   */
  public static int ZZ_CMAP(int ch) {
    return ZZ_CMAP_A[ZZ_CMAP_Y[ZZ_CMAP_Z[ch>>13]|((ch>>7)&0x3f)]|(ch&0x7f)];
  }

  /* The ZZ_CMAP_Z table has 136 entries */
  static final char ZZ_CMAP_Z[] = zzUnpackCMap(
    "\1\0\207\100");

  /* The ZZ_CMAP_Y table has 128 entries */
  static final char ZZ_CMAP_Y[] = zzUnpackCMap(
    "\1\0\177\200");

  /* The ZZ_CMAP_A table has 256 entries */
  static final char ZZ_CMAP_A[] = zzUnpackCMap(
    "\11\0\1\1\1\2\1\0\1\1\1\2\22\0\1\1\1\0\1\40\1\3\3\0\1\41\3\0\1\17\1\43\1\14"+
    "\1\31\1\0\1\20\1\27\6\25\2\15\1\34\2\0\1\42\3\0\4\23\1\30\1\23\15\36\1\35"+
    "\5\36\1\16\1\44\1\37\1\45\1\0\1\21\1\0\1\11\1\26\2\23\1\7\1\10\2\36\1\32\2"+
    "\36\1\12\1\36\1\33\1\24\2\36\1\5\1\13\1\4\1\6\2\36\1\22\1\36\1\16\1\46\1\0"+
    "\1\47\202\0");

  /** 
   * Translates DFA states to action switch labels.
   */
  private static final int [] ZZ_ACTION = zzUnpackAction();

  private static final String ZZ_ACTION_PACKED_0 =
    "\1\0\1\1\1\2\1\3\4\4\1\5\1\1\1\5"+
    "\1\6\2\4\1\7\1\10\1\11\1\12\1\13\1\14"+
    "\1\15\1\16\2\4\2\5\1\4\1\5\1\4\1\0"+
    "\2\17\2\0\1\5\5\4\3\7\3\10\2\4\1\5"+
    "\1\4\1\17\1\0\1\17\1\5\1\0\2\17\4\0"+
    "\1\5\3\17\1\7\1\20\1\10\1\21\1\22\1\4"+
    "\2\17\1\5\1\0\1\5\1\20\1\0\1\21\2\0"+
    "\1\4\1\0\1\20\1\0\1\21\1\0\1\4\2\0"+
    "\1\20\1\0\1\21\1\4\1\0\1\20\1\21\1\4"+
    "\1\23\1\4\1\0\1\23\1\0\1\24\1\0\1\23"+
    "\2\4\1\24\1\0\2\4\1\0\2\4\1\0";

  private static int [] zzUnpackAction() {
    int [] result = new int[116];
    int offset = 0;
    offset = zzUnpackAction(ZZ_ACTION_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAction(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /** 
   * Translates a state to a row index in the transition table
   */
  private static final int [] ZZ_ROWMAP = zzUnpackRowMap();

  private static final String ZZ_ROWMAP_PACKED_0 =
    "\0\0\0\50\0\120\0\170\0\240\0\310\0\360\0\u0118"+
    "\0\u0140\0\u0168\0\u0190\0\50\0\u01b8\0\u01e0\0\u0208\0\u0230"+
    "\0\50\0\50\0\50\0\50\0\50\0\50\0\u0258\0\u0280"+
    "\0\u02a8\0\u02d0\0\u02f8\0\u0320\0\u0348\0\u0370\0\u0398\0\u03c0"+
    "\0\u03e8\0\u0410\0\u0438\0\u0460\0\u0488\0\u04b0\0\u04d8\0\u0500"+
    "\0\u0528\0\u0550\0\u0578\0\u05a0\0\u05c8\0\u05f0\0\u0618\0\u0640"+
    "\0\u0668\0\u0690\0\u06b8\0\u06e0\0\310\0\u0708\0\u0730\0\u0758"+
    "\0\u0780\0\u07a8\0\u07d0\0\u07f8\0\u0820\0\u0848\0\u0870\0\u0898"+
    "\0\u08c0\0\50\0\u08e8\0\50\0\u0910\0\310\0\u0938\0\u0960"+
    "\0\50\0\u0988\0\u09b0\0\u09d8\0\u0a00\0\u0a28\0\u0a50\0\u0a78"+
    "\0\u0aa0\0\u0ac8\0\u0af0\0\u0b18\0\u0b40\0\u0b68\0\u0b90\0\u0bb8"+
    "\0\u0be0\0\u0c08\0\50\0\u0c30\0\50\0\u0c58\0\u0c80\0\u0b40"+
    "\0\u0b90\0\u0ca8\0\u0cd0\0\u0cf8\0\u0d20\0\50\0\u0d48\0\u0d70"+
    "\0\u0d98\0\u0dc0\0\u0de8\0\u0e10\0\310\0\u0e38\0\u0e60\0\u0e88"+
    "\0\u0eb0\0\u0ed8\0\u0f00\0\u0f28";

  private static int [] zzUnpackRowMap() {
    int [] result = new int[116];
    int offset = 0;
    offset = zzUnpackRowMap(ZZ_ROWMAP_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackRowMap(String packed, int offset, int [] result) {
    int i = 0;  /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int high = packed.charAt(i++) << 16;
      result[j++] = high | packed.charAt(i++);
    }
    return j;
  }

  /** 
   * The transition table of the DFA
   */
  private static final int [] ZZ_TRANS = zzUnpackTrans();

  private static final String ZZ_TRANS_PACKED_0 =
    "\1\2\2\3\1\4\1\5\3\6\1\7\3\6\1\10"+
    "\1\11\1\6\1\12\1\13\4\6\1\11\1\6\1\11"+
    "\1\6\1\14\1\15\1\16\1\2\2\6\1\2\1\17"+
    "\1\20\1\21\1\22\1\23\1\24\1\25\1\26\51\0"+
    "\2\3\45\0\2\4\1\0\45\4\4\0\1\6\1\27"+
    "\11\6\1\0\11\6\1\0\2\6\1\0\2\6\15\0"+
    "\13\6\1\0\11\6\1\0\2\6\1\0\2\6\15\0"+
    "\5\6\1\30\5\6\1\0\11\6\1\0\2\6\1\0"+
    "\2\6\15\0\11\6\1\31\1\6\1\0\1\32\4\6"+
    "\1\31\1\6\1\31\1\6\1\0\1\15\1\16\1\0"+
    "\2\6\15\0\3\6\1\33\5\6\1\34\1\6\1\0"+
    "\1\34\1\35\3\6\1\34\1\6\1\34\1\33\1\36"+
    "\2\6\1\0\2\6\26\0\1\37\2\0\1\40\4\0"+
    "\1\37\1\0\1\37\2\0\1\41\1\42\20\0\3\6"+
    "\1\33\5\6\1\43\1\6\1\0\1\43\1\6\1\44"+
    "\1\6\1\45\1\43\1\46\1\43\1\33\1\36\2\6"+
    "\1\0\2\6\15\0\13\6\1\0\11\6\1\0\1\6"+
    "\1\47\1\0\2\6\15\0\5\6\1\50\5\6\1\0"+
    "\11\6\1\0\2\6\1\0\2\6\11\0\2\51\1\0"+
    "\34\51\1\52\1\53\7\51\2\54\1\0\34\54\1\55"+
    "\1\54\1\56\6\54\4\0\2\6\1\57\10\6\1\0"+
    "\11\6\1\0\2\6\1\0\2\6\15\0\6\6\1\60"+
    "\4\6\1\0\11\6\1\0\2\6\1\0\2\6\15\0"+
    "\3\6\1\33\5\6\1\31\1\6\1\0\1\31\1\35"+
    "\3\6\1\31\1\6\1\31\1\33\1\36\2\6\1\0"+
    "\2\6\15\0\3\6\1\33\5\6\1\61\1\6\1\0"+
    "\1\61\4\6\1\61\1\6\1\61\1\33\1\36\2\6"+
    "\1\0\2\6\15\0\10\6\1\62\1\63\1\6\1\64"+
    "\1\65\4\6\1\63\1\6\1\63\1\6\1\0\2\6"+
    "\1\0\2\6\15\0\3\6\1\33\5\6\1\66\1\6"+
    "\1\0\1\66\1\35\3\6\1\66\1\6\1\66\1\33"+
    "\1\36\2\6\1\67\2\6\15\0\11\6\1\70\1\6"+
    "\1\0\1\70\4\6\1\70\1\6\1\70\1\6\1\0"+
    "\2\6\1\0\2\6\26\0\1\71\2\0\1\71\4\0"+
    "\1\71\1\0\1\71\27\0\1\72\5\0\1\37\2\0"+
    "\1\37\1\73\3\0\1\37\1\0\1\37\1\72\1\36"+
    "\25\0\1\72\20\0\1\72\1\36\51\0\1\74\25\0"+
    "\1\75\42\0\11\6\1\76\1\6\1\0\1\76\4\6"+
    "\1\76\1\6\1\76\1\6\1\0\2\6\1\67\2\6"+
    "\15\0\3\6\3\77\3\6\1\77\1\6\1\0\1\77"+
    "\2\6\1\77\1\6\4\77\1\0\2\6\1\0\2\6"+
    "\15\0\13\6\1\0\1\100\4\6\1\100\1\6\1\100"+
    "\1\6\1\0\2\6\1\0\2\6\15\0\13\6\1\0"+
    "\1\101\6\6\1\101\1\6\1\0\2\6\1\0\2\6"+
    "\15\0\4\6\1\65\6\6\1\0\11\6\1\0\2\6"+
    "\1\0\2\6\15\0\13\6\1\0\11\6\1\0\1\6"+
    "\1\65\1\0\2\6\11\0\2\51\1\0\34\51\1\52"+
    "\1\102\46\51\1\52\10\51\40\0\1\103\7\0\2\54"+
    "\1\0\34\54\1\55\1\54\1\104\45\54\1\55\10\54"+
    "\41\0\1\105\12\0\3\6\1\106\7\6\1\0\11\6"+
    "\1\0\2\6\1\0\2\6\15\0\7\6\1\57\3\6"+
    "\1\0\11\6\1\0\2\6\1\0\2\6\15\0\11\6"+
    "\1\61\1\6\1\0\1\61\4\6\1\61\1\6\1\61"+
    "\1\6\1\0\2\6\1\0\2\6\15\0\11\6\1\63"+
    "\1\6\1\0\1\65\4\6\1\63\1\6\1\63\1\6"+
    "\1\0\2\6\1\0\2\6\15\0\11\6\1\63\1\6"+
    "\1\0\1\63\1\107\3\6\1\63\1\6\1\63\1\6"+
    "\1\0\2\6\1\0\2\6\26\0\1\110\2\0\1\111"+
    "\4\0\1\110\1\0\1\110\24\0\3\6\1\33\5\6"+
    "\1\112\1\6\1\0\1\112\1\35\3\6\1\112\1\6"+
    "\1\112\1\33\1\36\2\6\1\0\2\6\26\0\1\113"+
    "\2\0\1\113\4\0\1\113\1\0\1\113\24\0\3\6"+
    "\1\33\5\6\1\70\1\6\1\0\1\70\1\35\3\6"+
    "\1\70\1\6\1\70\1\33\1\36\2\6\1\0\2\6"+
    "\20\0\1\72\5\0\1\71\2\0\1\71\1\36\3\0"+
    "\1\71\1\0\1\71\1\72\33\0\1\64\1\110\1\0"+
    "\1\64\1\111\4\0\1\110\1\0\1\110\35\0\1\37"+
    "\2\0\1\37\4\0\1\37\1\0\1\37\30\0\1\111"+
    "\72\0\1\111\20\0\11\6\1\114\1\6\1\0\1\114"+
    "\4\6\1\114\1\6\1\114\1\6\1\0\2\6\1\0"+
    "\2\6\15\0\3\6\3\77\3\6\1\77\1\6\1\0"+
    "\1\77\1\44\1\6\1\77\1\6\4\77\1\0\2\6"+
    "\1\0\2\6\15\0\13\6\1\0\1\100\1\45\3\6"+
    "\1\100\1\6\1\100\1\6\1\0\2\6\1\0\2\6"+
    "\15\0\13\6\1\0\1\101\1\46\5\6\1\101\1\6"+
    "\1\0\2\6\1\0\2\6\11\0\37\103\1\115\1\116"+
    "\7\103\37\105\1\117\1\105\1\120\6\105\4\0\11\6"+
    "\1\63\1\6\1\0\1\63\4\6\1\63\1\6\1\63"+
    "\1\6\1\0\2\6\1\0\2\6\26\0\1\110\2\0"+
    "\1\110\1\121\3\0\1\110\1\0\1\110\24\0\3\6"+
    "\1\33\4\6\1\122\1\31\1\6\1\0\1\31\1\35"+
    "\3\6\1\31\1\6\1\31\1\33\1\36\2\6\1\0"+
    "\2\6\26\0\1\123\2\0\1\123\4\0\1\123\1\0"+
    "\1\123\24\0\10\6\1\122\1\61\1\6\1\0\1\61"+
    "\4\6\1\61\1\6\1\61\1\6\1\0\2\6\1\0"+
    "\2\6\11\0\37\103\1\115\1\124\47\103\1\125\7\103"+
    "\37\105\1\117\1\105\1\126\47\105\1\127\6\105\15\0"+
    "\1\110\2\0\1\110\4\0\1\110\1\0\1\110\24\0"+
    "\11\6\1\130\1\6\1\0\1\130\4\6\1\130\1\6"+
    "\1\130\1\6\1\0\2\6\1\0\2\6\45\0\1\131"+
    "\13\0\37\103\1\115\1\132\47\103\1\133\7\103\37\105"+
    "\1\117\1\105\1\134\47\105\1\135\6\105\4\0\11\6"+
    "\1\136\1\6\1\0\1\136\4\6\1\136\1\6\1\136"+
    "\1\6\1\0\2\6\1\0\2\6\26\0\1\137\2\0"+
    "\1\137\4\0\1\137\1\0\1\137\20\0\40\103\1\140"+
    "\7\103\41\105\1\141\6\105\4\0\10\6\1\142\2\6"+
    "\1\0\11\6\1\0\2\6\1\0\2\6\26\0\1\143"+
    "\2\0\1\143\4\0\1\143\1\0\1\143\24\0\11\6"+
    "\1\144\1\6\1\0\1\144\4\6\1\144\1\6\1\144"+
    "\1\6\1\0\2\6\1\0\2\6\25\0\1\145\1\0"+
    "\1\146\1\145\11\0\1\147\22\0\11\6\1\150\1\6"+
    "\1\0\1\150\4\6\1\150\1\6\1\150\1\6\1\0"+
    "\2\6\1\0\2\6\26\0\1\151\2\0\1\151\4\0"+
    "\1\151\1\0\1\151\35\0\1\152\2\0\1\152\4\0"+
    "\1\152\1\0\1\152\24\0\1\153\7\6\1\154\1\6"+
    "\1\155\1\145\11\6\1\0\2\6\1\0\1\153\1\6"+
    "\26\0\1\156\2\0\1\156\4\0\1\156\1\0\1\156"+
    "\34\0\1\145\1\152\1\146\1\145\1\152\4\0\1\152"+
    "\1\0\1\152\24\0\11\6\1\157\1\6\1\0\1\157"+
    "\4\6\1\157\1\6\1\157\1\6\1\0\2\6\1\0"+
    "\2\6\15\0\11\6\1\160\1\6\1\0\1\160\4\6"+
    "\1\160\1\6\1\160\1\6\1\0\2\6\1\0\2\6"+
    "\45\0\1\161\17\0\11\6\1\162\1\6\1\0\1\162"+
    "\4\6\1\162\1\6\1\162\1\6\1\0\2\6\1\0"+
    "\2\6\15\0\11\6\1\163\1\6\1\0\1\163\4\6"+
    "\1\163\1\6\1\163\1\6\1\0\2\6\1\0\2\6"+
    "\26\0\1\164\2\0\1\164\4\0\1\164\1\0\1\164"+
    "\24\0\13\6\1\0\11\6\1\0\2\6\1\67\2\6"+
    "\15\0\13\6\1\0\11\6\1\0\2\6\1\161\2\6"+
    "\26\0\1\146\2\0\1\146\4\0\1\146\1\0\1\146"+
    "\20\0";

  private static int [] zzUnpackTrans() {
    int [] result = new int[3920];
    int offset = 0;
    offset = zzUnpackTrans(ZZ_TRANS_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackTrans(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }


  /* error codes */
  private static final int ZZ_UNKNOWN_ERROR = 0;
  private static final int ZZ_NO_MATCH = 1;
  private static final int ZZ_PUSHBACK_2BIG = 2;

  /* error messages for the codes above */
  private static final String[] ZZ_ERROR_MSG = {
    "Unknown internal scanner error",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * ZZ_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private static final int [] ZZ_ATTRIBUTE = zzUnpackAttribute();

  private static final String ZZ_ATTRIBUTE_PACKED_0 =
    "\1\0\1\11\11\1\1\11\4\1\6\11\7\1\1\0"+
    "\2\1\2\0\21\1\1\0\2\1\1\0\2\1\4\0"+
    "\4\1\1\11\1\1\1\11\4\1\1\11\1\1\1\0"+
    "\2\1\1\0\1\1\2\0\1\1\1\0\1\1\1\0"+
    "\1\1\1\0\1\1\2\0\1\11\1\0\1\11\1\1"+
    "\1\0\5\1\1\0\1\11\1\0\1\1\1\0\4\1"+
    "\1\0\2\1\1\0\2\1\1\0";

  private static int [] zzUnpackAttribute() {
    int [] result = new int[116];
    int offset = 0;
    offset = zzUnpackAttribute(ZZ_ATTRIBUTE_PACKED_0, offset, result);
    return result;
  }

  private static int zzUnpackAttribute(String packed, int offset, int [] result) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      do result[j++] = value; while (--count > 0);
    }
    return j;
  }

  /** the input device */
  private java.io.Reader zzReader;

  /** the current state of the DFA */
  private int zzState;

  /** the current lexical state */
  private int zzLexicalState = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private CharSequence zzBuffer = "";

  /** the textposition at the last accepting state */
  private int zzMarkedPos;

  /** the current text position in the buffer */
  private int zzCurrentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int zzStartRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int zzEndRead;

  /**
   * zzAtBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean zzAtBOL = true;

  /** zzAtEOF == true <=> the scanner is at the EOF */
  private boolean zzAtEOF;

  /** denotes if the user-EOF-code has already been executed */
  private boolean zzEOFDone;

  /* user code: */
  public _TomlLexer() {
    this((java.io.Reader)null);
  }


  /**
   * Creates a new scanner
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public _TomlLexer(java.io.Reader in) {
    this.zzReader = in;
  }


  /** 
   * Unpacks the compressed character translation table.
   *
   * @param packed   the packed character translation table
   * @return         the unpacked character translation table
   */
  private static char [] zzUnpackCMap(String packed) {
    int size = 0;
    for (int i = 0, length = packed.length(); i < length; i += 2) {
      size += packed.charAt(i);
    }
    char[] map = new char[size];
    int i = 0;  /* index in packed string  */
    int j = 0;  /* index in unpacked array */
    while (i < packed.length()) {
      int  count = packed.charAt(i++);
      char value = packed.charAt(i++);
      do map[j++] = value; while (--count > 0);
    }
    return map;
  }

  public final int getTokenStart() {
    return zzStartRead;
  }

  public final int getTokenEnd() {
    return getTokenStart() + yylength();
  }

  public void reset(CharSequence buffer, int start, int end, int initialState) {
    zzBuffer = buffer;
    zzCurrentPos = zzMarkedPos = zzStartRead = start;
    zzAtEOF  = false;
    zzAtBOL = true;
    zzEndRead = end;
    yybegin(initialState);
  }

  /**
   * Refills the input buffer.
   *
   * @return      {@code false}, iff there was new input.
   *
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  private boolean zzRefill() throws java.io.IOException {
    return true;
  }


  /**
   * Returns the current lexical state.
   */
  public final int yystate() {
    return zzLexicalState;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  public final void yybegin(int newState) {
    zzLexicalState = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  public final CharSequence yytext() {
    return zzBuffer.subSequence(zzStartRead, zzMarkedPos);
  }


  /**
   * Returns the character at position {@code pos} from the
   * matched text.
   *
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch.
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  public final char yycharat(int pos) {
    return zzBuffer.charAt(zzStartRead+pos);
  }


  /**
   * Returns the length of the matched text region.
   */
  public final int yylength() {
    return zzMarkedPos-zzStartRead;
  }


  /**
   * Reports an error that occurred while scanning.
   *
   * In a wellformed scanner (no or only correct usage of
   * yypushback(int) and a match-all fallback rule) this method
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void zzScanError(int errorCode) {
    String message;
    try {
      message = ZZ_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = ZZ_ERROR_MSG[ZZ_UNKNOWN_ERROR];
    }

    throw new Error(message);
  }


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  public void yypushback(int number)  {
    if ( number > yylength() )
      zzScanError(ZZ_PUSHBACK_2BIG);

    zzMarkedPos -= number;
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   java.io.IOException  if any I/O-Error occurs
   */
  public IElementType advance() throws java.io.IOException {
    int zzInput;
    int zzAction;

    // cached fields:
    int zzCurrentPosL;
    int zzMarkedPosL;
    int zzEndReadL = zzEndRead;
    CharSequence zzBufferL = zzBuffer;

    int [] zzTransL = ZZ_TRANS;
    int [] zzRowMapL = ZZ_ROWMAP;
    int [] zzAttrL = ZZ_ATTRIBUTE;

    while (true) {
      zzMarkedPosL = zzMarkedPos;

      zzAction = -1;

      zzCurrentPosL = zzCurrentPos = zzStartRead = zzMarkedPosL;

      zzState = ZZ_LEXSTATE[zzLexicalState];

      // set up zzAction for empty match case:
      int zzAttributes = zzAttrL[zzState];
      if ( (zzAttributes & 1) == 1 ) {
        zzAction = zzState;
      }


      zzForAction: {
        while (true) {

          if (zzCurrentPosL < zzEndReadL) {
            zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL/*, zzEndReadL*/);
            zzCurrentPosL += Character.charCount(zzInput);
          }
          else if (zzAtEOF) {
            zzInput = YYEOF;
            break zzForAction;
          }
          else {
            // store back cached positions
            zzCurrentPos  = zzCurrentPosL;
            zzMarkedPos   = zzMarkedPosL;
            boolean eof = zzRefill();
            // get translated positions and possibly new buffer
            zzCurrentPosL  = zzCurrentPos;
            zzMarkedPosL   = zzMarkedPos;
            zzBufferL      = zzBuffer;
            zzEndReadL     = zzEndRead;
            if (eof) {
              zzInput = YYEOF;
              break zzForAction;
            }
            else {
              zzInput = Character.codePointAt(zzBufferL, zzCurrentPosL/*, zzEndReadL*/);
              zzCurrentPosL += Character.charCount(zzInput);
            }
          }
          int zzNext = zzTransL[ zzRowMapL[zzState] + ZZ_CMAP(zzInput) ];
          if (zzNext == -1) break zzForAction;
          zzState = zzNext;

          zzAttributes = zzAttrL[zzState];
          if ( (zzAttributes & 1) == 1 ) {
            zzAction = zzState;
            zzMarkedPosL = zzCurrentPosL;
            if ( (zzAttributes & 8) == 8 ) break zzForAction;
          }

        }
      }

      // store back cached position
      zzMarkedPos = zzMarkedPosL;

      if (zzInput == YYEOF && zzStartRead == zzCurrentPos) {
        zzAtEOF = true;
        return null;
      }
      else {
        switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction]) {
          case 1: 
            { return com.intellij.psi.TokenType.BAD_CHARACTER;
            } 
            // fall through
          case 21: break;
          case 2: 
            { return com.intellij.psi.TokenType.WHITE_SPACE;
            } 
            // fall through
          case 22: break;
          case 3: 
            { return COMMENT;
            } 
            // fall through
          case 23: break;
          case 4: 
            { return BARE_KEY;
            } 
            // fall through
          case 24: break;
          case 5: 
            { return BARE_KEY_OR_NUMBER;
            } 
            // fall through
          case 25: break;
          case 6: 
            { return DOT;
            } 
            // fall through
          case 26: break;
          case 7: 
            { return BASIC_STRING;
            } 
            // fall through
          case 27: break;
          case 8: 
            { return LITERAL_STRING;
            } 
            // fall through
          case 28: break;
          case 9: 
            { return EQ;
            } 
            // fall through
          case 29: break;
          case 10: 
            { return COMMA;
            } 
            // fall through
          case 30: break;
          case 11: 
            { return L_BRACKET;
            } 
            // fall through
          case 31: break;
          case 12: 
            { return R_BRACKET;
            } 
            // fall through
          case 32: break;
          case 13: 
            { return L_CURLY;
            } 
            // fall through
          case 33: break;
          case 14: 
            { return R_CURLY;
            } 
            // fall through
          case 34: break;
          case 15: 
            { return NUMBER;
            } 
            // fall through
          case 35: break;
          case 16: 
            { return MULTILINE_BASIC_STRING;
            } 
            // fall through
          case 36: break;
          case 17: 
            { return MULTILINE_LITERAL_STRING;
            } 
            // fall through
          case 37: break;
          case 18: 
            { return BOOLEAN;
            } 
            // fall through
          case 38: break;
          case 19: 
            { return DATE_TIME;
            } 
            // fall through
          case 39: break;
          case 20: 
            { return BARE_KEY_OR_DATE;
            } 
            // fall through
          case 40: break;
          default:
            zzScanError(ZZ_NO_MATCH);
          }
      }
    }
  }


}
