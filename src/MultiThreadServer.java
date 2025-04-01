import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultiThreadServer {

    public static final String filepath = "SourceFiles/";
    public static ExecutorService service = Executors.newFixedThreadPool(5);


    public static void main(String[] args) throws IOException {

        try (ServerSocketChannel listenChannel = ServerSocketChannel.open()){
            listenChannel.bind(new InetSocketAddress(1068));
            SocketChannel sock = listenChannel.accept();
            ServerCommands server = new ServerCommands(sock);
            if (listenChannel.isOpen()){
                service.execute(server.Start());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Executor started");
    }
}
