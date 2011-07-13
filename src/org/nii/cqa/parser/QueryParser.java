
//----------------------------------------------------
// The following code was generated by CUP v0.11a beta 20060608
// Wed Jul 13 15:57:01 JST 2011
//----------------------------------------------------

package org.nii.cqa.parser;

import org.nii.cqa.base.*;
import java.util.*;
import java.io.*;

/** CUP v0.11a beta 20060608 generated parser.
  * @version Wed Jul 13 15:57:01 JST 2011
  */
public class QueryParser extends java_cup.runtime.lr_parser {

  /** Default constructor. */
  public QueryParser() {super();}

  /** Constructor which sets the default scanner. */
  public QueryParser(java_cup.runtime.Scanner s) {super(s);}

  /** Constructor which sets the default scanner. */
  public QueryParser(java_cup.runtime.Scanner s, java_cup.runtime.SymbolFactory sf) {super(s,sf);}

  /** Production table. */
  protected static final short _production_table[][] = 
    unpackFromStrings(new String[] {
    "\000\013\000\002\006\003\000\002\002\004\000\002\006" +
    "\005\000\002\005\003\000\002\005\004\000\002\004\004" +
    "\000\002\003\005\000\002\002\003\000\002\002\003\000" +
    "\002\002\005\000\002\002\005" });

  /** Access to production table. */
  public short[][] production_table() {return _production_table;}

  /** Parse-action table. */
  protected static final short[][] _action_table = 
    unpackFromStrings(new String[] {
    "\000\023\000\006\006\005\011\010\001\002\000\006\002" +
    "\001\005\001\001\002\000\004\011\010\001\002\000\006" +
    "\002\023\005\022\001\002\000\006\002\ufffe\005\ufffe\001" +
    "\002\000\004\007\011\001\002\000\006\011\014\012\015" +
    "\001\002\000\006\002\ufffc\005\ufffc\001\002\000\006\004" +
    "\016\010\017\001\002\000\006\004\ufffa\010\ufffa\001\002" +
    "\000\006\004\ufff9\010\ufff9\001\002\000\006\011\020\012" +
    "\021\001\002\000\006\002\ufffb\005\ufffb\001\002\000\006" +
    "\004\ufff8\010\ufff8\001\002\000\006\004\ufff7\010\ufff7\001" +
    "\002\000\006\006\005\011\010\001\002\000\004\002\000" +
    "\001\002\000\006\002\uffff\005\uffff\001\002\000\006\002" +
    "\ufffd\005\ufffd\001\002" });

  /** Access to parse-action table. */
  public short[][] action_table() {return _action_table;}

  /** <code>reduce_goto</code> table. */
  protected static final short[][] _reduce_table = 
    unpackFromStrings(new String[] {
    "\000\023\000\010\004\006\005\003\006\005\001\001\000" +
    "\002\001\001\000\004\004\024\001\001\000\002\001\001" +
    "\000\002\001\001\000\004\003\011\001\001\000\004\002" +
    "\012\001\001\000\002\001\001\000\002\001\001\000\002" +
    "\001\001\000\002\001\001\000\002\001\001\000\002\001" +
    "\001\000\002\001\001\000\002\001\001\000\006\004\006" +
    "\005\023\001\001\000\002\001\001\000\002\001\001\000" +
    "\002\001\001" });

  /** Access to <code>reduce_goto</code> table. */
  public short[][] reduce_table() {return _reduce_table;}

  /** Instance of action encapsulation class. */
  protected CUP$QueryParser$actions action_obj;

  /** Action encapsulation object initializer. */
  protected void init_actions()
    {
      action_obj = new CUP$QueryParser$actions(this);
    }

  /** Invoke a user supplied parse action. */
  public java_cup.runtime.Symbol do_action(
    int                        act_num,
    java_cup.runtime.lr_parser parser,
    java.util.Stack            stack,
    int                        top)
    throws java.lang.Exception
  {
    /* call code in generated class */
    return action_obj.CUP$QueryParser$do_action(act_num, parser, stack, top);
  }

  /** Indicates start state. */
  public int start_state() {return 0;}
  /** Indicates start production. */
  public int start_production() {return 1;}

  /** <code>EOF</code> Symbol index. */
  public int EOF_sym() {return 0;}

