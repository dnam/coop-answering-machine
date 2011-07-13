package org.nii.cqa.parser;

import java_cup.runtime.Symbol;

%%
%cup
%class KBScanner
%{	
	private Symbol symbol(int sym) {
    return new Symbol(sym, yyline+1, yycolumn+1);
  }
  
  private Symbol symbol(int sym, Object val) {
    return new Symbol(sym, yyline+1, yycolumn+1, val);
  }
  
  private void error(String message) {
    System.out.println("Error at line "+(yyline+1)+", column "+(yycolumn+1)+" : "+message);
  }
%}
%eofval{
    return symbol(KBSym.EOF);
%eofval}

%%
"," { return symbol(KBSym.COMMA); }
"&" { return symbol(KBSym.AND); }
"|" { return symbol(KBSym.OR); }
"-" { return symbol(KBSym.NEG); }
"(" { return symbol(KBSym.LPAREN); }
")" { return symbol(KBSym.RPAREN); }
"->" { return symbol(KBSym.IMPLY); }
[A-Z][a-zA-Z0-9]* { return symbol(KBSym.VARIABLE, new String(yytext())); }
[a-z][a-zA-Z0-9]* { return symbol(KBSym.NONVAR, new String(yytext())); }
[ \t\n\r\f] { /* ignore white space. */ }
. { System.err.println("Illegal character: "+yytext()); }
