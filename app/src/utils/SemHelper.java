package utils;
import java.util.concurrent.Semaphore;

public SemHelper {
    Semaphore[] sems;
    int total;

    public SemHelper(int num) {
        total = num;
        sems = new Semaphore[num]
        initSems();
    }


    public void initSems() {
        for (int i = 0; i < total; i++) {
            sems[i] = new Semaphore(1, true);
        }
    }

    public void wait(int index) {
        sems[index].acquire();
    }

    public void signal(int index) {
        sems[index].release();
    }

}