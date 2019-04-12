
public class ClassType {
    public static void main(String[] args) {
        MyRunnable myRunnable = new MyRunnable();
        Class<? extends Runnable> myRunnableClass = myRunnable.getClass();
        printAsString(myRunnableClass.getCanonicalName());
    }

    private static void printAsString(String myRunnableClass) {
        System.out.println("myRunnableClass = " + myRunnableClass);
    }

    private static class MyRunnable implements Runnable {

        @Override
        public void run() {

        }
    }
}
