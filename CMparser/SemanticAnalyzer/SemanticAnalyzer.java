package SemanticAnalyzer;


import java.lang.reflect.Array;


import absyn.*;



public class SemanticAnalyzer{

    SymbolTable table;
    final static int SPACES = 4;

    public SemanticAnalyzer(DecList decList)
    {
        table = new SymbolTable();
        analyze(decList);

    }

    public int getType(Dec dtype) {
        if (dtype instanceof SimpleDec) {
            return ((SimpleDec)dtype).typ.typ;
        } else if (dtype instanceof ArrayDec) {
            return ((ArrayDec)dtype).typ.typ;
        } else if (dtype instanceof FunctionDec) {
            return ((FunctionDec)dtype).result.typ;
        }
        // Default or error case
        throw new IllegalStateException("Unsupported Dec subclass");
    }
    
    public String getTypeString(Dec dtype) {
        if (dtype instanceof SimpleDec) {
            return ((SimpleDec)dtype).typ.toString();
        } else if (dtype instanceof ArrayDec) {
            return ((ArrayDec)dtype).typ.toString();
        } else if (dtype instanceof FunctionDec) {
            return ((FunctionDec)dtype).result.toString();
        }
        // Default or error case
        throw new IllegalStateException("Unsupported Dec subclass");
    }

    public boolean checkAssignComp(VarExp lhs, Exp rhs)
    {

        //checks if simpel var arraydec is being assigned to(i.e int k[10];, k = 1;)
        if (rhs instanceof VarExp)
        {
            VarExp v = (VarExp) rhs;
            if ((v.variable instanceof SimpleVar) && (rhs.dtype instanceof ArrayDec))
            {
                if(!((lhs.variable instanceof SimpleVar) && (lhs.dtype instanceof ArrayDec))) 
                {
                    return false;
                }                    
            }
        }

        if((lhs.variable instanceof SimpleVar) && (lhs.dtype instanceof ArrayDec)) 
        {
            if (rhs instanceof VarExp)
            {
                VarExp v = (VarExp) rhs;
                if (!((v.variable instanceof SimpleVar) && (rhs.dtype instanceof ArrayDec)))
                {
                    return false;
                }
            }
            else
            {
                return false;
            }
        } 

        return true;
    }

    public boolean checkOpComp(Exp left, Exp right)
    {
        if (left instanceof VarExp)
        {
            
            VarExp v = (VarExp) left;
            if ((v.variable instanceof SimpleVar) && (left.dtype instanceof ArrayDec))         
            {
                return false;
            }  
        }
        if (right instanceof VarExp)
        {
            VarExp v = (VarExp) right;

            if ((v.variable instanceof SimpleVar) && (right.dtype instanceof ArrayDec))         
            {
                return false;
            }   
        }

        return true;
    }



    public boolean isInteger(Dec dtype) {
        int type = getType(dtype); 
        return type == NameTy.INT;
    }
    
    public boolean isBool(Dec dtype) {
        int type = getType(dtype); 
        return type == NameTy.BOOL;
    }

    private void indent(int level) {
        for (int i = 0; i < level * SPACES; i++) System.out.print(" ");
    }


    




    public void analyze(DecList decList) {
        table.enter_scope("Global");

        table.insert("output", 
        new FunctionDec(
            0, 
            new NameTy(0, NameTy.VOID) , 
            "output",
            new VarDecList(new SimpleDec(0, new NameTy(0, NameTy.INT), "Output_param"), null),
            new NilExp(0)));

        table.insert("input", 
        new FunctionDec(
            0, 
            new NameTy(0, NameTy.INT) , 
            "input",
            new VarDecList(new SimpleDec(0, new NameTy(0, NameTy.VOID), "Input_param"), null),
            new NilExp(1)));

        

        // System.out.println("MAGIC TIME");

        while (decList != null) {
            if (decList.head != null)
              analyze(decList.head);
            decList = decList.tail;
        }
        table.exit_scope("Global");

    }

