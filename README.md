# MiniJava semantic checker

A semantic checker for MiniJava programs, using the visitor design pattern. If a
program is semantically correct, it prints in the standard output, the offsets
of the fields and the methods of the classes. These offsets are defined as:

  - 1 byte for `boolean` types
  - 4 bytes for `integer` types
  - 8 bytes for `int[]`,  classes and functions

MiniJava is a subset of Java, it has many familiar features and it can be fully
compiled with `javac`. The BNF version of MiniJava can be found
[here](http://cgi.di.uoa.gr/~thp06/project_files/minijava-new/minijava.html).

## Getting Started

### Prerequisites

  - A modern Java version.
  - `JTB` ([Java Tree Builder](http://compilers.cs.ucla.edu/jtb/)) is required
to build the visitors.
  - [JavaCC](https://javacc.org/download), to create the parser off the
provided grammar file.

The above tools may be provided by your distribution’s package manager (on Linux
systems). In this case, the `makefile` will need to be changed accordingly, to
point to the location of the JTB and JavaCC executables.

### Downloading

To acquire this repo type on a terminal:

    git clone https://github.com/aakordas/mj-semantic-checker

Enter the newly created directory with:

    cd mj-semantic-checker

### Building

To build the program, just type

    make

on the command line and the program will start compiling, provided the `.jar`
files for JTB and JavaCC are in the project’s directory.

### Running

To run the program, just type, after a successful compilation:

    java Main <file1> <file2> ...

Where `<file1>`, `<file2>`, etc. are the files you want to be checked. In case of
an incorrect file, an error message is printed and the program continues to the
next file, if any.

## Disclaimer

This program is not fully complete, yet, It works mostly fine, but it has some
issues when there is inheritance in the input programs. It’s a work in progress.
