import syntaxtree.*;
import visitor.*;

import java.util.Map;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    public static void main (String[] args) {
	if (args.length < 1) {
	    System.err.println("Usage: java Main <inputFile> [<inputFile1> <inputFile2> ...]");
	    System.exit(1);
	}

	FileInputStream fis = null;

	try {
	    for (String a : args) {
		fis = new FileInputStream(a);
		MiniJavaParser parser = new MiniJavaParser(fis);
		Goal root = parser.Goal();

		PopulatingVisitor pv = new PopulatingVisitor();
		CheckingVisitor cv = new CheckingVisitor();

		try {
		    root.accept(pv, null);
		    root.accept(cv, null);

		    ClassClass parent;
		    for (Map.Entry<String, ClassClass> cls : PopulatingVisitor.classes.entrySet()) {
			System.out.println("----------- " + cls.getValue().getName() + " -----------");
			if (cls.getValue().getExtend() == null) {
			    cls.getValue().printClass();
			} else {
			    // Gets the parent class.
			    parent = PopulatingVisitor.classes.get(cls.getValue().getExtend());
			    // Prints the current class continuing from the  offsets of the parent class.
			    cls.getValue().printClass(parent.getVariablesOffset(), parent.getMethodsOffset());
			}
		    }
		    System.out.println();
		} catch (Exception e) {
		    System.out.println(e.getMessage());
		    System.out.println();

		    continue;
		}
	    }
	} catch (ParseException ex) {
	    System.out.println(ex.getMessage());
	} catch (FileNotFoundException ex) {
	    System.err.println(ex.getMessage());
	} finally {
	    try {
		if (fis != null) {
		    fis.close();
		}
	    } catch (IOException ex) {
		System.err.println(ex.getMessage());
	    }
	}
    }
}
