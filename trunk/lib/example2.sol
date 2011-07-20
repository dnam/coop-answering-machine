cnf(clause1, top_clause, [p(X), s(X)]).
cnf(clause2, axiom, [q(X), -p(X)]).
cnf(clause3, axiom, [-s(Y)]).
cnf(clause4, axiom, [-p(Z), -q(Z), r(Z)]).
pf([POS] < 2).
