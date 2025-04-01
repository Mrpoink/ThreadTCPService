import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

public class SingleThreadedSimulation {

    public static void main(String[] args) {
        long totalPoints = 100_000_000;
        long pointsInsideCircle = 0;
        Instant start = Instant.now();
        for(long i=0; i<totalPoints; i++){
            //randomly generate a point but ensure it is inside the square.
            double x = ThreadLocalRandom.current().
                    nextDouble(0,2);
            double y = ThreadLocalRandom.current().
                    nextDouble(0,2);
            if(Math.sqrt((x-1)*(x-1) + (y-1)*(y-1)) <= 1){
                //this point is inside the circle
                pointsInsideCircle ++;
            }
        }
        double pi = pointsInsideCircle/(double)totalPoints * 4;
        Instant finish = Instant.now();
        long timeElapsed = Duration.between(start, finish).toMillis();
        System.out.println("pi="+pi);
        System.out.println("runtime="+timeElapsed);
    }
}
