package absyn;

public class IfExp extends Exp {
  public Exp test;
  public Exp then_seg;
  public Exp else_seg;

  public IfExp( int pos, Exp test, Exp then_seg, Exp else_seg) {
    this.pos = pos;
    this.test = test;
    this.then_seg = then_seg;
    this.else_seg = else_seg;
  }

  public void accept( AbsynVisitor visitor, int level ) {
    visitor.visit( this, level );
  }
}