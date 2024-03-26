# Java ID3 Implementation
A pure-Java implementation of the ID3 decision tree algorithm. Inspired by my python version and written during the first section of my first year Java course so excuse the spaghetti code.

### Usage:
**`Main.java` contains example usage.**

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

This results in:
```
Check Outlook:
  If Outlook = Overcast; Play = Yes
  If Outlook = Rainy; Check Windy:
    If Windy = True; Play = No
    If Windy = False; Play = Yes
  If Outlook = Sunny; Check Humidity:
    If Humidity = High; Play = No
    If Humidity = Normal; Play = Yes
```
