# SymJava
SymJava is a Java library for symbolic mathematics.

SymJava is developed under Java 7 and Eclipse-Kepler (SR2 4.3.2, https://www.eclipse.org/downloads/packages/release/kepler/sr2)

Install java-oo Eclipse plugin for Java Operator Overloading support (https://github.com/amelentev/java-oo):
Click in menu: Help -> Install New Software. Enter in "Work with" field: 
http://amelentev.github.io/eclipse.jdt-oo-site/

-----------------------Examples---------------------------
See symjava.examples.*

Expr expr = x + y * z;
System.out.println(expr); //x + y*z

Expr expr2 = expr.subs(x, y*y);
System.out.println(expr2); //y^2 + y*z
System.out.println(expr2.diff(y)); //2*y + z

Func f = new Func("f1", expr2.diff(y));
System.out.println(f); //f1(y,z)

BytecodeFunc func = f.toBytecodeFunc();
System.out.println(func.apply(1,2)); //4.0

