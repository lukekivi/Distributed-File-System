package utils;

public class Log {
    public static void error(String id, String msg) {
        System.out.println("ERROR:" + id + " - " + msg);
        System.exit(1);
    }

    public static void error(String id, String msg, Exception x) {
        System.out.println("ERROR:" + id + " - " + msg);
        x.printStackTrace();
        System.exit(1);
    }

    public static void inof(String id, String msg) {
        System.out.println(id + " - " + msg);
    }
}