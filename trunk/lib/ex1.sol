cnf(c1, axiom, [ill(pete, sinusitis)]).
cnf(c2, axiom, [ill(mary, bronchitis)]).
cnf(c3, axiom, [-treat(pete, medi)]).
cnf(c4, axiom, [ill(X, sinusitis), treat(X, medi)]).
cnf(query_clause, top_clause, [-ill(X, sinusitis), ans(X)]).
pf([ans(_)]).