    public void analyze(Dec leaf)
    {
        if (leaf instanceof FunctionDec)
            analyze((FunctionDec) leaf);
        else if (leaf instanceof VarDec)
            analyze((VarDec) leaf);
    }

    public void analyze(VarDec varDec) {
        if (varDec instanceof SimpleDec) {
            analyze((SimpleDec) varDec);
        } else if (varDec instanceof ArrayDec) {
            analyze((ArrayDec) varDec);
        }
        // Extend with more cases as needed
    }


    public void analyze(FunctionDec leaf) {

//mutual.cm causing issues because the params are never deleted from the symbol table since we never realy exit scope.
        
        if (leaf.body instanceof NilExp)
        {
            // table.scope_level++;
            // table.exit_scope("");
            table.insert(leaf.func, leaf);
            return;

        }

        table.scope_level++;
        analyze(leaf.params); //So that params are in the same scope as the function body.
        table.scope_level--;

        table.insert(leaf.func, leaf);

        analyze(leaf.body, "function " + leaf.func);
        

    } 

    public void analyze(CompoundExp leaf, String scope_name) {

        table.enter_scope(scope_name);

        analyze(leaf.decs); //VarDecList
        analyze(leaf.exps); //ExpList

        table.exit_scope(scope_name);

    }

    public void analyze(VarDecList varDecList) {

        while (varDecList != null) {
            if (varDecList.head != null)
                analyze(varDecList.head);
            varDecList = varDecList.tail;
        }


    }

    public void analyze(ArrayDec leaf) {
        table.insert(leaf.name, leaf);
    }

    public void analyze(SimpleDec leaf) {
        table.insert(leaf.name, leaf);
    }

    public void analyze(ExpList expList) {
        while (expList != null) {
          if (expList.head != null)
            analyze(expList.head);
          expList = expList.tail;
        }
    }
    
    public void analyze(Exp leaf, String scope_name)
    {
        analyze((CompoundExp) leaf, scope_name);
    }

    public void analyze(Exp exp) {
        if (exp instanceof VarExp) {
            analyze((VarExp) exp);
        } else if (exp instanceof CallExp) {
            analyze((CallExp) exp);
        } else if (exp instanceof OpExp) {
            analyze((OpExp) exp);
        } else if (exp instanceof AssignExp) {
            analyze((AssignExp) exp);
        } else if (exp instanceof IfExp) {
            analyze((IfExp) exp);
        } else if (exp instanceof WhileExp) {
            analyze((WhileExp) exp);
        } else if (exp instanceof ReturnExp) {
            analyze((ReturnExp) exp);
        } 
        // else if (exp instanceof CompoundExp) {
        //     analyze((CompoundExp) exp);
        // } 
        else if (exp instanceof IntExp) {
            analyze((IntExp) exp);
        } else if (exp instanceof BoolExp) {
            analyze((BoolExp) exp);
        } else if (exp instanceof NilExp) {
            analyze((NilExp) exp);
        }
    }    
    
    public void analyze(VarExp leaf) {
        
        leaf.dtype= analyze(leaf.variable);
    }

    public Dec analyze(Var leaf)
    {
        if (leaf instanceof SimpleVar)
            return analyze((SimpleVar) leaf);
        else
            return analyze((IndexVar) leaf);

    }



    public Dec analyze(SimpleVar leaf) {
        
        NodeType n = table.lookup_for_use(leaf.name);
        if (n == null)
        {
            table.print_error("Using undeclared variable '" + leaf.name + "'", leaf.pos);

            // return new SimpleDec(leaf.pos, new NameTy(leaf.pos, NameTy.VOID), "SimpleVar_blah");
            return null;
        }
        // else if (!(n.def instanceof SimpleDec))
        //     table.print_error("Variable " + leaf.name + " cannot be used as a SimplVar", leaf.pos);
        return n.def;  
        
    }

