package org.nii.cqa.parser;

import java_cup.runtime.Symbol;

%%
%cup
%class QueryScanner
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
    return symbol(sym.EOF);
%eofval}

%%
"," { return symbol(sym.COMMA); }
"&" { return symbol(sym.AND); }
"-" { return symbol(sym.NEG); }
"(" { return symbol(sym.LPAREN); }
")" { return symbol(sym.RPAREN); }
[A-Z][a-zA-Z0-9]* { return symbol(sym.VARIABLE, new String(yytext())); }
[a-z][a-zA-Z0-9]* { return symbol(sym.NONVAR, new String(yytext())); }
[ \t\r\n\f] { /* ignore white space. */ }
. { System.err.println("Illegal character: "+yytext()); }
