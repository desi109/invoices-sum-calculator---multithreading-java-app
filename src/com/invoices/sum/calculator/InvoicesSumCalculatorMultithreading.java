package com.invoices.sum.calculator;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CyclicBarrier;
import utils.csv.CsvFileReader;
import utils.processes.FileLineProcessingThread;
import utils.processes.ResultFinalizationThread;

public class InvoicesSumCalculatorMultithreading {

    private static final String FILE_PATH = new File(Paths.get(".").toString(), "resources/test-invoices.csv").getAbsolutePath();
    private static final int NUM_THREADS = 6;
    private static CyclicBarrier barrier;
    private static List<Float> results = new ArrayList<>(NUM_THREADS);

    private static List<Thread> threads = new ArrayList<>(NUM_THREADS);

    public static void main(String[] args) {

        // 1. Start the program and the main thread
        try {
            // initialize a CyclicBarrier to wait for all FileLineProcessingThread to finish, before start the ResultConsolidationThread
            barrier = new CyclicBarrier(NUM_THREADS, new ResultFinalizationThread(results));
            long beforeUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

            // 2. Load and process the file invoices.csv
            CsvFileReader csvFileReader = new CsvFileReader(FILE_PATH);
            long startTime = System.currentTimeMillis();
            processPostsByLineMultithreading(csvFileReader);
            long endTime = System.currentTimeMillis();

            long afterUsedMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();

            System.out.println("Reading took: " + ((endTime - startTime)) + " ms");
            System.out.println("Memory used for the for the whole multithreading program: " +  ((afterUsedMemory - beforeUsedMemory) / 1024.0) + " MB");

        } catch (FileNotFoundException ex) {
            System.out.println(FILE_PATH + " does not exists!");
        }
    }

    private static void processPostsByLineMultithreading(CsvFileReader csvFileReader) {
        for (int i = 1; i <= NUM_THREADS; ++i) {
            FileLineProcessingThread thread = new FileLineProcessingThread("Thread #" + i, csvFileReader, barrier, results);
            thread.start();
            threads.add(thread);
        }
    }
}
