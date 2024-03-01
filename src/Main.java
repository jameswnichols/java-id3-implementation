public class Main {
    public static void main(String[] args){
        float startTime = System.currentTimeMillis();
        Dataset dataset = new Dataset("courseworkDataset.csv");
        Node rootNode = dataset.getTreeRootNode();
        rootNode.renderTree();
        float elapsedTime = System.currentTimeMillis() - startTime;
        System.out.println("Finished in "+elapsedTime+"ms.");
    }
}