    public Dec analyze(IndexVar leaf) {

        analyze(leaf.index);

        if (leaf.index.dtype != null)
        {
            if (getType(leaf.index.dtype) != NameTy.INT)
            table.print_error("Index must evaluate to a type Int", leaf.index.pos);
        }
        
            
        NodeType n = table.lookup_for_use(leaf.name);
        if (n == null)
        {
            table.print_error("Using undeclared variable '" + leaf.name + "'", leaf.pos);
//jk        
            return null;
            // return new ArrayDec(leaf.pos, new NameTy(leaf.pos, NameTy.VOID), "SimpleVar_blah", 1);
        }
        else if (!(n.def instanceof ArrayDec))
            table.print_error("Variable " + leaf.name + " cannot be used as an IndexVar", leaf.pos);
        return n.def;  
    }

    public void analyze(IfExp leaf) {

        analyze(leaf.test);

        if (leaf.test.dtype != null)
        {
            if (!isBool(leaf.test.dtype))
            {
                table.print_error("Test for if statment cannot be evaluated to a boolean", leaf.pos);
            }
        }

        if (leaf.then_seg instanceof CompoundExp)
            analyze(leaf.then_seg, "If scope");
        else
            analyze(leaf.then_seg);

        if (leaf.else_seg instanceof CompoundExp)
            analyze(leaf.else_seg, "If scope");
        else
            analyze(leaf.else_seg);
    }

    public void analyze(WhileExp leaf) {

        analyze(leaf.test);
        if (leaf.test.dtype != null)
        {
            if (!isBool(leaf.test.dtype))
            {
                table.print_error("Test for if statment cannot be evaluated to a boolean", leaf.pos);
                return;
            }
        }

        if (leaf.body instanceof CompoundExp)
            analyze(leaf.body, "While scope");
        else
            analyze(leaf.body);
    }

    public void analyze(AssignExp leaf) {
        analyze(leaf.lhs);
        analyze(leaf.rhs);

        if ((leaf.lhs.dtype != null) && (leaf.rhs.dtype != null))
        {
            if (checkAssignComp(leaf.lhs, leaf.rhs))
            {
                if ( getType(leaf.lhs.dtype) != getType(leaf.rhs.dtype))
                {
                    if (leaf.lhs.variable instanceof SimpleVar)
                        table.print_error("Invalid type being assigned to '" + (((SimpleVar)leaf.lhs.variable).name) + "'", leaf.pos) ;
                    else if (leaf.lhs.variable instanceof IndexVar)
                        table.print_error("Invalid type being assigned to '" + (((IndexVar)leaf.lhs.variable).name) + "'", leaf.pos);
                }
            }
            else
            {
                table.print_error("Incompatible array type with non-array type", leaf.pos);
            }
        }


    }
    public void analyze(OpExp leaf) 
    {
        analyze(leaf.left);
        analyze(leaf.right);

        
        switch(leaf.op) {
            case OpExp.PLUS:
            case OpExp.MINUS:
            case OpExp.MUL:
            case OpExp.DIV:
                // Arithmetic operations
                if (leaf.left.dtype != null && leaf.right.dtype != null)
                {
                    if (checkOpComp(leaf.left, leaf.right))
                    {
                        if (!(isInteger(leaf.left.dtype)) || !(isInteger(leaf.right.dtype))) {
                            table.print_error("Arithmatic operations require both operands to be int", leaf.pos);
                        }
                    }
                    else
                    {
                        table.print_error("Cannot perform arithmatic operation on array type variable", leaf.pos);
                    }
                    leaf.dtype = new SimpleDec(leaf.pos, new NameTy(leaf.pos, NameTy.INT), "OpExp_int_blah");
                }
                else
                {
                    leaf.dtype = new SimpleDec(leaf.pos, new NameTy(leaf.pos, NameTy.INT), "OpExp_int_blah");
                }

                break;
            case OpExp.EQ: //not sure where equal would belong.
            case OpExp.NE:
            case OpExp.LT:
            case OpExp.LE:
            case OpExp.GT:
            case OpExp.GE:
                if (leaf.left.dtype != null && leaf.right.dtype != null)
                {
                    if (checkOpComp(leaf.left, leaf.right))
                    {
                        if (leaf.op == OpExp.EQ)
                        {
                            if (getType(leaf.left.dtype) != getType(leaf.right.dtype))
                            {
                                table.print_error("Checking equality requires both operands to be of the same type", leaf.pos);

                            }
                        }
                        else
                        {   
                            if (!(isInteger(leaf.left.dtype)) || !(isInteger(leaf.right.dtype))) 
                            {
                                table.print_error("Comparison operations require both operands to be int", leaf.pos);
                            }
                        }
                    }
                    else
                    {
                        table.print_error("Cannot perform comparison operation on array type",  leaf.pos);
                    }
                    leaf.dtype = new SimpleDec(leaf.pos, new NameTy(leaf.pos, NameTy.BOOL), "OpExp_int_blah");

                }
                else
                {
                    leaf.dtype = new SimpleDec(leaf.pos, new NameTy(leaf.pos, NameTy.BOOL), "OpExp_int_blah");
                }
                break;
            
            case OpExp.NOT:
            case OpExp.AND:
            case OpExp.OR:

                if (leaf.left.dtype != null && leaf.right.dtype != null)
                {
                    if (checkOpComp(leaf.left, leaf.right))
                    {   
                        if (!(isBool(leaf.left.dtype)) || !(isBool(leaf.right.dtype))) {
                            table.print_error("Logical operations require both operands to be boolean.", leaf.pos);
                        }
                    }
                    else
                    {
                        table.print_error("Cannot perform logical operations on array type", leaf.pos);
                    }

                    leaf.dtype = new SimpleDec(leaf.pos, new NameTy(leaf.pos, NameTy.BOOL), "OpExp_bool_blah");
                }
                else
                {
                    leaf.dtype = new SimpleDec(leaf.pos, new NameTy(leaf.pos, NameTy.BOOL), "OpExp_bool_blah");
                }
                break;
            default:
                table.print_error("Unknown operation", leaf.pos);
                break;
        }
    
        //analyze the lhs, then the right ahnd side. Then, check if there dtypes match. If they do, then great. If not, then make them match by some standard to continue parsing with error recovery
    }


