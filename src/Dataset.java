import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.Math;
public class Dataset {

    private String filepath;

    private String targetClass;
    private List<HashMap<String, String>> loadedDataset;
    private List<HashMap<String, String>> currentDataset;
    private List<String> datasetHeaders;

    private boolean canGetTree = false;

    private static int sum(List<Integer> l){
        int total = 0;
        for (Integer i : l){
            total += i;
        }
        return total;
    }

    public static double log2(double N)
    {

        // calculate log2 N indirectly
        // using log() method
        return (Math.log(N) / Math.log(2));
    }

    public double getEntropyFromDataset(String attribute, List<HashMap<String, String>> dataset){
        HashMap<String, Integer> counts = new HashMap<>();
        int total = 0;
        for (HashMap<String, String> row : dataset) {
            String rowValue = row.get(attribute);
            if (counts.containsKey(rowValue)) {
                counts.put(rowValue, counts.get(rowValue) + 1);
            } else {
                counts.put(rowValue, 1);
            }
            total += 1;
        }
        double entropy = 0.0;
        for (String key : counts.keySet()){
            entropy += ((double) counts.get(key) / total) * log2(((double) counts.get(key) / total));
        }
        return -entropy;
    }

    public HashMap<String, EntropyAttributeResult> getEntropyOfAttribute(String attribute, List<HashMap<String, String>> dataset){
        List<String> attributeValues = new ArrayList<>();
        List<String> targetClassValues = new ArrayList<>();
        for (HashMap<String, String> row : dataset) {
            if (!attributeValues.contains(row.get(attribute))) {
                attributeValues.add(row.get(attribute));
            }
            if (!targetClassValues.contains(row.get(this.targetClass))) {
                targetClassValues.add(row.get(this.targetClass));
            }
        }
        HashMap<String, HashMap<String, Integer>> attributeTargetCounts = new HashMap<>();
        for (String attrib : attributeValues){
            HashMap<String, Integer> innerHash = new HashMap<>();
            for (String target : targetClassValues){
                innerHash.put(target, 0);
            }
            attributeTargetCounts.put(attrib, innerHash);
        }
        for (HashMap<String, String> row : dataset){
            Integer currentTargetCount = attributeTargetCounts.get(row.get(attribute)).get(row.get(this.targetClass));
            attributeTargetCounts.get(row.get(attribute)).put(row.get(this.targetClass), currentTargetCount + 1);
        }

        HashMap<String, EntropyAttributeResult> entropyResults = new HashMap<>();
        for (String attributeValue : attributeTargetCounts.keySet()){
            HashMap<String, Integer> targetClassResults = attributeTargetCounts.get(attributeValue);
            List<Integer> targetClassCounts = new ArrayList<>(targetClassResults.values());
            int valueTotal = sum(targetClassCounts);
            double entropy = 0;
            for (Integer targetClassCount : targetClassCounts){
                double prob = (double) targetClassCount / valueTotal;
                entropy += prob != 0 ? prob * log2(prob) : 0.0 ;
            }
            entropyResults.put(attributeValue, new EntropyAttributeResult(-entropy, valueTotal, targetClassResults));

        }

        return entropyResults;
    }

    private List<String> getAttributesFromDataset(List<HashMap<String, String>> dataset){
        HashMap<String, String> firstRow = dataset.get(0);
        List<String> attributes = new ArrayList<>(firstRow.keySet());
        attributes.remove(this.targetClass);
        return attributes;
    }

    private double getInformationGainOfAttribute(HashMap<String, EntropyAttributeResult> entropyResults, int datasetLength, double targetEntropy){
        double informationGain = 0.0;
        for (EntropyAttributeResult result : entropyResults.values()){
            informationGain += ((double) result.total() / datasetLength) * result.entropy();
        }
        return targetEntropy - informationGain;
    }

    private String getNonZeroClassResult(HashMap<String, Integer> classResults){
        for (String value : classResults.keySet()){
            if (classResults.get(value) != 0){
                return value;
            }
        }
        return "";
    }

    private List<Decision> getDecisionsFromInfoGain(HighestInfoGain infoGain){
        List<Decision> decisions = new ArrayList<>();
        HashMap<String, EntropyAttributeResult> attributeEntropies = infoGain.attributeEntropies();
        for (String attribValue : attributeEntropies.keySet()){
            EntropyAttributeResult entropyAttributeResult = attributeEntropies.get(attribValue);
            if (entropyAttributeResult.entropy() == 0.0){
                String resultValue = getNonZeroClassResult(entropyAttributeResult.targetClassResults());
                decisions.add(new Decision(attribValue, resultValue));
            }
        }
        return decisions;
    }

