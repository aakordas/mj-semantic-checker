import visitor.GJDepthFirst;
import java.util.Enumeration;

import syntaxtree.*;

import java.util.LinkedHashMap;

public class PopulatingVisitor extends GJDepthFirst<String, String> {
    public static LinkedHashMap<String, ClassClass> classes;

    // Having these two does not feel so elegant, but it gets the job done...
    private String currentClass;	// It holds the name of the current class.
    private String currentMethod;	// It holds the name of the current method.

    public ClassClass getClass(String name) {
	return classes.get(name);
    }

    public PopulatingVisitor() {
	this.classes = new LinkedHashMap<String ,ClassClass>();
	this.currentClass = null;
	this.currentMethod = null;
    }

    //
    // Auto class visitors--probably don't need to be overridden.
    //
    public String visit(NodeList n, String argu) throws Exception {
	if (n.size() == 1)
	    return n.elementAt(0).accept(this,argu);
	String _ret=null;
	int _count=0;
	for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
	    e.nextElement().accept(this,argu);
	    _count++;
	}
	return _ret;
    }

    public String visit(NodeListOptional n, String argu) throws Exception {
	if ( n.present() ) {
	    if (n.size() == 1)
		return n.elementAt(0).accept(this,argu);
	    String _ret=null;
	    int _count=0;
	    for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
		e.nextElement().accept(this,argu);
		_count++;
	    }
	    return _ret;
	}
	else
	    return null;
    }

    public String visit(NodeOptional n, String argu) throws Exception {
	if ( n.present() )
	    return n.node.accept(this,argu);
	else
	    return null;
    }

    public String visit(NodeSequence n, String argu) throws Exception {
	if (n.size() == 1)
	    return n.elementAt(0).accept(this,argu);
	String _ret=null;
	int _count=0;
	for ( Enumeration<Node> e = n.elements(); e.hasMoreElements(); ) {
	    e.nextElement().accept(this,argu);
	    _count++;
	}
	return _ret;
    }

    public String visit(NodeToken n, String argu) throws Exception { return null; }

    //
    // User-generated visitor methods below
    //

    /**
     * f0 -> MainClass()
     * f1 -> ( TypeDeclaration() )*
     * f2 -> <EOF>
     */
    public String visit(Goal n, String argu) throws Exception {
	n.f0.accept(this, null);

	if (n.f1.present()) {
	    n.f1.accept(this, null);
	}

	return null;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> "public"
     * f4 -> "static"
     * f5 -> "void"
     * f6 -> "main"
     * f7 -> "("
     * f8 -> "String"
     * f9 -> "["
     * f10 -> "]"
     * f11 -> Identifier()
     * f12 -> ")"
     * f13 -> "{"
     * f14 -> ( VarDeclaration() )*
     * f15 -> ( Statement() )*
     * f16 -> "}"
     * f17 -> "}"
     */
    public String visit(MainClass n, String argu) throws Exception {
	currentClass = n.f1.accept(this, null);

	// If, in some magic way, the key already exists
	if (classes.put(currentClass, new ClassClass(true, currentClass)) != null) {
	    throw new Exception("Class " + currentClass + " already exists, somehow...");
	}

	getClass(currentClass).setFields(null);
	currentMethod = "main";
	getClass(currentClass).addMethod(new ClassMethod("void", currentMethod));

	String strId = n.f11.accept(this, null);
	if (getClass(currentClass).addArgument(currentMethod, "String[]", strId) != null) {
	    throw new Exception("Argument " + strId + " wot");
	}

	// I’m using the argument to indicate that I’m coming to get method information and not
	// class information, for example.
	if (n.f14.present()) {
	    n.f14.accept(this, "method");
	}

	return null;
    }

    /**
     * f0 -> ClassDeclaration()
     *       | ClassExtendsDeclaration()
     */
    public String visit(TypeDeclaration n, String argu) throws Exception {
	n.f0.accept(this, null);
	return null;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "{"
     * f3 -> ( VarDeclaration() )*
     * f4 -> ( MethodDeclaration() )*
     * f5 -> "}"
     */
    public String visit(ClassDeclaration n, String argu) throws Exception {
	currentClass = n.f1.accept(this, argu);

	if (classes.put(currentClass, new ClassClass(currentClass)) != null) {
	    throw new Exception("Class " + currentClass + " already exists.");
	}

	if (n.f3.present()) {
	    n.f3.accept(this, "class");
	}

	if (n.f4.present()) {
	    n.f4.accept(this, currentClass);
	}

	return null;
    }

    /**
     * f0 -> "class"
     * f1 -> Identifier()
     * f2 -> "extends"
     * f3 -> Identifier()
     * f4 -> "{"
     * f5 -> ( VarDeclaration() )*
     * f6 -> ( MethodDeclaration() )*
     * f7 -> "}"
     */
    public String visit(ClassExtendsDeclaration n, String argu) throws Exception {
	currentClass = n.f1.accept(this, argu);
	if (classes.
	    put(currentClass, new ClassClass(n.f3.accept(this, null), currentClass)) != null) {
	    throw new Exception("Class " + currentClass + " already exists.");
	}

	if (n.f5.present()) {
	    n.f5.accept(this, "class");
	}

	if (n.f6.present()) {
	    n.f6.accept(this, currentClass);
	}

	return null;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */
    public String visit(VarDeclaration n, String argu) throws Exception {
	String type = n.f0.accept(this, null);
	String id = n.f1.accept(this, null);

	// If it got called from a class definition
	if (argu.equals("class")) {
	    // Add the field in the correct class.
	    if (getClass(currentClass).addField(type, id) != null) {
		throw new Exception("Field " + id + " in class " + currentClass +
				    " has already been declared.");
	    }
	} else if (argu.equals("method")) { // If it got called from a method definition.
	    // Add the local variables in the correct method.
	    if (getClass(currentClass).addLocalVariable(currentMethod, type, id) != null) {
		throw new Exception("Local variable " + id + " in method " + currentMethod +
				    " has already been declared.");
	    }
	}

	return null;
    }

    /**
     * f0 -> "public"
     * f1 -> Type()
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( FormalParameterList() )?
     * f5 -> ")"
     * f6 -> "{"
     * f7 -> ( VarDeclaration() )*
     * f8 -> ( Statement() )*
     * f9 -> "return"
     * f10 -> Expression()
     * f11 -> ";"
     * f12 -> "}"
     */
    public String visit(MethodDeclaration n, String argu) throws Exception {
	currentMethod = n.f2.accept(this, null);

	if (getClass(currentClass).
	    addMethod(new ClassMethod(n.f1.accept(this, null), currentMethod)) != null) {
	    throw new Exception("Method " + currentMethod + " in class " + currentClass +
				" has already been defined.");
	}


	if (n.f4.present()) {
	    n.f4.accept(this, null);
	}

	if (n.f7.present()) {
	    n.f7.accept(this, "method");
	}

	if (n.f8.present()) {
	    n.f8.accept(this, null);
	}

	String parentClass = getClass(currentClass).getExtend();
	ClassMethod parent = null;
	ClassMethod child = null;
	boolean poly;

	if (parentClass != null) {
	    parent = getClass(parentClass).getMethod(currentMethod);
	    child = getClass(currentClass).getMethod(currentMethod);
	}

	if (parent != null) {
	    poly = parent.polymorphicEquals(child);
	    if (!poly) {
		throw new Exception("Method " + currentMethod + ", in class " + currentClass +
				    " has already been defined in its parent class.");
	    }
	}

	return null;
    }

    /**
     * f0 -> FormalParameter()
     * f1 -> FormalParameterTail()
     */
    public String visit(FormalParameterList n, String argu) throws Exception {
	n.f0.accept(this, null);
	n.f1.accept(this, null);

	return null;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     */
    public String visit(FormalParameter n, String argu) throws Exception {
	String type = n.f0.accept(this, null);
	String id = n.f1.accept(this, null);

	if (getClass(currentClass).addArgument(currentMethod, type, id) != null) {
	    throw new Exception("Argument " + id + " in method " + currentMethod +
				" has already been defined.");
	}

	return null;
    }

    /**
     * f0 -> ( FormalParameterTerm() )*
     */
    public String visit(FormalParameterTail n, String argu) throws Exception {
	if (n.f0.present()) {
	    n.f0.accept(this, null);
	}
	return null;
    }

    /**
     * f0 -> ","
     * f1 -> FormalParameter()
     */
    public String visit(FormalParameterTerm n, String argu) throws Exception {
	n.f1.accept(this, null);
	return null;
    }

    /**
     * f0 -> ArrayType()
     *       | BooleanType()
     *       | IntegerType()
     *       | Identifier()
     */
    public String visit(Type n, String argu) throws Exception {
	return n.f0.accept(this, null);
    }

    /**
     * f0 -> "int"
     * f1 -> "["
     * f2 -> "]"
     */
    public String visit(ArrayType n, String argu) throws Exception {
	return "int[]";
    }

    /**
     * f0 -> "boolean"
     */
    public String visit(BooleanType n, String argu) throws Exception {
	return "boolean";
    }

    /**
     * f0 -> "int"
     */
    public String visit(IntegerType n, String argu) throws Exception {
	return "int";
    }

    /**
     * f0 -> Block()
     *       | AssignmentStatement()
     *       | ArrayAssignmentStatement()
     *       | IfStatement()
     *       | WhileStatement()
     *       | PrintStatement()
     */
    public String visit(Statement n, String argu) throws Exception {
	n.f0.accept(this, null);
	return null;
    }

    /**
     * f0 -> "{"
     * f1 -> ( Statement() )*
     * f2 -> "}"
     */
    public String visit(Block n, String argu) throws Exception {
	if (n.f1.present()) {
	    n.f1.accept(this, null);
	}

	return null;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "="
     * f2 -> Expression()
     * f3 -> ";"
     */
    public String visit(AssignmentStatement n, String argu) throws Exception {
	return null;
    }

    /**
     * f0 -> Identifier()
     * f1 -> "["
     * f2 -> Expression()
     * f3 -> "]"
     * f4 -> "="
     * f5 -> Expression()
     * f6 -> ";"
     */
    public String visit(ArrayAssignmentStatement n, String argu) throws Exception {
	return null;
    }

    /**
     * f0 -> "if"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     * f5 -> "else"
     * f6 -> Statement()
     */
    public String visit(IfStatement n, String argu) throws Exception {
	return null;
    }

    /**
     * f0 -> "while"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     */
    public String visit(WhileStatement n, String argu) throws Exception {
	return null;
    }

    /**
     * f0 -> "System.out.println"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> ";"
     */
    public String visit(PrintStatement n, String argu) throws Exception {
	return null;
    }

    /**
     * f0 -> AndExpression()
     *       | CompareExpression()
     *       | PlusExpression()
     *       | MinusExpression()
     *       | TimesExpression()
     *       | ArrayLookup()
     *       | ArrayLength()
     *       | MessageSend()
     *       | Clause()
     */
    public String visit(Expression n, String argu) throws Exception {
	return n.f0.accept(this, null);
    }

    /**
     * f0 -> Clause()
     * f1 -> "&&"
     * f2 -> Clause()
     */
    public String visit(AndExpression n, String argu) throws Exception {
	return null;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "<"
     * f2 -> PrimaryExpression()
     */
    public String visit(CompareExpression n, String argu) throws Exception {
	return null;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "+"
     * f2 -> PrimaryExpression()
     */
    public String visit(PlusExpression n, String argu) throws Exception {
	return null;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "-"
     * f2 -> PrimaryExpression()
     */
    public String visit(MinusExpression n, String argu) throws Exception {
	return null;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "*"
     * f2 -> PrimaryExpression()
     */
    public String visit(TimesExpression n, String argu) throws Exception {
	return null;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "["
     * f2 -> PrimaryExpression()
     * f3 -> "]"
     */
    public String visit(ArrayLookup n, String argu) throws Exception {
	return null;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> "length"
     */
    public String visit(ArrayLength n, String argu) throws Exception {
	return null;
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( ExpressionList() )?
     * f5 -> ")"
     */
    public String visit(MessageSend n, String argu) throws Exception {
	return null;
    }

    /**
     * f0 -> Expression()
     * f1 -> ExpressionTail()
     */
    public String visit(ExpressionList n, String argu) throws Exception {
	n.f0.accept(this, null);
	n.f1.accept(this, null);
	return null;
    }

    /**
     * f0 -> ( ExpressionTerm() )*
     */
    public String visit(ExpressionTail n, String argu) throws Exception {
	if (n.f0.present()) {
	    n.f0.accept(this, null);
	}

	return null;
    }

    /**
     * f0 -> ","
     * f1 -> Expression()
     */
    public String visit(ExpressionTerm n, String argu) throws Exception {
	return n.f1.accept(this, null);
    }

    /**
     * f0 -> NotExpression()
     *       | PrimaryExpression()
     */
    public String visit(Clause n, String argu) throws Exception {
	return n.f0.accept(this, null);
    }

    /**
     * f0 -> IntegerLiteral()
     *       | TrueLiteral()
     *       | FalseLiteral()
     *       | Identifier()
     *       | ThisExpression()
     *       | ArrayAllocationExpression()
     *       | AllocationExpression()
     *       | BracketExpression()
     */
    public String visit(PrimaryExpression n, String argu) throws Exception {
	return n.f0.accept(this, null);
    }

    /**
     * f0 -> <INTEGER_LITERAL>
     */
    public String visit(IntegerLiteral n, String argu) throws Exception {
	return "int";
    }

    /**
     * f0 -> "true"
     */
    public String visit(TrueLiteral n, String argu) throws Exception {
	return "boolean";
    }

    /**
     * f0 -> "false"
     */
    public String visit(FalseLiteral n, String argu) throws Exception {
	return "boolean";
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    public String visit(Identifier n, String argu) throws Exception {
	return n.f0.toString();
    }

    /**
     * f0 -> "this"
     */
    public String visit(ThisExpression n, String argu) throws Exception {
	return currentClass;
    }

    /**
     * f0 -> "new"
     * f1 -> "int"
     * f2 -> "["
     * f3 -> Expression()
     * f4 -> "]"
     */
    public String visit(ArrayAllocationExpression n, String argu) throws Exception {
	n.f3.accept(this, null);
	return null;
    }

    /**
     * f0 -> "new"
     * f1 -> Identifier()
     * f2 -> "("
     * f3 -> ")"
     */
    public String visit(AllocationExpression n, String argu) throws Exception {
	n.f1.accept(this, null);
	return null;
    }

    /**
     * f0 -> "!"
     * f1 -> Clause()
     */
    public String visit(NotExpression n, String argu) throws Exception {
	n.f1.accept(this, null);
	return null;
    }

    /**
     * f0 -> "("
     * f1 -> Expression()
     * f2 -> ")"
     */
    public String visit(BracketExpression n, String argu) throws Exception {
	return n.f1.accept(this, null);
    }
}
