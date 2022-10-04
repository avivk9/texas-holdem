package Cards;

import java.io.*;

public class Test {
    public static void main(String[] args) throws IOException {
        String dataFileLocation = "Cards\\name_stock.csv";
        File data = new File(dataFileLocation);
        data.createNewFile();
        PrintWriter writeToData = new PrintWriter(new FileWriter(data, true));
        BufferedReader readFromData = new BufferedReader(new FileReader(data));
        writeToData.println("aviv,123");
        writeToData.println("jhon,12345");
        System.out.println(readFromData.readLine());


        writeToData.close();
        readFromData.close();
    }
}
