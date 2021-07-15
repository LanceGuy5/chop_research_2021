import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;

@SuppressWarnings({"unused"})
public class DataSorter {

    private static final String desktop = System.getProperty("user.home");

    private static File CUI_CODES;

    public static void main(String[] args) throws Exception {
        System.out.println("Enter the path of the \"CUI_CODEs.csv\" file:");
        Scanner scanner = new Scanner(System.in);
        String path = scanner.next();
        path = formatFilePath(path);
        File CUI_CODES = new File(path);
        if (Files.exists(CUI_CODES.toPath())) {
            System.out.println("CUI_CODEs.csv file found");
            try {
                System.out.println("Writing two new files to the desktop");
                File HPO = searchForCode("HPO", CUI_CODES);
                File HGNC = searchForCode("HGNC", CUI_CODES);
            } catch (IOException e) {
                e.printStackTrace();
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

    private static File searchForCode(String code, File original) throws IOException {
        ArrayList<String> assoc = new ArrayList<>();
        FileReader fr = new FileReader(original);
        BufferedReader reader = new BufferedReader(fr);
        File ret = new File(desktop + "/desktop/", code + "-CODEs.csv");
        String line = "";
        String[] tempArray;
        while ((line = reader.readLine()) != null) {
            String delimiter = ",";
            tempArray = line.split(delimiter);
            if (tempArray[1].trim().startsWith(code)) assoc.add(line);
        }
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
}