cnf(c1, axiom, [ill(pete, sinusitis)]).
cnf(c2, axiom, [ill(mary, sinusitis)]).
cnf(c3, axiom, [-treat(pete, medi)]).
cnf(c4, axiom, [ill(X, sinusitis), treat(X, medi)]).
%cnf(query_clause, top_clause, [-ill(Y, sinusitis),-ill(Y, bronchitis), ans(Y)]).
cnf(query_clause, top_clause, [--ill(Y, sinusitis), ans(Y)]).
pf([ans(_)]).
