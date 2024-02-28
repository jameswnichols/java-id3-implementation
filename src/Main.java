public class Main {



    public static void main(String[] args){
        Dataset dataset = new Dataset("courseworkDataset.csv");
        Node rootNode = dataset.getTreeRootNode();
        rootNode.renderTree();
    }
}
