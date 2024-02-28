import java.util.HashMap;

public class Node {

    private String Class;

    private String targetClass;
    private HashMap<String, Node> children = new HashMap<String, Node>();
    private HashMap<String, String> decisions = new HashMap<String, String>();

    public Node(String targetClass){
        this.targetClass = targetClass;
        this.Class = "NaN";
    }

    public void setClass(String c){
        this.Class = c;
    }

    public void addDecision(String attValue, String targetClassValue){
        this.decisions.put(attValue, targetClassValue);
    }

    public void addChild(String attValue, Node n){
        this.children.put(attValue, n);
    }

    public void renderTree(){
        System.out.println("Check "+this.Class+":");
        this.internalRenderTree(1);
    }

    private void internalRenderTree(int indentLevel){
        String indentSpaces = "  ".repeat(indentLevel); //new String(new char[indentLevel]).replace("\0", "  ");
        for (String decision : this.decisions.keySet()){
            System.out.println(indentSpaces+"If "+this.Class+" = "+decision+"; "+this.targetClass+" = "+this.decisions.get(decision));
        }
        for (String childValue : this.children.keySet()){
            Node childNode = this.children.get(childValue);
            System.out.println(indentSpaces + "If "+this.Class+" = "+childValue+"; Check "+childNode.Class+":");
            childNode.internalRenderTree(indentLevel + 1);
        }
    }
}
