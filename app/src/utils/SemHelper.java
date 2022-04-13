package utils;
import java.util.concurrent.Semaphore;
import java.lang.InterruptedException;

public class SemHelper {
    Semaphore[] sems; // Array that contains a semaphore for each file
    int total; // Total number of files


    public SemHelper(int num) {
        this.total = num;
        this.sems = new Semaphore[num];
        initSems();
    }


    /**
     * Initializes each semaphore
     */
    public void initSems() {
        for (int i = 0; i < total; i++) {
            // Each semaphore lock can be held by 1 process at once, queue is ordered on arrival time for getting the lock
            sems[i] = new Semaphore(1, true);
        }
    }


    /**
     * Makes the thread wait for he semaphore lock
     */
    public void wait(int index) {
        final String FID = "SemHelper.wait()";
        try {
            sems[index].acquire(); // Wait for lock, process goes to sleep until given access
        } catch (InterruptedException e) {
            Log.error(FID, "Something failed when acquiring semaphore lock", e);
        }
    }


    /**
     * Done with semaphore lock and gives it up, wakes up next process and gives them the lock
     */
    public void signal(int index) {
        sems[index].release(); // Signal the next sleeping thread
    }

}