package utils.csv;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CsvFileReader implements AutoCloseable {

    private FileReader fr = null;
    private StringBuilder sb = new StringBuilder();
    private int i;

    public CsvFileReader(String fileLocation) throws FileNotFoundException {
        fr = new FileReader(fileLocation);
    }

    public synchronized List<String> getCsvLine() throws IOException {
        sb.setLength(0);
        List<String> fileLine = new ArrayList<>();

        // read every line, split its elements by comma, and put them iн fileLine ArrayList
        while ((i = fr.read()) != -1) {
            char c = (char) i;

            if (c == 10) {  // 10 -> NEW LINE (\n)
                for (String element : sb.toString().split(",")) {
                    fileLine.add(element);
                }
                sb.setLength(0);
                return fileLine;
            } else {
                sb.append(c);
            }
        }

        if (sb.length() != 0) {
            for (String element : sb.toString().split(",")) {
                fileLine.add(element);
            }
            sb.setLength(0);
            return fileLine;
        }
        return null;
    }

    @Override
    public void close() { }
}
