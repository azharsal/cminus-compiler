// import java.io.*;
// import SemanticAnalyzer.*;
// import absyn.*;

// class CM {

//   static public void main(String argv[]) {    
//     if (argv.length < 2) {
//       System.err.println("Missing Arguments. Run as: java -cp /cup/jar/path CM /file/path -flag");
//       System.exit(1);
//     }

//     String fileName = argv[0];
//     String flag = argv[1];

//     switch (flag) {
//       case "-a":
//         parseFile(fileName);
//         break;
//       case "-s":
//         makeSymbolTable(fileName);
//         break;
//       default:
//         System.err.println("Error: Invalid flag. The Flags are:\n-a: Create AST");
//         System.exit(1);
//     }
//   }

//   static Absyn parseFile(String fileName) {
//     /* Check if the file exists */
//     File file = new File(fileName);
//     if (!file.exists()) {
//       System.err.println("Error: File '" + fileName + "' not found.");
//       System.exit(1);
//     }

//     /* Start the parser */
//     try {
//       parser p = new parser(new Lexer(new FileReader(fileName)));
//       Absyn result = (Absyn)(p.parse().value);      
//       if (result != null) {
//          System.out.println("The Abstract Syntax Tree Is: ");
//          AbsynVisitor visitor = new ShowTreeVisitor();
//          result.accept(visitor, 0); 
//       }
//       return result;

//     } catch (Exception e) {
//       /* do cleanup here -- possibly rethrow e */

//       e.printStackTrace();
//       return null;
//     }
//   }

//   static void makeSymbolTable(String filename)
//   {
//     Absyn tree = parseFile(filename);
//     DecList dec_list = (DecList) tree;
//     SemanticAnalyzer symbol_table = new SemanticAnalyzer(dec_list);
//   }
// }
import java.io.*;
import SemanticAnalyzer.*;
import absyn.*;

class CM {

  static public void main(String argv[]) {
    if (argv.length < 2) {
      System.err.println("Missing Arguments. Run as: java CM /file/path -flag");
      System.exit(1);
    }

    String inputFileName = argv[0];
    String flag = argv[1];

    String baseFileName = inputFileName.substring(0, inputFileName.lastIndexOf('.'));
    String outputFileName;

    switch (flag) {
      case "-a":
        outputFileName = baseFileName + ".ast";
        parseFile(inputFileName, outputFileName);
        break;
      case "-s":
        outputFileName = baseFileName + ".sym";
        makeSymbolTable(inputFileName, outputFileName);
        break;
      default:
        System.err.println("Error: Invalid flag. The Flags are:\n-a: Create AST\n-s: Create Symbol Table");
        System.exit(1);
    }
  }

  static Absyn parseFile(String fileName, String outputFileName) {
    File file = new File(fileName);
    if (!file.exists()) {
      System.err.println("Error: File '" + fileName + "' not found.");
      System.exit(1);
    }

    // Redirect output
    try (PrintStream out = new PrintStream(new FileOutputStream(outputFileName))) {
      System.setOut(out);

      parser p = new parser(new Lexer(new FileReader(fileName)));
      Absyn result = (Absyn)(p.parse().value);
      if (result != null) {
        System.out.println("The Abstract Syntax Tree Is: ");
        AbsynVisitor visitor = new ShowTreeVisitor();
        result.accept(visitor, 0); 
      }
      return result;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  static void makeSymbolTable(String fileName, String outputFileName) {
    Absyn tree = parseFile(fileName, outputFileName + ".tmp");
    if (tree instanceof DecList) {
      DecList dec_list = (DecList) tree;

      try (PrintStream out = new PrintStream(new FileOutputStream(outputFileName))) {
        System.setOut(out);
        SemanticAnalyzer symbol_table = new SemanticAnalyzer(dec_list);
      } catch (FileNotFoundException e) {
        System.err.println("Error writing to output file: " + outputFileName);
        e.printStackTrace();
      }

      new File(outputFileName + ".tmp").delete();
    } else {
      System.err.println("Error: Unable to create symbol table.");
    }
  }
}
