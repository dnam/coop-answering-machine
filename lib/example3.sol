cnf(c1, axiom, [pathway(X,Z), -reaction(X,Y), -pathway(Y,Z)]).
cnf(c2, axiom, [pathway(X,Y), -reaction(X,Y)]).
cnf(c3, axiom, [reaction(a,b), reaction(a,c)]).
cnf(c4, axiom, [reaction(b,d), reaction(c,d)]).
cnf(c5, axiom, [-reaction(c,b)]).
cnf(c6, top_clause, [-pathway(U,d), ans(U)]).
pf([-reaction(_,_), ans(_)]).
