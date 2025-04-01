//import java.time.Instant;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.*;
//
//public class Simulation implements Callable<Long> {
//
////    private long points;
////    private static Integer START;
////
////    public Simulation(long points) {this.points  = points;}
////
////    public Long call() throws Exception {
////        long pointsInsideCircle = 0;
////        Instant start = Instant.now();
////        for(long i=0; i<points; i++) {
////            //randomly generate a point but ensure it is inside the square.
////            double x = ThreadLocalRandom.current().
////                    nextDouble(0, 2);
////            double y = ThreadLocalRandom.current().
////                    nextDouble(0, 2);
////            if (Math.sqrt((x - 1) * (x - 1) + (y - 1) * (y - 1)) <= 1) {
////                //this point is inside the circle
////                pointsInsideCircle++;
////            }
////        }
////        return pointsInsideCircle;
////    }
////
////    private static final long END = 100_000_000;
////
////    private static final int THREADS = 4;
////
////    public static void main(String[] args) throws Exception {
////        long i = START;
////        ExecutorService es = Executors.newFixedThreadPool(THREADS);
////        List<Future<Long>> resultList = new ArrayList<>();
////
////        for(long j=i;j<THREADS; i=START+(END-START)/THREADS, j++){
////            Callable<Long> Task = new CountingTask(i, START+(END-START)/THREADS*j);
////            Future<Long> result = es.submit(Task);
////            resultList.add(result);
////        }
////        Callable<Long> lastTask = new CountingTask(i, END);
////        Future<Long> lastResult = es.submit(lastTask);
////
////    }
//}

