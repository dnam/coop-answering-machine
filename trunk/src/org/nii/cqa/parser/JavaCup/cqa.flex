package org.nii.cqa.parser;

import java_cup.runtime.SymbolFactory;
%%
%cup
%class QueryScanner
%{
	public QueryScanner(java.io.InputStream r, SymbolFactory sf){
		this(r);
		this.sf=sf;
	}
	private SymbolFactory sf;
%}
%eofval{
    return sf.newSymbol("EOF",sym.EOF);
%eofval}

%%
"," { return sf.newSymbol("Comma",sym.COMMA); }
"&" { return sf.newSymbol("Conjuction",sym.AND); }
"-" { return sf.newSymbol("Negation",sym.NEG); }
"(" { return sf.newSymbol("Left Bracket",sym.LPAREN); }
")" { return sf.newSymbol("Right Bracket",sym.RPAREN); }
[A-Z][a-zA-Z0-9]* { return sf.newSymbol("Variable",sym.VARIABLE, new String(yytext())); }
[a-z][a-zA-Z0-9]* { return sf.newSymbol("Non-Variable",sym.NONVAR, new String(yytext())); }
[ \t\r\n\f] { /* ignore white space. */ }
. { System.err.println("Illegal character: "+yytext()); }
