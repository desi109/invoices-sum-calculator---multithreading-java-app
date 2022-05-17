package com.invoices.sum.calculator;

import utils.csv.CsvFileReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import utils.watcher.Watcher;

public class InvoicesSumCalculatorSingleThreaded {

    private static final String FILE_PATH = new File(Paths.get(".").toString(), "resources/invoices.csv").getAbsolutePath();

    public static void main(String[] args) {

        // 1. Start the program and the main thread
        try {
            long beforeUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

            // 2. Load and process the file invoices.csv
            CsvFileReader csvFileReader = new CsvFileReader(FILE_PATH);
            Watcher watcher = new Watcher();
            watcher.startTimeNanos();
            processPostsByLineSingleThreaded(csvFileReader);
            watcher.endTimeNanos();

            long afterUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

            System.out.println("Reading took: " + watcher.timeMillis() + " ms");
            System.out.println("Memory used from a single thread: " + ((afterUsedMemory - beforeUsedMemory) / 1024.0) + " MB");

        } catch (FileNotFoundException ex) {
            System.out.println(FILE_PATH + " does not exists!");
        }
    }

    private static void processPostsByLineSingleThreaded(CsvFileReader csvFileReader) {
        Watcher watcher = new Watcher();
        watcher.startTimeNanos();
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
                    if (isNumeric(fileLine.get(4))) {
                        float invoiceQuantity = Float.parseFloat(fileLine.get(4));
                        sumOfAllInvoicesForCurrentThread += invoiceAmount * invoiceQuantity;
                    } else {
                        sumOfAllInvoicesForCurrentThread += invoiceAmount;
                    }
                    //simulate more complicated computational work
                    // Thread.sleep(1);
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

        watcher.endTimeNanos();
        System.out.println("Execution time for a single thread: " + watcher.timeMillis() + " ms");
        System.out.println("File lines size processed by a single thread: " + fileLinesSize);
        System.out.println("Invoices sum:  " + sumOfAllInvoicesForCurrentThread);
    }

    private static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            float f = Float.parseFloat(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
