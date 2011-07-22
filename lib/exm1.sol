cnf(c1, axiom, [ill(pete, sinusitis)]).
cnf(c2, axiom, [ill(mary, bronchitis)]).
cnf(c3, axiom, [treat(pete, medi)]).
cnf(c4, axiom, [-ill(X, sinusitis), treat(X, medi)]).

cnf(query_clause, top_clause, [-ill(X, sinusitis), -treat(pete, Y), -ill(W, Z), ans(X,Y,W,Z)]).

%cnf(query2, top_clause, [-treat(X, medi), ans_2(X)]).

pf([ans(_,_,_,_)]).
%pf([ans_2(_)]).
