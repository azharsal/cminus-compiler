package absyn;

abstract public class VarDec extends Dec {
    public NameTy typ;
    public String name;
    public int offset;
    public int nestLevel;

}