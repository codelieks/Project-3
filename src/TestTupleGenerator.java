package src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.lang.System.out;

public class TestTupleGenerator {
    public static void main(String[] args) {
        TupleGeneratorImpl test = new TupleGeneratorImpl();

        // Add schemas as before
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
                new String[][]{{"profId", "Professor", "id"},
                        {"crsCode", "Course", "crsCode"}});

        test.addRelSchema("Transcript",
                "studId crsCode semester grade",
                "Integer String String String",
                "studId crsCode semester",
                new String[][]{{"studId", "Student", "id"},
                        {"crsCode", "Course", "crsCode"},
                        {"crsCode semester", "Teaching", "crsCode semester"}});

        String[] tables = {"Student", "Professor", "Course", "Teaching", "Transcript"};
        int[] tupleCounts = {500, 1000, 2000, 5000, 10000};

        // Store response times for printing
        Map<String, List<Long>> responseTimes = new HashMap<>();

        for (int count : tupleCounts) {
            for (String indexType : new String[]{"NoIndex", "TreeMap", "HashMap", "LinHashMap"}) {
                System.out.println("Indexing Type: " + indexType + " with " + count + " tuples");
                Comparable[][][] resultTest = test.generate(new int[]{count, count, count, count, count});

                Table studentTable = null;
                Table transcriptTable = null;

                // Create and initialize tables
                for (int i = 0; i < resultTest.length; i++) {
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

                    for (Comparable[] tuple : resultTest[i]) {
                        table.insert(tuple);
                    }

                    if (tables[i].equals("Student")) {
                        studentTable = table;
                    } else if (tables[i].equals("Transcript")) {
                        transcriptTable = table;
                    }
                }

                // Ensure tables are initialized before setting index type
                if (studentTable != null && transcriptTable != null) {
                    studentTable.setIndexType(indexType);
                    transcriptTable.setIndexType(indexType);

                    // Measure response times
                    long startTime, endTime, duration;
                    KeyType keyValue = new KeyType(new Comparable[]{100});
                    if (indexType.equals("NoIndex")) {
                        startTime = System.currentTimeMillis();
                        Table selectResult = studentTable.noIndexSelect(keyValue);
                        endTime = System.currentTimeMillis();
                        duration = endTime - startTime;
                        responseTimes.computeIfAbsent("Select" + indexType + count, k -> new ArrayList<>()).add(duration);
                        System.out.println("NoIndex select duration: " + duration + " ms");

                        startTime = System.currentTimeMillis();
                        Table joinResult = studentTable.noIndexjoin("id", "studId", transcriptTable);
                        endTime = System.currentTimeMillis();
                        duration = endTime - startTime;
                        responseTimes.computeIfAbsent("Join" + indexType + count, k -> new ArrayList<>()).add(duration);
                        System.out.println("NoIndex join duration: " + duration + " ms");
                    } else {
                        startTime = System.currentTimeMillis();
                        Table selectResult = studentTable.select(keyValue);
                        endTime = System.currentTimeMillis();
                        duration = endTime - startTime;
                        responseTimes.computeIfAbsent("Select" + indexType + count, k -> new ArrayList<>()).add(duration);
                        System.out.println("Select duration: " + duration + " ms");

                        startTime = System.currentTimeMillis();
                        Table joinResult = studentTable.join("id", "studId", transcriptTable);
                        endTime = System.currentTimeMillis();
                        duration = endTime - startTime;
                        responseTimes.computeIfAbsent("Join" + indexType + count, k -> new ArrayList<>()).add(duration);
                        System.out.println("Join duration: " + duration + " ms");
                    }
                }
            }
        }

        responseTimes.forEach((key, value) -> {
            System.out.println(key + ": " + value);
        });
    }
}