    public void analyze(IntExp leaf) {
        leaf.dtype = new SimpleDec(leaf.pos, new NameTy(leaf.pos, NameTy.INT), "int_blah");
    }
    public void analyze(BoolExp leaf) {
        leaf.dtype = new SimpleDec(leaf.pos, new NameTy(leaf.pos, NameTy.BOOL), "bool_blah");
    }



    public void analyze(CallExp leaf) {

        NodeType functionNode = table.lookup_for_use(leaf.func);
        if (functionNode == null || !(functionNode.def instanceof FunctionDec))
        {
            // table.printSymbolTable();
            table.print_error("Function " + leaf.func + " is not defined", leaf.pos);
            leaf.dtype = null;
            return;
        }

        FunctionDec fDec = (FunctionDec) functionNode.def;

        // leaf.dtype = new SimpleDec(leaf.pos, fDec.result, "call_blah");
        leaf.dtype = fDec;

        VarDecList params = fDec.params;
        ExpList args = leaf.args;

        int argsCount = countExpList(args);
        int paramsCount = countVarDecList(params);

        // System.out.println("argcounts = " + argsCount + ", paramcounts = " + paramsCount);
        
        if (paramsCount != argsCount)
        {
            table.print_error("Function " + leaf.func + " expects " + paramsCount + " arguments, but got " + argsCount, leaf.pos);
        
            return;
        }

        int i = 1;
        while (params != null && args != null)
        {
            if (params.head != null &&  args.head != null)
            {
            //args.head will be either: assignexp, opexp, boolexp, callexp, varexp, intexp.
                
                
                analyze(args.head);

                if (args.head.dtype == null)
                {
                    return;
                }
                // analyze(params.head); //args.head willl either be a opexp, boolexp,

                if (params.head instanceof SimpleDec) 
                {
                    SimpleDec param = (SimpleDec) params.head;

                    // System.out.println("blahblah simpledec " + param.name);
                    if (args.head instanceof VarExp)
                    {
                        VarExp v = (VarExp) args.head;
                        if (v.variable instanceof SimpleVar)
                        {
                            if (args.head.dtype instanceof ArrayDec)
                            {
                                table.print_error("Function expected non-array type argument", leaf.pos);
                                return;
                            }   
                        }
                    }
                    if (getType(args.head.dtype) != param.typ.typ)
                    {
                        table.print_error("Argument " + i + " in function call " + leaf.func +
                        " expected to be " + (param.typ.toString()) + " instead recieved "
                        + getTypeString(args.head.dtype), leaf.pos);
                        return;
                    }   
                }
                else if (params.head instanceof ArrayDec)
                {   
                    ArrayDec param = (ArrayDec) params.head;
                    // System.out.println("blahblha arraydec " + param.name);
                    boolean compat = true;
                    if ((args.head instanceof VarExp))
                    {
                        VarExp v = (VarExp) args.head;
                        if ((v.variable instanceof SimpleVar))
                        {
                            if ((args.head.dtype instanceof ArrayDec))
                            {
                                if (getType(args.head.dtype) != param.typ.typ)
                                {

                                    table.print_error("Argument " + i + " in function call " + leaf.func +
                                    " expected to be " + (param.typ.toString()) + " instead recieved "
                                    + getTypeString(args.head.dtype), leaf.pos);
                                    return;
                                }
                            }
                            else
                                compat = false;
                        }
                        else
                            compat = false;
                    }
                    else
                    {
                        compat = false;
                    }
                    
                    if (!compat)
                    {
                        table.print_error("Function expected array-type argument", leaf.pos);
                        return;
                    } 


                    
                }
                // System.out.println("HWOEJHWIOHEIOUWIWHD " + param.head.name);
                // if (getType(args.head.dtype) != param.typ.typ)
                // {

                //     table.print_error("Argument " + i + " in function call " + leaf.func +
                //     " expected to be " + (params.head.typ.toString()) + " instead recieved "
                //     + (getType(args.head.dtype)), leaf.pos);
                //     return;
                // }
            }      

            params = params.tail;
            args = args.tail;

            i++;
        }
    }
    

