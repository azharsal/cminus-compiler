JAVA=java
JAVAC=javac
JFLEX=/opt/homebrew/cellar/jflex/1.9.1/bin/jflex
CLASSPATH=-cp /Users/salmanazhar/development/java-cup-bin-11b-20160615/java-cup-11b.jar:.
CUP=$(JAVA) $(CLASSPATH) java_cup.Main

# JFLEX=jflex
# CLASSPATH=-cp /usr/share/java/cup.jar:.
# CUP=cup

all: cm.class

cm.class: absyn/*.java SemanticAnalyzer/*.java parser.java sym.java Lexer.java ShowTreeVisitor.java Scanner.java CM.java
	$(JAVAC) $(CLASSPATH) $^

%.class: %.java
	$(JAVAC) $(CLASSPATH) $^

Lexer.java: cminus.flex
	$(JFLEX) cminus.flex

parser.java: cminus.cup
	$(CUP) -expect 3 cminus.cup

clean:
	rm -f parser.java Lexer.java sym.java *.class absyn/*.class semanticanalyzer/*.class *~
