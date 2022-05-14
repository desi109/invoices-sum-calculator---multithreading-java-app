package com.invoices.sum.calculator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import utils.csv.CsvFileReader;
import static utils.processes.FileLineProcessingThread.isNumeric;

public class InvoicesSumCalculatorSingleThreaded {

    private static final String FILE_PATH = new File(Paths.get(".").toString(), "resources/invoices.csv").getAbsolutePath();

    public static void main(String[] args) {

        // 1. Start the program and the main thread
        try {
            long beforeUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

            // 2. Load and process the file invoices.csv
            CsvFileReader csvFileReader = new CsvFileReader(FILE_PATH);
            long startTime = new Date().getTime();
            processPostsByLineSingleThreaded(csvFileReader);
            long endTime = new Date().getTime();

            long afterUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

            System.out.println("Reading took: " + ((endTime - startTime)) + " ms");
            System.out.println("Memory used from a single thread: " +  ((afterUsedMemory - beforeUsedMemory) / 1024.0) + " MB");

        } catch (FileNotFoundException ex) {
            System.out.println(FILE_PATH + " does not exists!");
        }
    }

    private static void processPostsByLineSingleThreaded(CsvFileReader csvFileReader) {
        long start = new Date().getTime();
        int fileLinesSize = 0;
        float sumOfAllInvoicesForCurrentThread = 0.0f;
        List<String> fileLine = new ArrayList<>();

        try {
            fileLine = csvFileReader.getCsvLine();

            while (fileLine != null) {
                ++fileLinesSize;

                // check if the sixth element is numeric (the invoice amount)
                if (isNumeric(fileLine.get(5))) {
                    float invoiceAmount = Float.parseFloat(fileLine.get(5));
                    sumOfAllInvoicesForCurrentThread += invoiceAmount;
                } else {
                    if (fileLine.size() < 5) {
                        String lineContent = "[";
                        int elementNumber = 0;
                        for (String element : fileLine) {
                            if ((fileLine.size() - 1) == elementNumber) {
                                lineContent += element.trim() + "]";
                            } else {
                                lineContent += element.trim() + ", ";
                            }
                            elementNumber++;
                        }

                        System.out.println("Warning: inconsistent line: " + fileLinesSize + "! Content: " + lineContent);
                        continue;
                    }
                }

                fileLine = csvFileReader.getCsvLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        long end = new Date().getTime();
        System.out.println("Execution time for a single thread: " + (end - start) + " ms");
        System.out.println("File lines size processed by a single thread: " + fileLinesSize);
        System.out.println("Invoices sum:  " + sumOfAllInvoicesForCurrentThread);

    }
}
