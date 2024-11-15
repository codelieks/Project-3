Group Member Names: Venkat-Sai Gudipati, Faisal Raiza, Collin Brohm
Compile commands: --------------
EITHER: use the supplied compile script OR
compile in order: 
rm -rf bin
javac -d bin -cp src src/*.java
java -cp bin src.TestTupleGenerator OR java -cp bin src.LinHashMap


Explanation:
Switching the underlying data structure to index data involved modifying the index field in the Table class. Depending on the chosen indexing method, the index field was set to different data structures: TreeMap, HashMap, LinkedHashMap, or null for the NoIndex case. This was handled through the setIndexType method, which allowed us to dynamically change the indexing mechanism used for the table data. Here's a concise overview:

NoIndex: Set index to null to disable indexing.

TreeMap: Set index to a new instance of TreeMap<>.

HashMap: Set index to a new instance of HashMap<>.

LinkedHashMap: Set index to a new instance of LinkedHashMap<>.

This approach enabled us to measure and compare the performance of different indexing methods by dynamically switching the underlying data structure used for indexing in the Table class.


Graphs are located within the excel sheet