    private List<String> getBranchesFromInfoGain(HighestInfoGain infoGain){
        List<String> branches = new ArrayList<>();
        HashMap<String, EntropyAttributeResult> attributeEntropies = infoGain.attributeEntropies();
        for (String attribValue : attributeEntropies.keySet()){
            EntropyAttributeResult entropyAttributeResult = attributeEntropies.get(attribValue);
            if (entropyAttributeResult.entropy() != 0.0){
                branches.add(attribValue);
            }
        }
        return branches;
    }

    private void getHeaders(){
        this.datasetHeaders = new ArrayList<>();
        try {
            File myObj = new File(this.filepath);
            Scanner myReader = new Scanner(myObj);
            String data = myReader.nextLine();
            this.datasetHeaders = Arrays.asList(data.strip().split(","));
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
        }
        this.targetClass = datasetHeaders.get(datasetHeaders.size()-1);
    }

    private void loadDataset(){
        this.loadedDataset = new ArrayList<>();
        boolean skippedHeaderData = false;
        try {
            File myObj = new File(this.filepath);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                if (!skippedHeaderData){
                    skippedHeaderData = true;
                    continue;
                }

                String[] splitData = data.strip().split(",");
                HashMap<String, String> lineData = new HashMap<>();
                for (int i = 0; i < splitData.length; i++){
                    lineData.put(this.datasetHeaders.get(i), splitData[i]);
                }
                this.loadedDataset.add(lineData);

            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
        }
    }

    private List<HashMap<String, String>> copyDataset(List<HashMap<String, String>> dataset){
        return new ArrayList<>(dataset);
    }

    private List<HashMap<String, String>> filterDataset(String attribute, String attributeValue, List<HashMap<String, String>> dataset){
        List<HashMap<String, String>> newDataset = new ArrayList<>();
        for (HashMap<String, String> row : dataset){
            if (row.get(attribute).equals(attributeValue)){
                HashMap<String, String> newRow = new HashMap<>(row);
                newDataset.add(newRow);
            }
        }
        newDataset = copyDataset(newDataset);
        for (HashMap<String, String> row : newDataset){
            row.remove(attribute);
        }
        return  newDataset;
    }

    public Node getTreeRootNode(){
        Node rootNode = new Node(this.targetClass);

        if (this.canGetTree){
            getChildNodes(rootNode, this.currentDataset);
        }else{
            System.out.println("Can't get Tree Root Node - No File Loaded!");
        }
        return rootNode;
    }

    private void getChildNodes(Node parentNode, List<HashMap<String, String>> dataset){
        List<HashMap<String, String>> thisNodeDataset = copyDataset(dataset);

        double mainClassEntropy = getEntropyFromDataset(this.targetClass, thisNodeDataset);

        List<String> attributes = getAttributesFromDataset(thisNodeDataset);
        HighestInfoGain highestInfoGain = new HighestInfoGain(-100.0, "NaN", new HashMap<>());

        for (String attribute : attributes){
            HashMap<String, EntropyAttributeResult> attributeEntropies = getEntropyOfAttribute(attribute, thisNodeDataset);
            double attributeInfoGain = getInformationGainOfAttribute(attributeEntropies, thisNodeDataset.size(), mainClassEntropy);

            if (attributeInfoGain > highestInfoGain.infoGain()){
                highestInfoGain = new HighestInfoGain(attributeInfoGain, attribute, attributeEntropies);
            }
        }

        parentNode.setClass(highestInfoGain.Class());

        List<Decision> decisions = getDecisionsFromInfoGain(highestInfoGain);
        List<String> branches = getBranchesFromInfoGain(highestInfoGain);

        for (Decision decision : decisions){
            parentNode.addDecision(decision.attributeValue(), decision.targetClassValue());
        }

        for (String branch : branches){
            List<HashMap<String, String>> filteredDataset = filterDataset(highestInfoGain.Class(), branch, thisNodeDataset);
            Node branchNode = new Node(this.targetClass);
            parentNode.addChild(branch, branchNode);
            getChildNodes(branchNode, filteredDataset);
        }
    }

    public Dataset(String filepath){
        this.loadFile(filepath);
    }

    public Dataset(){
        this.canGetTree = false;
    }

    public void loadFile(String filepath){
        this.filepath = filepath;
        this.getHeaders();
        this.loadDataset();
        this.currentDataset = copyDataset(this.loadedDataset);
        this.canGetTree = true;
    }

    public List<HashMap<String, String>> getDataset(){
        return this.loadedDataset;
    }
}