  /** <code>error</code> Symbol index. */
  public int error_sym() {return 1;}



	public QueryParser (java.io.Reader input) {
		super(new QueryScanner(input));
	}
	
	public static void main(String args[]) throws Exception {
		QueryParser p;
		p = new QueryParser(new FileReader("../CQA/lib/test.txt"));
		
		Query q = (Query) p.parse().value;
		System.out.println("Result: " + q);
	}

}

/** Cup generated class to encapsulate user supplied action code.*/
class CUP$QueryParser$actions {
  private final QueryParser parser;

  /** Constructor */
  CUP$QueryParser$actions(QueryParser parser) {
    this.parser = parser;
  }

  /** Method with the actual generated action code. */
  public final java_cup.runtime.Symbol CUP$QueryParser$do_action(
    int                        CUP$QueryParser$act_num,
    java_cup.runtime.lr_parser CUP$QueryParser$parser,
    java.util.Stack            CUP$QueryParser$stack,
    int                        CUP$QueryParser$top)
    throws java.lang.Exception
    {
      /* Symbol object for return from actions */
      java_cup.runtime.Symbol CUP$QueryParser$result;

      /* select the action based on the action number */
      switch (CUP$QueryParser$act_num)
        {
          /*. . . . . . . . . . . . . . . . . . . .*/
          case 10: // tuple ::= tuple COMMA VARIABLE 
            {
              Vector<Integer> RESULT =null;
		int tleft = ((java_cup.runtime.Symbol)CUP$QueryParser$stack.elementAt(CUP$QueryParser$top-2)).left;
		int tright = ((java_cup.runtime.Symbol)CUP$QueryParser$stack.elementAt(CUP$QueryParser$top-2)).right;
		Vector<Integer> t = (Vector<Integer>)((java_cup.runtime.Symbol) CUP$QueryParser$stack.elementAt(CUP$QueryParser$top-2)).value;
		int strleft = ((java_cup.runtime.Symbol)CUP$QueryParser$stack.peek()).left;
		int strright = ((java_cup.runtime.Symbol)CUP$QueryParser$stack.peek()).right;
		String str = (String)((java_cup.runtime.Symbol) CUP$QueryParser$stack.peek()).value;
		 t.add(SymTable.addSymbol(str, SymType.VARIABLE));RESULT=t; 
              CUP$QueryParser$result = parser.getSymbolFactory().newSymbol("tuple",0, ((java_cup.runtime.Symbol)CUP$QueryParser$stack.elementAt(CUP$QueryParser$top-2)), ((java_cup.runtime.Symbol)CUP$QueryParser$stack.peek()), RESULT);
            }
          return CUP$QueryParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 9: // tuple ::= tuple COMMA NONVAR 
            {
              Vector<Integer> RESULT =null;
		int tleft = ((java_cup.runtime.Symbol)CUP$QueryParser$stack.elementAt(CUP$QueryParser$top-2)).left;
		int tright = ((java_cup.runtime.Symbol)CUP$QueryParser$stack.elementAt(CUP$QueryParser$top-2)).right;
		Vector<Integer> t = (Vector<Integer>)((java_cup.runtime.Symbol) CUP$QueryParser$stack.elementAt(CUP$QueryParser$top-2)).value;
		int strleft = ((java_cup.runtime.Symbol)CUP$QueryParser$stack.peek()).left;
		int strright = ((java_cup.runtime.Symbol)CUP$QueryParser$stack.peek()).right;
		String str = (String)((java_cup.runtime.Symbol) CUP$QueryParser$stack.peek()).value;
		 t.add(SymTable.addSymbol(str, SymType.CONSTANT));RESULT=t; 
              CUP$QueryParser$result = parser.getSymbolFactory().newSymbol("tuple",0, ((java_cup.runtime.Symbol)CUP$QueryParser$stack.elementAt(CUP$QueryParser$top-2)), ((java_cup.runtime.Symbol)CUP$QueryParser$stack.peek()), RESULT);
            }
          return CUP$QueryParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 8: // tuple ::= VARIABLE 
            {
              Vector<Integer> RESULT =null;
		int strleft = ((java_cup.runtime.Symbol)CUP$QueryParser$stack.peek()).left;
		int strright = ((java_cup.runtime.Symbol)CUP$QueryParser$stack.peek()).right;
		String str = (String)((java_cup.runtime.Symbol) CUP$QueryParser$stack.peek()).value;
		 Vector<Integer> tup = new Vector<Integer>(); tup.add(SymTable.addSymbol(str, SymType.VARIABLE)); RESULT=tup; 
              CUP$QueryParser$result = parser.getSymbolFactory().newSymbol("tuple",0, ((java_cup.runtime.Symbol)CUP$QueryParser$stack.peek()), ((java_cup.runtime.Symbol)CUP$QueryParser$stack.peek()), RESULT);
            }
          return CUP$QueryParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 7: // tuple ::= NONVAR 
            {
              Vector<Integer> RESULT =null;
		int strleft = ((java_cup.runtime.Symbol)CUP$QueryParser$stack.peek()).left;
		int strright = ((java_cup.runtime.Symbol)CUP$QueryParser$stack.peek()).right;
		String str = (String)((java_cup.runtime.Symbol) CUP$QueryParser$stack.peek()).value;
		 Vector<Integer> tup = new Vector<Integer>(); tup.add(SymTable.addSymbol(str, SymType.CONSTANT)); RESULT=tup; 
              CUP$QueryParser$result = parser.getSymbolFactory().newSymbol("tuple",0, ((java_cup.runtime.Symbol)CUP$QueryParser$stack.peek()), ((java_cup.runtime.Symbol)CUP$QueryParser$stack.peek()), RESULT);
            }
          return CUP$QueryParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 6: // closedTuple ::= LPAREN tuple RPAREN 
            {
              Vector<Integer> RESULT =null;
		int tleft = ((java_cup.runtime.Symbol)CUP$QueryParser$stack.elementAt(CUP$QueryParser$top-1)).left;
		int tright = ((java_cup.runtime.Symbol)CUP$QueryParser$stack.elementAt(CUP$QueryParser$top-1)).right;
		Vector<Integer> t = (Vector<Integer>)((java_cup.runtime.Symbol) CUP$QueryParser$stack.elementAt(CUP$QueryParser$top-1)).value;
		 RESULT=t; 
              CUP$QueryParser$result = parser.getSymbolFactory().newSymbol("closedTuple",1, ((java_cup.runtime.Symbol)CUP$QueryParser$stack.elementAt(CUP$QueryParser$top-2)), ((java_cup.runtime.Symbol)CUP$QueryParser$stack.peek()), RESULT);
            }
          return CUP$QueryParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 5: // basic_literal ::= NONVAR closedTuple 
            {
              Literal RESULT =null;
		int predleft = ((java_cup.runtime.Symbol)CUP$QueryParser$stack.elementAt(CUP$QueryParser$top-1)).left;
		int predright = ((java_cup.runtime.Symbol)CUP$QueryParser$stack.elementAt(CUP$QueryParser$top-1)).right;
		String pred = (String)((java_cup.runtime.Symbol) CUP$QueryParser$stack.elementAt(CUP$QueryParser$top-1)).value;
		int tleft = ((java_cup.runtime.Symbol)CUP$QueryParser$stack.peek()).left;
		int tright = ((java_cup.runtime.Symbol)CUP$QueryParser$stack.peek()).right;
		Vector<Integer> t = (Vector<Integer>)((java_cup.runtime.Symbol) CUP$QueryParser$stack.peek()).value;
		 Literal l = new Literal(); l.setID(SymTable.addSymbol(pred, SymType.PREDICATE)); l.setNegative(false); l.setMultiParams(t); RESULT=l; 
              CUP$QueryParser$result = parser.getSymbolFactory().newSymbol("basic_literal",2, ((java_cup.runtime.Symbol)CUP$QueryParser$stack.elementAt(CUP$QueryParser$top-1)), ((java_cup.runtime.Symbol)CUP$QueryParser$stack.peek()), RESULT);
            }
          return CUP$QueryParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 4: // literal ::= NEG basic_literal 
            {
              Literal RESULT =null;
		int negLitleft = ((java_cup.runtime.Symbol)CUP$QueryParser$stack.peek()).left;
		int negLitright = ((java_cup.runtime.Symbol)CUP$QueryParser$stack.peek()).right;
		Literal negLit = (Literal)((java_cup.runtime.Symbol) CUP$QueryParser$stack.peek()).value;
		 negLit.setNegative(true); RESULT=negLit; 
              CUP$QueryParser$result = parser.getSymbolFactory().newSymbol("literal",3, ((java_cup.runtime.Symbol)CUP$QueryParser$stack.elementAt(CUP$QueryParser$top-1)), ((java_cup.runtime.Symbol)CUP$QueryParser$stack.peek()), RESULT);
            }
          return CUP$QueryParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 3: // literal ::= basic_literal 
            {
              Literal RESULT =null;
		int lleft = ((java_cup.runtime.Symbol)CUP$QueryParser$stack.peek()).left;
		int lright = ((java_cup.runtime.Symbol)CUP$QueryParser$stack.peek()).right;
		Literal l = (Literal)((java_cup.runtime.Symbol) CUP$QueryParser$stack.peek()).value;
		 RESULT=l; 
              CUP$QueryParser$result = parser.getSymbolFactory().newSymbol("literal",3, ((java_cup.runtime.Symbol)CUP$QueryParser$stack.peek()), ((java_cup.runtime.Symbol)CUP$QueryParser$stack.peek()), RESULT);
            }
          return CUP$QueryParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 2: // query ::= query AND literal 
            {
              Query RESULT =null;
		int qleft = ((java_cup.runtime.Symbol)CUP$QueryParser$stack.elementAt(CUP$QueryParser$top-2)).left;
		int qright = ((java_cup.runtime.Symbol)CUP$QueryParser$stack.elementAt(CUP$QueryParser$top-2)).right;
		Query q = (Query)((java_cup.runtime.Symbol) CUP$QueryParser$stack.elementAt(CUP$QueryParser$top-2)).value;
		int lleft = ((java_cup.runtime.Symbol)CUP$QueryParser$stack.peek()).left;
		int lright = ((java_cup.runtime.Symbol)CUP$QueryParser$stack.peek()).right;
		Literal l = (Literal)((java_cup.runtime.Symbol) CUP$QueryParser$stack.peek()).value;
		 q.add(l); RESULT=q; 
              CUP$QueryParser$result = parser.getSymbolFactory().newSymbol("query",4, ((java_cup.runtime.Symbol)CUP$QueryParser$stack.elementAt(CUP$QueryParser$top-2)), ((java_cup.runtime.Symbol)CUP$QueryParser$stack.peek()), RESULT);
            }
          return CUP$QueryParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 1: // $START ::= query EOF 
            {
              Object RESULT =null;
		int start_valleft = ((java_cup.runtime.Symbol)CUP$QueryParser$stack.elementAt(CUP$QueryParser$top-1)).left;
		int start_valright = ((java_cup.runtime.Symbol)CUP$QueryParser$stack.elementAt(CUP$QueryParser$top-1)).right;
		Query start_val = (Query)((java_cup.runtime.Symbol) CUP$QueryParser$stack.elementAt(CUP$QueryParser$top-1)).value;
		RESULT = start_val;
              CUP$QueryParser$result = parser.getSymbolFactory().newSymbol("$START",0, ((java_cup.runtime.Symbol)CUP$QueryParser$stack.elementAt(CUP$QueryParser$top-1)), ((java_cup.runtime.Symbol)CUP$QueryParser$stack.peek()), RESULT);
            }
          /* ACCEPT */
          CUP$QueryParser$parser.done_parsing();
          return CUP$QueryParser$result;

          /*. . . . . . . . . . . . . . . . . . . .*/
          case 0: // query ::= literal 
            {
              Query RESULT =null;
		int lleft = ((java_cup.runtime.Symbol)CUP$QueryParser$stack.peek()).left;
		int lright = ((java_cup.runtime.Symbol)CUP$QueryParser$stack.peek()).right;
		Literal l = (Literal)((java_cup.runtime.Symbol) CUP$QueryParser$stack.peek()).value;
		 Query q = new Query(); q.add(l); RESULT=q; 
              CUP$QueryParser$result = parser.getSymbolFactory().newSymbol("query",4, ((java_cup.runtime.Symbol)CUP$QueryParser$stack.peek()), ((java_cup.runtime.Symbol)CUP$QueryParser$stack.peek()), RESULT);
            }
          return CUP$QueryParser$result;

          /* . . . . . .*/
          default:
            throw new Exception(
               "Invalid action number found in internal parse table");

        }
    }
}

