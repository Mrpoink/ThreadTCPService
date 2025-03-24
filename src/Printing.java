import java.util.Random;
import java.util.concurrent.*;


/**
 * The way I understand it is that we are creating a Java thread,
 * and within it, we are created 'child threads'. My computer has 16 cores
 * so 10 shouldn't be hard. We print it twice and we shutdown
 * the executable service
 */
public class Printing {

    static class PrintTask implements Runnable {
        private final String message;

        public PrintTask(String message) { this.message = message; }

        public void run(){
            Integer n;
            Random rand = new Random();
            n = rand.nextInt();
            do{
                n = (n-1) + (n-2);
            }while(n < 1000);
            System.out.println(message + " " + n);
        }
    }

    public static void main(String[] args) {

        ExecutorService es = Executors.newFixedThreadPool(16);

        es.submit(new PrintTask("Finished 1"));
        es.submit(new PrintTask("Finished 2"));

        es.shutdown();

        /**
         * The output should be:
         * Hello World
         * From Child thread
         */
    }
}
