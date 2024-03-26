# Java ID3 Implementation
A pure-Java implementation of the ID3 decision tree algorithm. Written during my first year Java course so excuse the spaghetti code.

### Usage:
Load a csv dataset using:
```java
Dataset dataset = new Dataset("tennisDataset.csv");
```
**The csv must have headers or the first row will be removed and data will be incorrect.**

Get the root node of the decision tree using:
```java
Node rootNode = dataset.getTreeRootNode();
```

View the tree using:
```java
rootNode.renderTree();
```
