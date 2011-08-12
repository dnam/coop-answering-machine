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
    return symbol(QuerySym.EOF);
%eofval}

%%
"," { return symbol(QuerySym.COMMA); }
"&" { return symbol(QuerySym.AND); }
"-" { return symbol(QuerySym.NEG); }
"(" { return symbol(QuerySym.LPAREN); }
")" { return symbol(QuerySym.RPAREN); }
[A-Z][a-zA-Z0-9]* { return symbol(QuerySym.VARIABLE, new String(yytext())); }
[a-z][a-zA-Z0-9]* { return symbol(QuerySym.NONVAR, new String(yytext())); }
[ \t\n\r\f] { /* ignore white space. */ }
. { System.err.println("Illegal character: "+yytext()); }
