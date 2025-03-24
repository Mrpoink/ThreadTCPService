import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadServer {

    public static ServerCommands commands = new ServerCommands();
    public static final String filepath = "SourceFiles/";
    public static ExecutorService service = Executors.newFixedThreadPool(5);

    public static Runnable Wait(SocketChannel socketChannel) {
        try {
            Start(socketChannel);
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void Start(SocketChannel socketChannel) throws IOException {
        while (true) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int bytesRead = socketChannel.read(buffer);
            System.out.println("Line 24 complete");
            buffer.flip();
            byte[] a = new byte[bytesRead];
            buffer.get(a);
            String clientMessage = new String(a);
            System.out.println("Line 29: " + clientMessage);
            switch (clientMessage) {
                case "L":
                    commands.List();
                    break;
                case "A":
                    commands.Append();
                    break;
                case "U":
                    commands.Upload();
                    break;
                case "D":
                    commands.Download();
                    break;
                case "R":
                    commands.Rename();
                    break;
                case "E":
                    commands.End();
                    break;
            }
            buffer.clear();
        }
    }

    public static void main(String[] args) throws IOException {
        service.submit(Wait(commands.socketChannel));

    }
}
