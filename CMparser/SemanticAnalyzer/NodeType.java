package SemanticAnalyzer;
 import absyn.*;

public class NodeType {
    public String id;
    public int level;
    public Dec def;
    
    public NodeType(String id, int level, Dec def) {
        this.id = id;
        this.level = level;
        this.def = def;
    }
}

