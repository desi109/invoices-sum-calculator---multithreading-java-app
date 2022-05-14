package utils.processes;

import java.util.List;

public class ResultFinalizationThread extends Thread {
    private List<Float> results;

    public ResultFinalizationThread(List<Float> results) {
        this.results = results;
    }

    @Override
    public void run() {
        System.out.println("Result Finalization Thread started!");
        float sum = 0.0f;

        for (Float result : results) {
            sum += result;
        }

        System.out.println("Invoices sum: " + sum);
    }
}
