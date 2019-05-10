import java.util.LinkedHashMap;

/**
 * The names keep getting better!
 *
 * A class construct, this will keep all the information for a single class, extended classes,
 * fields, methods...
 */
class ClassClass {
    /**
     * The class this class extends. If none, the it’s null. Being a String is good enough, since
     * I’m using a map to store class information.
     */
    private String extend;

    /**
     * Indicates if a class is the main class or not
     */
    private boolean main;

    /**
     * The name of the class.
     */
    private String name;

    /**
     * A LinkedHashMap for classes’ fields.
     *
     * Maps (name -> type)
     */
    private LinkedHashMap<String, String> fields;

    /**
     * A LinkedHashMap so I can quickly lookup method names.
     *
     * Maps (name -> Method)
     */
    private LinkedHashMap<String, ClassMethod> methods;

    /**
     * The current offset of the variables, for this class.
     */
    private int variablesOffset;

    /**
     * The current offset of the methods, for this class.
     */
    private int methodsOffset;

    /**
     * A default constructor for a ClassClass
     *
     * @param name The name of the class.
     */
    public ClassClass(String name) {
	this.extend = null;
	this.main = false;
	this.name = new String(name);
	this.fields = new LinkedHashMap<String, String>();
	this.methods = new LinkedHashMap<String, ClassMethod>();
	this.variablesOffset= 0;
	this.methodsOffset = 0;
    }

    /**
     * A default constructor for a main class
     *
     * @param main Whether this class is the main or not.
     * @param name The name of the class.
     */
    public ClassClass(boolean main, String name) {
	this.extend = null;
	this.main = main;
	this.name = new String(name);
	this.fields = new LinkedHashMap<String, String>();
	this.methods = new LinkedHashMap<String, ClassMethod>();
	this.variablesOffset = 0;
	this.methodsOffset = 0;
    }

    /**
     * A constructor for a ClassClass with the class it extends.
     *
     * @param extend The name of the "parent" class.
     * @param name The name of the class.
     */
    public ClassClass(String extend, String name) {
	this.extend = new String(extend);
	this.name = new String(name);
	this.fields = new LinkedHashMap<String, String>();
	this.methods = new LinkedHashMap<String, ClassMethod>();
	this.variablesOffset = 0;
	this.methodsOffset = 0;
    }

    /**
     * @return The inherited class.
     */
    public String getExtend() {
	return extend;
    }

    /**
     * @return The name of the class.
     */
    public String getName() {
	return name;
    }

    /**
     * @param fields The fields of the current class
     */
    public void setFields(LinkedHashMap<String, String> fields) {
	this.fields = fields;
    }

    /**
     * In case we are building the fields one by one, insert them one by one.
     *
     * @param type The type of the new field.
     * @param name The name of the identifier of the new field.
     */
    public String addField(String type, String name) {
	return this.fields.put(name, type);
    }

    /**
     * Return the type of the field requested.
     *
     * @param name The name of the field we want to know the type of.
     *
     * @return The type of the field or null, if it doesn’t exist.
     */
    public String getFieldType(String name) {
	return this.fields.get(name);
    }

    /**
     * @param name The name of the method to be returned.
     *
     * @return The ClassMethod with the provided name
     */
    public ClassMethod getMethod(String name) {
	return this.methods.get(name);
    }

    /**
     * Add a method to the current class.
     *
     * @param method The new method to be added.
     *
     * @return Null, if there is no such method, otherwise the ClassMethod object.
     */
    public ClassMethod addMethod(ClassMethod method) {
	return this.methods.put(method.getName(), method);
    }

    /**
     * @return The current offset for the variables of this class.
     */
    public int getVariablesOffset() {
	return this.variablesOffset;
    }

    /**
     * @return The current offset for the methods of this class.
     */
    public int getMethodsOffset() {
	return this.methodsOffset;
    }

    /**
     * Adds an argument in the requested method.
     *
     * @param method The method that has this argument.
     * @param type The type of the new argument.
     * @param name The name of the identifier.
     *
     * @return null if the new argument was added successfully.
     */
    public String addArgument(String method, String type, String name) {
	return this.getMethod(method).addArgument(type, name);
    }

    /**
     * Check how many arguments the requested method has.
     *
     * This is useful when the method has no arguments, because I just need to verify that, I don’t
     * need to type check, or anything.
     *
     * @param name The name of the method we want to know the number of arguments of.
     *
     * @return The number of arguments the method has.
     */
    public int getNumOfArguments(String name) {
	return getMethod(name).getArguments().size();
    }

    /**
     * Add a local variable in the requested method.
     *
     * @param method The method that has this local variable.
     * @param type The type of the new local variable.
     * @param name The name of the identifier.
     *
     * @return null if the local variable was added successfully.
     */
    public String addLocalVariable(String method, String type, String name) {
	return getMethod(method).addLocalVariable(type, name);
    }

    /**
     * Find the type of the variable ‘name’.
     *
     * @param method The method in which we are in.
     * @param name The name of the identifier of which the type is requested.
     *
     * @return The type of the identifier or null, if it wasnt’ found.
     */
    public String getVariableType(String method, String name) {
	// In case the "variable" is a number check that first...
	if (name.matches("[0-9]+")) {
	    return "int";
	}

	// First look into the local variables.
	Object result = getMethod(method).getLocalVariableType(name);

	// If you found something, return it.
	if (result != null) {
	    return (String) result;
	}

	result = getMethod(method).getArgumentType(name);

	if (result != null) {
	    return (String) result;
	}

	// If it hasn’t been found so far, look into this class’s fields.
	result = getFieldType(name);

	return (result == null ? "null" : (String) result);
    }

    /**
     * Prints the fields and the methods of a class, with their correct offset.
     */
    public void printClass() {
	if (!this.main) {
	    this.fields.forEach((n, t) -> {
				System.out.println(this.name + "." + n + ": " + variablesOffset);
				switch(t) {
				case "int":
				    variablesOffset += 4;
				    break;
				case "boolean":
				    variablesOffset += 1;
				    break;
				default:
				    variablesOffset += 8;
				    break;
				}});
	    this.methods.forEach((n, t) -> {
				 System.out.println(this.name + "." + n + ": " + methodsOffset);
				 methodsOffset += 8;
		});
	}
    }

    /**
     * Prints the fields and the methods of a class, starting from the provided offsets.
     *
     * @param vo The offset for the variables.
     * @param mo The offset for the methods.
     */
    public void printClass(int vo, int mo) {
	variablesOffset = vo;
	methodsOffset = mo;

	if (!this.main) {
	    this.fields.forEach((n, t) -> {
		    System.out.println(this.name + "." + n + ": " + variablesOffset);
		    switch(t) {
		    case "int":
			variablesOffset += 4;
			break;
		    case "boolean":
			variablesOffset += 1;
			break;
		    default:
			variablesOffset += 8;
			break;
		    }});

	    this.methods.forEach((n, t) -> {
		    System.out.println(this.name + "." + n + ": " + methodsOffset);
		    methodsOffset += 8;
		});
	}
    }
}
