package utils.processes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import utils.csv.CsvFileReader;

public class FileLineProcessingThread extends Thread {
    private String threadName;
    private CsvFileReader csvFileReader;
    private CyclicBarrier barrier;
    private List<Float> results;

    public FileLineProcessingThread(String threadName, CsvFileReader csvFileReader, CyclicBarrier barrier, List<Float> results) {
        this.threadName = threadName;
        this.csvFileReader = csvFileReader;
        this.barrier = barrier;
        this.results = results;
    }

    @Override
    public void run() {
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

        results.add(sumOfAllInvoicesForCurrentThread);
        long end = new Date().getTime();
        System.out.println("Execution time of thread " + threadName + ": " + (end - start) + " ms");
        System.out.println("Sum of all invoices of thread " + threadName + ": " + sumOfAllInvoicesForCurrentThread);
        System.out.println("File lines size processed by thread " + threadName + ": " + fileLinesSize);

        try {
            // the CyclicBarrier will wait for all FileLineProcessingThread to finish, before start the ResultConsolidationThread
            barrier.await();
        } catch (InterruptedException e) {
            e.printStackTrace( );
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }

    public static boolean isNumeric(String strNum) {
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