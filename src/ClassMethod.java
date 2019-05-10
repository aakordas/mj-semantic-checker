import java.util.LinkedHashMap;
import java.util.HashMap;
import java.lang.Exception;

/**
 * A method of a class.
 */
class ClassMethod {
    /**
     * The return type of the method.
     */
    private String type;

    /**
     * The name of the method.
     */
    private String name;

    /**
     * A list (for all intents and purporses) of the method’s arguments.
     *
     * LinkedHashMap ensures that the elements will maintain the order they are insterted with.
     *
     * It maps (name -> type).
     */
    private LinkedHashMap<String, String> arguments;

    /**
     * The variables that are defined in the method.
     *
     * It maps (name -> type)
     */
    private HashMap<String, String> localVariables;

    /**
     * A default constructor for a ClassMethod.
     */
    public ClassMethod() {
	this.type = new String();
	this.name = new String();
	this.arguments = new LinkedHashMap<String, String>();
	this.localVariables = new HashMap<String, String>();
    }

    /**
     * A constructor without the arguments for a ClassMethod
     *
     * @param type The return type of the method.
     * @param name The name of the identifier.
     */
    public ClassMethod(String type, String name) {
	this.type = type;
	this.name = name;
	this.arguments = new LinkedHashMap<String, String>();
	this.localVariables = new HashMap<String, String>();
    }

    /**
     * @return The return type of the method
     */
    public String getType() {
	return type;
    }

    /**
     * @return The name of the method
     */
    public String getName() {
	return name;
    }

    /**
     * @return the arguments’ list
     */
    public LinkedHashMap<String, String> getArguments() {
	return arguments;
    }

    /**
     * @param name The name of the argument we want to learn the type of.
     *
     * @return The type of the provided argument or null if the key does not exist.
     */
    public String getArgumentType(String name) {
	return this.arguments.get(name);
    }

    /**
     * Add a new argument in this construct, if it doesn’t already exist.
     *
     * @param type The type of the argumetn
     * @param name The name of the identifier of the argument.
     *
     * @exception Exception It just throws a generic exception, because I just want the program to
     * die, in whatever way.
     */
    public String addArgument(String type, String name) {
	return this.arguments.put(name, type);
    }

    /**
     * Return the type of the provided identifier, if it exists.
     *
     * @param name The name of which we want to learn the type.
     *
     * @return The type of the identifier, or null, if it doesn’t exist.
     */
    public String getLocalVariableType(String name) {
	return this.localVariables.get(name);
    }

    /**
     * Add to the map the local variable with the provided characteristics.
     *
     * @param type The type of the new local variable.
     * @param name The name of the new identifier.
     *
     * @return null if the insertion was successful.
     */
    public String addLocalVariable(String type, String name) {
	if (this.arguments.containsKey(name)) {
	    return "null";
	}

	return this.localVariables.put(name, type);
    }

    /**
     * Checks if two methods are equal, for polymorphism.
     *
     * Two methods are equal if they have the same return type, the same name and the same number
     * and type of arguments (in the same order).
     *
     * @param e The method in the parent class
     *
     * @return true if the methods are equal, otherwise false.
     */
    // That’s not used anywhere, which is kind of worrying...
    public boolean polymorphicEquals(ClassMethod e) {
	if (this.type == e.getType() && this.name == e.getName() &&
	    this.arguments.equals(e.getArguments())) {
	    return true;
	}

	return false;
    }
}