    public int countVarDecList(VarDecList list) {

        int count = 0;
        VarDecList param = list;
        while (param != null) {
            if (param.head instanceof SimpleDec) {
                SimpleDec simpleDec = (SimpleDec) param.head;

                if (simpleDec.typ.typ != NameTy.VOID)
                    count++;
            }
            else if (param.head instanceof ArrayDec) {
                ArrayDec arrayDec = (ArrayDec) param.head;
                if (arrayDec.typ.typ != NameTy.VOID)
                    count++;
            }
            param = param.tail;
        }

        return count;
    }
    
    
    public int countExpList(ExpList list) {
        int count = 0;
        while (list != null) {
            if (list.head != null)
            {
                count++;
            }
            list = list.tail;
        }
        return count;
    }
    
    public void analyze(ReturnExp leaf) {
        
        if (!(leaf.exp instanceof NilExp))
        {
            analyze(leaf.exp);
            if (table.curr_return_type.typ == NameTy.VOID)
            {   
                table.print_error("Function expects return of type " + table.curr_return_type.toString(), leaf.pos);
                return;
            }

            if (leaf.exp.dtype != null)
            {
                if (getType(leaf.exp.dtype) != table.curr_return_type.typ)
                    table.print_error("Function expects return of type " + table.curr_return_type.toString(), leaf.pos);
            }
            
        }
        else
        {
            if (table.curr_return_type.typ != NameTy.VOID)
            {   
                table.print_error("Function expects return of type " + table.curr_return_type.toString(), leaf.pos);
                return;
            }
        }
        
    }

    public void analyze(NilExp leaf) {
        leaf.dtype = null;
    }
    public void analyze(NameTy leaf) {}
   
}
