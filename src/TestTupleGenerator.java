package src;

import static java.lang.System.out;

public class TestTupleGenerator {
    public static void main(String[] args) {
        TupleGeneratorImpl test = new TupleGeneratorImpl();

        test.addRelSchema("Student",
                         "id name address status",
                         "Integer String String String",
                         "id",
                         null);

        test.addRelSchema("Professor",
                         "id name deptId",
                         "Integer String String",
                         "id",
                         null);

        test.addRelSchema("Course",
                         "crsCode deptId crsName descr",
                         "String String String String",
                         "crsCode",
                         null);

        test.addRelSchema("Teaching",
                         "crsCode semester profId",
                         "String String Integer",
                         "crsCode semester",
                         new String[][] {{"profId", "Professor", "id"},
                                         {"crsCode", "Course", "crsCode"}});

        test.addRelSchema("Transcript",
                         "studId crsCode semester grade",
                         "Integer String String String",
                         "studId crsCode semester",
                         new String[][] {{"studId", "Student", "id"},
                                         {"crsCode", "Course", "crsCode"},
                                         {"crsCode semester", "Teaching", "crsCode semester"}});

        String[] tables = {"Student", "Professor", "Course", "Teaching", "Transcript"};
        int[] tupleCount = {10000, 1000, 2000, 50000, 5000};

        Comparable[][][] resultTest = test.generate(tupleCount);

        for (int i = 0; i < resultTest.length; i++) {
            // Convert domain strings to Class objects
            String[] attribute = test.getAttributeNames(tables[i]);
            String[] domainStrings = test.getDomains(tables[i]);
            Class[] domainClasses = new Class[domainStrings.length];
            
            for (int j = 0; j < domainStrings.length; j++) {
                switch (domainStrings[j]) {
                    case "Integer":
                        domainClasses[j] = Integer.class;
                        break;
                    case "String":
                        domainClasses[j] = String.class;
                        break;
                    case "Double":
                        domainClasses[j] = Double.class;
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported domain: " + domainStrings[j]);
                }
            }

            String[] keyAttributes = test.getKeyAttributes(tables[i]);

            Table table = new Table(tables[i], attribute, domainClasses, keyAttributes);

            // Insert the tuples into the table
            for (Comparable[] tuple : resultTest[i]) {
                table.insert(tuple);
            }

           table.print(); // Print the table for visual check
           System.out.println("___________________________________________");

               // Run select and join operations for each index type
               for (String indexType : new String[]{"NoIndex", "TreeMap", "HashMap", "LinHashMap"}) {
                System.out.println("Testing with index type: " + indexType);
                runSelectAndJoinOperations(table, indexType);
            }
        }
    }

    // Method to run select and join operations based on the index type
    public static void runSelectAndJoinOperations(Table table, String indexType) {
        // Build the index for the given index type
        buildIndexForTable(table, indexType);

        // Select operation on Student where id = 100
        KeyType selectKey = new KeyType(new Comparable[]{100});
        Table selectResult = table.select(selectKey);
        System.out.println("Select Operation Result:");
        selectResult.print();

        // Join operation: Student join Transcript on id = studId
        Table transcriptTable = getTranscriptTable(); // Retrieve the Transcript table (assume it exists in the same way)
        Table joinResult = table.join("id", "studId", transcriptTable);
        System.out.println("Join Operation Result:");
        joinResult.print();
    }

    // Build the index for the table based on the index type
    public static void buildIndexForTable(Table table, String indexType) {
        switch (indexType) {
            case "NoIndex":
                // Do nothing, no index used
                break;
            case "TreeMap":
                // Use TreeMap indexing
                table.setIndexUsingTreeMap();  // Set up TreeMap indexing (method you would define in Table)
                break;
            case "HashMap":
                // Use HashMap indexing
                table.setIndexUsingHashMap();  // Set up HashMap indexing (method you would define in Table)
                break;
            case "LinHashMap":
                // Use LinHashMap indexing
                table.setIndexUsingLinHashMap();  // Set up LinHashMap indexing (method you would define in Table)
                break;
            default:
                throw new IllegalArgumentException("Unsupported index type: " + indexType);
        }
    }

    // Assume you have a method to get the Transcript table
    public static Table getTranscriptTable() {
        // Construct the Transcript table in a similar manner as done for Student
        // Just as an example:
        String[] attribute = {"studId", "crsCode", "semester", "grade"};
        Class[] domainClasses = {Integer.class, String.class, String.class, String.class};
        String[] keyAttributes = {"studId", "crsCode", "semester"};

        Table transcriptTable = new Table("Transcript", attribute, domainClasses, keyAttributes);
        // Add data to Transcript table
        // For simplicity, data could be inserted similarly to the previous tables
        return transcriptTable;
    }
}



