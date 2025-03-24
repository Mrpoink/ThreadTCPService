import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadServer {

    public static final String filepath = "SourceFiles/";

    public static Runnable Wait(SocketChannel socketChannel) {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int bytesRead = socketChannel.read(buffer);
            System.out.println("Line 24 complete");
            buffer.flip();
            byte[] a = new byte[bytesRead];
            buffer.get(a);
            String clientMessage = new String(a);
            System.out.println("Line 29: " + clientMessage);
            buffer.clear();
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {

        ServerCommands commands = new ServerCommands();

        ExecutorService service = Executors.newFixedThreadPool(5);

        service.submit(Wait(commands.socketChannel));
    }
}
