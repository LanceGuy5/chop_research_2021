import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"unused"})
public class DataSorter {

    private static final String desktop = System.getProperty("user.home");

    private static File CUI_CODES;

    private static ArrayList<ArrayList<String>> sets = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        System.out.println("Enter the path of the \"CUI_CODEs.csv\" file:");
        Scanner scanner = new Scanner(System.in);
        String path = scanner.next();
        path = formatFilePath(path);
        File CUI_CODES = new File(path);
        if (Files.exists(CUI_CODES.toPath())) {
            boolean filesMade = false;
            System.out.println("CUI_CODEs.csv file found");
            try {
                System.out.println("Writing two new files to the desktop");
                File HPO = searchForCode("HPO", CUI_CODES, true);
                File HGNC = searchForCode("HGNC", CUI_CODES, true);
                if(HPO.exists() && HGNC.exists()) filesMade = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(filesMade){
                System.out.println("Would you like to merge the new files into a new file? \"Y\" or \"N\"");
                String choice = scanner.nextLine();
                if(!(choice.equalsIgnoreCase("Y") || choice.equalsIgnoreCase("N"))) {
                    while (!(choice.equalsIgnoreCase("Y") || choice.equalsIgnoreCase("N"))) {
                        System.out.println("Not valid choice. . . pick \"Y\" or \"N\"");
                        choice = scanner.nextLine();
                    }
                }
                if(choice.equalsIgnoreCase("Y")){
                    File MERGED = mergeFiles();
                }
            }
        } else throw new Exception("File does not exist or the path input was incorrect.");
    }

    //TODO Method used to properly format the file path based on OS
    private static String formatFilePath(String path) {
        String OS = System.getProperty("os.name");
        if (OS.equals("Windows 10")) {
            path = path.replace("_", "-");
        }
        return path.trim();
    }

    /**
     * Method to convert the original CUI_CODEs.csv file into a new csv file of whatever SAB code.
     * @param code Whatever code you would like to be selected - ex. HPO, HGNC.
     * @param original The original CUI_CODEs.csv file.
     * @param addToMerge Boolean value that, if true, allows the data to be added to a merged file later on.
     * @return The newly created csv file of all of the specific SAB code.
     * @throws IOException If either the BufferedReader or BufferedWriter is unable to function properly.
     * @see BufferedReader
     * @see BufferedWriter
     */
    private static File searchForCode(String code, File original, boolean addToMerge) throws IOException {
        ArrayList<String> assoc = new ArrayList<>();
        FileReader fr = new FileReader(original);
        BufferedReader reader = new BufferedReader(fr);
        File ret = new File(desktop + "/desktop/", code + "-CODEs.csv");
        String line = "";
        String[] tempArray;
        while ((line = reader.readLine()) != null) {
            String delimiter = ",";
            tempArray = line.split(delimiter);
            if (tempArray[1].trim().startsWith(code))
                assoc.add(line.substring(0, line.indexOf(',') + 1) + line.substring(line.indexOf(',')+ code.length() + 1));
        }
        if(addToMerge) sets.add(assoc);
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(ret));
            System.out.println("Writing. . .");
            StringBuilder sb = new StringBuilder();
            sb.append(":START_ID,:END_ID\n");
            for (int i = 0; i < assoc.size(); i++) {
                if (i != assoc.size() - 1) sb.append(assoc.get(i)).append("\n");
                else sb.append(assoc.get(i));
            }
            writer.write(sb.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.println(code + " file written!");
            reader.close();
        }
        return ret;
    }

    /**
     * Merges all of the files created above.
     * @return The new merged file.
     */
    private static File mergeFiles(){
        LinkedHashMap<Integer, String> mappedValues = new LinkedHashMap<>();
        File ret = new File(desktop + "/desktop/", "MERGED-HPO-HGNC.csv");
        for (ArrayList<String> set : sets) {
            for (int i = 1; i < set.size(); i++) {
                String tempLine = set.get(i);
                mappedValues.put(Integer.parseInt(tempLine.substring(1, 8)), tempLine);
            }
            System.out.println("Data loaded into the Map!");
            LinkedHashMap<Integer, String> sorted = mappedValues
                    .entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                            (oldValue, newValue) -> oldValue, LinkedHashMap::new));
            System.out.println("Data sorted.");
            System.out.println("Writing data to file on desktop:");
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(ret));
                System.out.println("Writing. . .");
                StringBuilder sb = new StringBuilder();
                sb.append(":START_ID,:END_ID\n");
                for(Map.Entry<Integer, String> entry: sorted.entrySet()){
                    sb.append(entry.getValue()).append("\n");
                }
                writer.write(sb.toString());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                System.out.println("Merged file written!");
            }
        }
        return ret;
    }
}