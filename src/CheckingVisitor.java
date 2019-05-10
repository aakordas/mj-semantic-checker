import visitor.GJDepthFirst;
import java.util.Enumeration;

import syntaxtree.*;

import java.util.Iterator;

public class CheckingVisitor extends GJDepthFirst<String, String[]> {
    private String currentClass;
    private String currentMethod;

    // I shouldn’t, but...
    private Iterator<String> firstArgumentTypeIterator;

    public ClassClass getClass(String name) {
	return PopulatingVisitor.classes.get(name);
    }

    public String getVariableType(String currentClass, String currentMethod, String variable) {
	return this.getClass(currentClass).getVariableType(currentMethod, variable);
    }

    public boolean isPrimitiveType(String name) {
	return (name.equals("int") || name.equals("boolean") || name.equals("int[]"));
    }

    public CheckingVisitor() {
	this.currentClass = null;
	this.currentMethod = null;
	this.firstArgumentTypeIterator = null;
    }

    //
    // Auto class visitors--probably don't need to be overridden.
    //
    public String visit(NodeList n, String[] argu) throws Exception {
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

    public String visit(NodeListOptional n, String[] argu) throws Exception {
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

    public String visit(NodeOptional n, String[] argu) throws Exception {
	if ( n.present() )
	    return n.node.accept(this,argu);
	else
	    return null;
    }

    public String visit(NodeSequence n, String[] argu) throws Exception {
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

    public String visit(NodeToken n, String[] argu) throws Exception { return null; }

    //
    // User-generated visitor methods below
    //

    /**
     * f0 -> MainClass()
     * f1 -> (TypeDeclaration() )*
     * f2 -> <EOF>
     */
    public String visit(Goal n, String[] argu) throws Exception {
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
    public String visit(MainClass n, String[] argu) throws Exception {
	currentClass = n.f1.accept(this, null);
	currentMethod = "main";

	n.f15.accept(this, null);

	return null;
    }

    /**
     * f0 -> ClassDeclaration()
     *       | ClassExtendsDeclaration()
     */
    public String visit(TypeDeclaration n, String[] argu) throws Exception {
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
    public String visit(ClassDeclaration n, String[] argu) throws Exception {
	currentClass = n.f1.accept(this, null);

	n.f4.accept(this, null);
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
    public String visit(ClassExtendsDeclaration n, String[] argu) throws Exception {
	currentClass = n.f1.accept(this, null);

	n.f6.accept(this, null);

	return null;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     * f2 -> ";"
     */
    public String visit(VarDeclaration n, String[] argu) throws Exception {
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
    public String visit(MethodDeclaration n, String[] argu) throws Exception {
	currentMethod = n.f2.accept(this, null);

	if (n.f8.present()) {
	    n.f8.accept(this, null);
	}

	String expType = n.f10.accept(this, null);

	String returnType = n.f1.accept(this, null);

	/* Checks if the expression’s type is the same as the method’s return type or, in case the
	 * expression returned an identifier, checks if its type is the same as the method’s return
	 * type. */
	if (!expType.equals(returnType) &&
	    !getVariableType(currentClass, currentMethod, expType).equals(returnType)) {
	    throw new Exception("Wrong return type in method " + currentMethod);
	}

	return null;
    }

    /**
     * f0 -> FormalParameter()
     * f1 -> FormalParameterTail()
     */
    public String visit(FormalParameterList n, String[] argu) throws Exception {
	n.f0.accept(this, null);
	n.f1.accept(this, null);

	return null;
    }

    /**
     * f0 -> Type()
     * f1 -> Identifier()
     */
    public String visit(FormalParameter n, String[] argu) throws Exception {
	return null;
    }

    /**
     * f0 -> ( FormalParameterTerm() )*
     */
    public String visit(FormalParameterTail n, String[] argu) throws Exception {
	return null;
    }

    /**
     * f0 -> ","
     * f1 -> FormalParameter()
     */
    public String visit(FormalParameterTerm n, String[] argu) throws Exception {
	return null;
    }

    /**
     * f0 -> ArrayType()
     *       | BooleanType()
     *       | IntegerType()
     *       | Identifier()
     */
    public String visit(Type n, String[] argu) throws Exception {
	return n.f0.accept(this, null);
    }

    /**
     * f0 -> "int"
     * f1 -> ""["
     * f2 -> "]"
     */
    public String visit(ArrayType n, String[] argu) throws Exception {
	return "int[]";
    }

    /**
     * f0 -> "boolean"
     */
    public String visit(BooleanType n, String[] argu) throws Exception {
	return "boolean";
    }

    /**
     * f0 -> "int"
     */
    public String visit(IntegerType n, String[] argu) throws Exception {
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
    public String visit(Statement n, String[] argu) throws Exception {
	n.f0.accept(this, null);
	return null;
    }

    /**
     * f0 -> "{"
     * f1 -> ( Statement() )*
     * f2 -> "}"
     */
    public String visit(Block n, String[] argu) throws Exception {
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
    public String visit(AssignmentStatement n, String[] argu) throws Exception {
	String lSide = n.f0.accept(this, null);
	String id = getVariableType(currentClass, currentMethod, lSide);

	if (id == null) {
	    throw new Exception("Identifier " + lSide + " in method " + currentMethod +
				" has not been defined");
	}

	String exp = n.f2.accept(this, null);
	String expType = getVariableType(currentClass, currentMethod, exp);

	String parent;

	/* Checks if the type of the expression is the same as the left side’s or, in case the
	 * expression returned an identifier, checks if the type of the identifier, from this scope,
	 * is the same. */
	if (!id.equals(exp) && !id.equals(expType)) {
	    parent = getClass(expType).getExtend();

	    if (parent == null || !id.equals(parent)) {
		throw new Exception("Invalid assignment to " + lSide + " in method " + currentMethod);
	    }
	}

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
    public String visit(ArrayAssignmentStatement n, String[] argu) throws Exception {
	String arrayId = n.f0.accept(this, null);
	String idType = getVariableType(currentClass, currentMethod, arrayId);

	if (idType == null) {
	    throw new Exception("Identifier " + arrayId + " is not reachable in method " +
				currentMethod);
	} else if (!idType.equals("int[]")) {
	    throw new Exception("Identifier " + arrayId + " is not of type int[], in method "  +
				currentMethod);
	}

	if (!n.f2.accept(this, null).equals("int") &&
	    !getVariableType(currentClass, currentMethod, n.f2.accept(this, null)).equals("int")) {
	    throw new Exception("Not valid index type for " + arrayId + " in method " +
				currentMethod);
	}

	if (!n.f5.accept(this, null).equals("int") &&
	    !getVariableType(currentClass, currentMethod, n.f5.accept(this, null)).equals("int")) {
	    throw new Exception("Invalid assignment to " + arrayId + " in method " + currentMethod);
	}

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
    public String visit(IfStatement n, String[] argu) throws Exception {
	String expression = n.f2.accept(this, null);

	if (!expression.equals("boolean") &&
	    !getVariableType(currentClass, currentMethod, expression).equals("boolean")) {
	    throw new Exception("Non boolean expression in if, in method " + currentMethod);
	}

	n.f4.accept(this, null);
	n.f6.accept(this, null);

	return null;
    }

    /**
     * f0 -> "while"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> Statement()
     */
    public String visit(WhileStatement n, String[] argu) throws Exception {
	String expression = n.f2.accept(this, null);

	if (!expression.equals("boolean") &&
	    !getVariableType(currentClass, currentMethod, expression).equals("boolean")) {
	    throw new Exception("Non boolean expression in while, in method " + currentMethod);
	}

	n.f4.accept(this, null);

	return null;
    }

    /**
     * f0 -> "System.out.println"
     * f1 -> "("
     * f2 -> Expression()
     * f3 -> ")"
     * f4 -> ";"
     */
    public String visit(PrintStatement n, String[] argu) throws Exception {
	String expression = n.f2.accept(this, null);
	if ((!expression.equals("int") && !expression.equals("boolean")) &&
	    (!getVariableType(currentClass, currentMethod, expression).equals("int") &&
	     !getVariableType(currentClass, currentMethod, expression).equals("boolean"))) {
	    throw new Exception("Cannot print the expression in method " + currentMethod);
	}

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
    public String visit(Expression n, String[] argu) throws Exception {
	return n.f0.accept(this, null);
    }

    /**
     * f0 -> Clause()
     * f1 -> "&&"
     * f2 -> Clause()
     */
    public String visit(AndExpression n, String[] argu) throws Exception {
	if (!n.f0.accept(this, null).equals("boolean")) {
	    throw new Exception("Boolean operation without boolean operands, in method " +
				currentMethod);
	}

	if (!n.f2.accept(this, null).equals("boolean")) {
	    throw new Exception("Boolean operation without boolean operands, in method " +
				currentMethod);
	}

	return "boolean";
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "<"
     * f2 -> PrimaryExpression()
     */
    public String visit(CompareExpression n, String[] argu) throws Exception {
	String operand1 = n.f0.accept(this, null);

	if (!operand1.equals("int") &&
	    !getVariableType(currentClass, currentMethod, operand1).equals("int")) {
	    throw new Exception("Trying to compare non-int elements, in method " + currentMethod);
	}

	String operand2 = n.f2.accept(this, null);

	if (!operand2.equals("int") &&
	    !getVariableType(currentClass, currentMethod, operand2).equals("int")) {
	    throw new Exception("Trying to compare non-int elements, in method " + currentMethod);
	}

	return "boolean";
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "+"
     * f2 -> PrimaryExpression()
     */
    public String visit(PlusExpression n, String[] argu) throws Exception {
	String operand1 = n.f0.accept(this, null);
	if (!operand1.equals("int") &&
	    !getVariableType(currentClass, currentMethod, operand1).equals("int")) {
	    throw new Exception("Trying to add non-int elements, in method " + currentMethod);
	}

	String operand2 = n.f2.accept(this, null);
	if (!operand2.equals("int") &&
	    !getVariableType(currentClass, currentMethod, operand2).equals("int")) {
	    throw new Exception("Trying to add non-int elements, in method " + currentMethod);
	}

	return "int";
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "-"
     * f2 -> PrimaryExpression()
     */
    public String visit(MinusExpression n, String[] argu) throws Exception {
	String operand1 = n.f0.accept(this, null);
	if (!operand1.equals("int") &&
	    !getVariableType(currentClass, currentMethod, operand1).equals("int")) {
	    throw new Exception("Trying to subtract non-int elements, in method " + currentMethod);
	}

	String operand2 = n.f2.accept(this, null);

	if (!operand2.equals("int") &&
	    !getVariableType(currentClass, currentMethod, operand2).equals("int")) {
	    throw new Exception("Trying to subtract non-int elements, in method " + currentMethod);
	}

	return "int";
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "*"
     * f2 -> PrimaryExpression()
     */
    public String visit(TimesExpression n, String[] argu) throws Exception {
	String operand1 = n.f0.accept(this, null);

	if (!operand1.equals("int") &&
	    !getVariableType(currentClass, currentMethod, operand1).equals("int")) {
	    throw new Exception("Trying to multiply non-int elements, in method " + currentMethod);
	}

	String operand2 = n.f2.accept(this, null);
	if (!operand2.equals("int") &&
	    !getVariableType(currentClass, currentMethod, operand2).equals("int")) {
	    throw new Exception("Trying to multiply non-int elements, in method " + currentMethod);
	}

	return "int";
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "["
     * f2 -> PrimaryExpression()
     * f3 -> "]"
     */
    public String visit(ArrayLookup n, String[] argu) throws Exception {
	String index = n.f2.accept(this, null);

	if (!isPrimitiveType(index)) {
	    String inType = getVariableType(currentClass, currentMethod, index);

	    // Check if the index identifier exists and is of the correct type.
	    if (inType == null) {
		throw new Exception("Identifier " + index + " in method " + currentMethod +
				    " has not been declared.");
	    }

	    if (!inType.equals("int")) {
		throw new Exception("Identifier " + index + " in method " + currentMethod +
				    " is not an int.");
	    }
	} else if (!index.equals("int")) {
	    throw new Exception("Identifier " + index + " in method " + currentMethod +
				" is not an int.");
	}

	String arrayId = n.f0.accept(this, null);
	String outType = getVariableType(currentClass, currentMethod, arrayId);

	// Check if the table identifier exists and is of the correct type.
	if (outType == null) {
	    throw new Exception("Identifier " + arrayId + " in method " + currentMethod +
				" has not been declared.");
	} else if (!outType.equals("int[]")) {
	    throw new Exception("Identifier " + arrayId + " in method " + currentMethod +
				" is not an int[]");
	}

	return "int";
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> "length"
     */
    public String visit(ArrayLength n, String[] argu) throws Exception {
	String arrayId = n.f0.accept(this, null);
	String type = getVariableType(currentClass, currentMethod, arrayId);

	if (type == null) {
	    throw new Exception("Identifier " + arrayId + " in method " + currentMethod +
				" has not been declared");
	} else if (!type.equals("int[]")) {
	    throw new Exception("Identifier " + arrayId + " in method " + currentMethod +
				" is not int[]");
	}

	return "int";
    }

    /**
     * f0 -> PrimaryExpression()
     * f1 -> "."
     * f2 -> Identifier()
     * f3 -> "("
     * f4 -> ( ExpressionList() )?
     * f5 -> ")"
     */
    public String visit(MessageSend n, String[] argu) throws Exception {
	String pe = n.f0.accept(this, null); // pe = PrimaryExpression

	ClassClass cClass = getClass(pe);
	String id = null;

	// Check if the primary expression is a class or an object of one. i.e. if the identifier is
        // the name of a class or ‘this’
	if (cClass == null) {
	    id = getVariableType(currentClass, currentMethod, pe);

	    // Since it’s not a variable in the current class, it has to be in the parent class.
	    if (id.equals("null")) {
		id = getClass(currentClass).getExtend();

		if (id == null) {
		    throw new Exception("Error in method " + currentMethod + ". Class " +
					currentClass + " does not extend another class. Identifier "
					+ pe + " has no matching declaration.");
		}

		id = getVariableType(id, currentMethod, pe);
	    }

	    if (id.equals("null")) {
		throw new Exception("Identifier " + id + " is not a defined type, in method " +
				    currentMethod);
	    }

	    cClass = getClass(id);
	}

	String messenger = n.f2.accept(this, null);
	ClassMethod cMethod = cClass.getMethod(messenger);

	if (cMethod == null) {
	    if (cClass.getExtend() == null) {
		throw new Exception(messenger + " is not a defined method in class " + pe);
	    }

	    cMethod = getClass(cClass.getExtend()).getMethod(messenger);
	}

	if (cMethod == null) {
	    throw new Exception(messenger + " is not a defined method in class " + pe);
	}

	int argc = cClass.getNumOfArguments(cMethod.getName());

	if (!n.f4.present()) {
	    // The method is called with no arguments, while it should have some.
	    if (argc != 0) {
		throw new Exception("Method " + messenger + " is used without arguments in method "
				    + currentMethod);
	    }
	} else if (n.f4.present()) { // Otherwise, let’s check what is up with all those arguments.
	    argu = new String[]{cClass.getName(), cMethod.getName(), String.valueOf(argc)};
	    n.f4.accept(this, argu);
	}

	return cMethod.getType();
    }

    /**
     * f0 -> Expression()
     * f1 -> ExpressionTail()
     */
    public String visit(ExpressionList n, String[] argu) throws Exception {
	// Get the types of all the arguments of the function.
	// They should be in order, since LinkedHashMap maintains the order specified in its
	// construction.
	firstArgumentTypeIterator = getClass(argu[0]).getMethod(argu[1]).getArguments().values()
	    .iterator();

	String firstArgumentType = null;
	if (firstArgumentTypeIterator.hasNext()) {
	    firstArgumentType = firstArgumentTypeIterator.next();
	} else {		// No values were fetched, somehow?
	    throw new Exception("eehhh?");
	}

	String firstArgument = n.f0.accept(this, null);

	if (!isPrimitiveType(firstArgument) &&
	    !isPrimitiveType(getVariableType(currentClass, currentMethod, firstArgument))) {
	    String cClass = getVariableType(currentClass, currentMethod, firstArgument);
	    String pClass;

	    if (!cClass.equals("null")) {
		pClass = getClass(cClass).getExtend();
	    } else {
		pClass = getClass(firstArgument).getExtend();
	    }

	    if (pClass != null) {
		if (!firstArgumentType.equals(pClass)) {
		    throw new Exception("In method " + currentMethod + ", member-method " + argu[1]
					+ " is called with wrong argument types.");
		}
	    }
	} else {
	    // Checks if the type of the first argument is correct.
	    if (!firstArgumentType.equals(firstArgument) &&
		!firstArgumentType.
		equals(getVariableType(currentClass, currentMethod, firstArgument))) {
		throw new Exception("In method " + currentMethod + ", member-method " + argu[1] +
				    " is called with wrong argument types.");
	    }
	}

	n.f1.accept(this, argu);

	return null;
    }

    /**
     * f0 -> ( ExpressionTerm() )*
     */
    public String visit(ExpressionTail n, String[] argu) throws Exception {
	if (n.f0.size() + 1 != Integer.parseInt(argu[2])) {
	    throw new Exception("In method  " + currentMethod + ", method " + argu[1] +
				" is called with incorrect number of arguments.");
	}

	if (n.f0.present()) {
	    n.f0.accept(this, argu);
	}

	return null;
    }

    /**
     * f0 -> ","
     * f1 -> Expression()
     */
    public String visit(ExpressionTerm n, String[] argu) throws Exception {
	String argument = n.f1.accept(this, null);

	if (firstArgumentTypeIterator.hasNext()) {
	    String firstArgumentType = firstArgumentTypeIterator.next();

	    if (!firstArgumentType.equals(argument) &&
		!firstArgumentType.equals(getVariableType(currentClass, currentMethod, argument))) {
		throw new Exception("In method " + currentMethod + ", member-method " + argu[1] +
				    " is called with wrong argument types.");
	    }
	}

	return n.f1.accept(this, null);
    }

    /**
     * f0 -> NotExpression()
     *       | PrimaryExpression()
     */
    public String visit(Clause n, String[] argu) throws Exception {
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
    public String visist(PrimaryExpression n, String[] argu) throws Exception {
	return n.f0.accept(this, null);
    }

    /**
     * f0 -> <INTEGER_LITERAL>
     */
    public String visit(IntegerLiteral n, String[] argu) throws Exception {
	return "int";
    }

    /**
     * f0 -> "true"
     */
    public String visit(TrueLiteral n, String[] argu) throws Exception {
	return "boolean";
    }

    /**
     * f0 -> "false"
     */
    public String visit(FalseLiteral n, String[] argu) throws Exception {
	return "boolean";
    }

    /**
     * f0 -> <IDENTIFIER>
     */
    public String visit(Identifier n, String[] argu) throws Exception {
	return n.f0.toString();
    }

    /**
     * f0 -> "this"
     */
    public String visit(ThisExpression n, String[] argu) throws Exception {
	return currentClass; // Do I even use this visit method anywhere?
    }

    /**
     * f0 -> "new"
     * f1 -> "int"
     * f2 -> "["
     * f3 -> Expression()
     * f4 -> "]"
     */
    public String visit(ArrayAllocationExpression n, String[] argu) throws Exception {
	String index = n.f3.accept(this, null);
	if (!index.equals("int") &&
	    !getVariableType(currentClass, currentMethod, index).equals("int")) {
	    throw new Exception("Non-int expression for array initialization, in method " +
				currentMethod);
	}

	return "int[]";
    }

    /**
     * f0 -> "new"
     * f1 -> Identifier()
     * f2 -> "("
     * f3 -> ")"
     */
    public String visit(AllocationExpression n, String[] argu) throws Exception {
	String type = n.f1.accept(this, null);

	if (getClass(type) == null) {
	    throw new Exception("Type " + type + " has not been defined, in method "  +
				currentMethod);
	}

	return type;
    }

    /**
     * f0 -> "!"
     * f1 -> Clause()
     */
    public String visit(NotExpression n, String[] argu) throws Exception {
	String boolClause = n.f1.accept(this, null);
	if (!boolClause.equals("boolean") &&
	    !getVariableType(currentClass, currentMethod, boolClause).equals("boolean")) {
	    throw new Exception("Trying to negate non-boolean value, in method " + currentMethod);
	}

	return "boolean";
    }

    /**
     * f0 -> "("
     * f1 -> Expression()
     * f2 -> ")"
     */
    public String visit(BracketExpression n, String[] argu) throws Exception {
	return n.f1.accept(this, null);
    }
}
