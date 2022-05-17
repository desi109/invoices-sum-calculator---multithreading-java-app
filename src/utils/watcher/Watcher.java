package utils.watcher;

public class Watcher {
    private long startTime = -1;

    public void startTimeNanos() {
        this.startTime = System.nanoTime();
    }

    public long endTimeNanos() {
        return System.nanoTime() - this.startTime;
    }

    public double timeMillis() {
        return this.endTimeNanos() / 1000000.0;
    }
}