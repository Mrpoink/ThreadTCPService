import java.io.*;
import java.util.Scanner;
import java.util.concurrent.*;

public class Upload_and_Download {

    static final String path = "SourceFiles/";

    static class Upload implements Runnable {

        private String filepath;

        public Upload(String filename){
            this.filepath = path + filename;
        }

        public void run() {
//            BufferedReader fis = null;
//            try {
//                fis = new BufferedReader(new FileReader(filepath));
//
//                String line = fis.readLine();
//
//                System.out.println(line);
//            } catch (FileNotFoundException e) {
//                System.out.println(e);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
        }
    }
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        ExecutorService executor = Executors.newFixedThreadPool(5);

        System.out.println("Enter filename: ");
        String filename = scan.nextLine();

        executor.submit(new Upload(filename));

        executor.shutdown();
    }
}
