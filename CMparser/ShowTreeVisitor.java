import absyn.*;

public class ShowTreeVisitor implements AbsynVisitor {

  final static int SPACES = 4;

  private void indent(int level) {
    for (int i = 0; i < level * SPACES; i++) System.out.print(" ");
  }

  public void visit(ExpList expList, int level) {
    while (expList != null) {
      if (expList.head != null)
        expList.head.accept(this, level);
      expList = expList.tail;
    }
  }

  public void visit(VarDecList varDecList, int level) {

    while (varDecList != null) {
      if (varDecList.head != null)
        varDecList.head.accept(this, level);
      varDecList = varDecList.tail;
    }
  }

  public void visit(DecList decList, int level) {
    while (decList != null) {
      if (decList.head != null)
        decList.head.accept(this, level);
      decList = decList.tail;
    }
  }

  public void visit(AssignExp exp, int level) {
    indent(level);
    System.out.println("AssignExp:");
    level++;
    exp.lhs.accept(this, level);
    exp.rhs.accept(this, level);
  }

  public void visit(IfExp exp, int level) {
    indent(level);
    System.out.println("IfExp:");
    level++;
    exp.test.accept(this, level);
    exp.then_seg.accept(this, level);
    if (exp.else_seg != null)
      exp.else_seg.accept(this, level);
  }

  public void visit(IntExp exp, int level) {
    indent(level);
    System.out.println("IntExp: " + exp.value);
  }

  public void visit(OpExp exp, int level) {
    indent(level);
    System.out.print("OpExp:");
    switch (exp.op) {
      case OpExp.MINUS:
        System.out.println(" - ");
        break;
      case OpExp.UMINUS:
      System.out.println(" - ");
      break;
      case OpExp.PLUS:
        System.out.println(" + ");
        break;
      case OpExp.MUL:
        System.out.println(" * ");
        break;
      case OpExp.DIV:
        System.out.println(" / ");
        break;
      case OpExp.EQ:
        System.out.println(" == ");
        break;
      case OpExp.NE:
        System.out.println(" != ");
        break;
      case OpExp.LT:
        System.out.println(" < ");
        break;
      case OpExp.LE:
        System.out.println(" <= ");
        break;
      case OpExp.GT:
        System.out.println(" > ");
        break;
      case OpExp.GE:
        System.out.println(" >= ");
        break;
      case OpExp.NOT:
        System.out.println(" ! ");
        break;
      case OpExp.OR:
        System.out.println(" || ");
        break;
      case OpExp.AND:
        System.out.println(" && ");
        break;
      default:
        System.out.println("Invalid operator at line: " + exp.pos);
    }
    level++;
    exp.left.accept(this, level);
    exp.right.accept(this, level);
  }

  public void visit(VarExp exp, int level) {
    indent(level);
    System.out.println("VarExp: ");
    level++;
    if (exp.variable != null)
      exp.variable.accept(this, level);
  }

  public void visit(ArrayDec exp, int level) {
    indent(level);


    if (exp.size == 0)
      System.out.println("ArrayDec: " + exp.name + "[]");
    else
      System.out.println("ArrayDec: " + exp.name + "[" + exp.size + "]");
    
    level++;
    if (exp.typ != null)
      exp.typ.accept(this, level);

    
  }

  public void visit(CallExp exp, int level) {
    indent(level);
    System.out.println("CallExp: " + exp.func);
    level++;
    if (exp.args != null)
      exp.args.accept(this, level);
  }

  public void visit(CompoundExp exp, int level) {
    indent(level);
    System.out.println("CompoundExp: ");

    if (exp.decs != null && exp.exps != null)
      level++;

    if (exp.decs != null)
      exp.decs.accept(this, level);
    if (exp.exps != null)
      exp.exps.accept(this, level);
  }

  public void visit(FunctionDec exp, int level) {
    indent(level);

    if (exp.func != null)
      System.out.println("FunctionDec: " + exp.func);
    else 
    System.out.println("FunctionDec:");
   
    

    level++;

    if (exp.result!= null)
      exp.result.accept(this, level);

    if (exp.params != null)
      exp.params.accept(this, level);

    if (exp.body != null)
      exp.body.accept(this, level);
  }

  public void visit(IndexVar exp, int level) {
    indent(level);
    System.out.println("IndexVar: " + exp.name);
    level++;
    exp.index.accept(this, level);
  }

  public void visit(NilExp exp, int level) {
    // indent(level);
    // System.out.println("NilExp:");
  }

  public void visit(ReturnExp exp, int level) {
    indent(level);
    System.out.println("ReturnExp: ");
    level++;

    if (exp.exp != null)
      exp.exp.accept(this, level);
  }

  public void visit(SimpleDec exp, int level) {
    indent(level);

    if (exp.name != null)
      System.out.println("SimpleDec: " + exp.name);
    else
    System.out.println("SimpleDec:");

    level++;
    if (exp.typ != null)
      exp.typ.accept(this, level);

    
  }

  public void visit(SimpleVar exp, int level) {
    indent(level);
    System.out.println("SimpleVar: " + exp.name);
  }

  public void visit(WhileExp exp, int level) {
    indent(level);
    System.out.println("WhileExp: ");
    level++;
    if (exp.test != null)
      exp.test.accept(this, level);
    if (exp.body != null)
      exp.body.accept(this, level);
  }

  public void visit(NameTy exp, int level) {
    indent(level);
    if (exp.typ == NameTy.BOOL)
      System.out.println("NameTy: BOOL");
    if (exp.typ == NameTy.INT)
      System.out.println("NameTy: INT");
    if (exp.typ == NameTy.VOID)
      System.out.println("NameTy: VOID");
  }

  public void visit(BoolExp exp, int level) {
    indent(level);
    System.out.println("BoolExp: " + exp.value);

  }
}
