package utils;
import java.util.concurrent.Semaphore;
import java.lang.InterruptedException;

public class SemHelper {
    Semaphore[] sems;
    int total;

    public SemHelper(int num) {
        this.total = num;
        this.sems = new Semaphore[num];
        initSems();
    }


    public void initSems() {
        for (int i = 0; i < total; i++) {
            sems[i] = new Semaphore(1, true);
        }
    }

    public void wait(int index) {
        final String FID = "SemHelper.wait()";
        try {
            sems[index].acquire();
        } catch (InterruptedException e) {
            Log.error(FID, "Something failed when acquiring semaphore lock", e);
        }
    }

    public void signal(int index) {
        sems[index].release();
    }

}