import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.stream.*;


public class ServerCommands {

    public static final String filepath = "SourceFiles/";

    public SocketChannel socketChannel;

    public ServerCommands(){
        try (ServerSocketChannel listenChannel = ServerSocketChannel.open()){
            listenChannel.bind(new InetSocketAddress(1068));
            this.socketChannel = listenChannel.accept(); //executor service thingy needed here lol
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void End(){
        try {
            String str6 = "CLOSING CONNECTION";
            ByteBuffer replyBuffer7 = ByteBuffer.wrap(str6.getBytes());
            socketChannel.write(replyBuffer7);
            socketChannel.close();
        } catch (RuntimeException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void Upload(){
        try {
            String str5 = "UPLOAD";
            ByteBuffer replyBuffer6 = ByteBuffer.wrap(str5.getBytes());
            socketChannel.write(replyBuffer6);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void Download(){
        try {
            String str4 = "DOWNLOAD";
            ByteBuffer replyBuffer5 = ByteBuffer.wrap(str4.getBytes());
            socketChannel.write(replyBuffer5);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public void Append(){
        try {
            String str3 = "APPEND";
            ByteBuffer replyBuffer4 = ByteBuffer.wrap(str3.getBytes());
            socketChannel.write(replyBuffer4);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void Rename(){
        try {
            String str2 = "File name?";
            ByteBuffer replyBuffer3 = ByteBuffer.wrap(str2.getBytes());
            socketChannel.write(replyBuffer3);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void List(){
        StringBuilder file_list;
        try (Stream<Path> paths = Files.walk(Paths.get(filepath))) {
            file_list = new StringBuilder(paths
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.joining(",")));
            ByteBuffer replyBuffer2 = ByteBuffer.wrap(file_list.toString().getBytes());
            socketChannel.write(replyBuffer2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
