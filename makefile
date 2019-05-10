all: compile

compile:
	java -jar ./jtb132.jar -te ./minijava.jj
	java -jar ./javacc-6.0.jar ./minijava-jtb.jj
	javac ./src/*.java

clean:
	rm -f ./src/*.class ./src/*~
