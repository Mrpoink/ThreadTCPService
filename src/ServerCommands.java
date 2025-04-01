import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.stream.*;


public class ServerCommands {

    public static final String filepath = "SourceFiles/";

    public static SocketChannel socketChannel;

    public ServerCommands(SocketChannel socket){
        socketChannel = socket;
    }

    public Runnable Start() throws IOException {
        while (true) {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int bytesRead = socketChannel.read(buffer);
            System.out.println("Line 23 complete");
            buffer.flip();
            byte[] a = new byte[bytesRead];
            buffer.get(a);
            String clientMessage = new String(a);
            System.out.println("Line 28: " + clientMessage);
            buffer.clear();
            switch (clientMessage) {
                case "L":
                    List();
                    buffer.clear();
                    break;
                case "U":
                    Upload();
                    buffer.clear();
                    break;
                case "D":
                    Download();
                    buffer.clear();
                    break;
            }
        }
    }

    public static void End(){
        try {
            String str6 = "CLOSING CONNECTION";
            ByteBuffer replyBuffer7 = ByteBuffer.wrap(str6.getBytes());
            socketChannel.write(replyBuffer7);
            socketChannel.close();
        } catch (RuntimeException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void Upload() {
        try {

            String str5 = "File name?";
            ByteBuffer replyBuffer6 = ByteBuffer.wrap(str5.getBytes());
            socketChannel.write(replyBuffer6);
            replyBuffer6.clear();

            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int bytesRead = socketChannel.read(buffer);
            System.out.println("Line 23 complete");
            buffer.flip();
            byte[] a = new byte[bytesRead];
            buffer.get(a);
            String clientMessage = new String(a);
            System.out.println("Line 28: " + clientMessage);
            buffer.clear();
            File file = new File("SourceFiles/" + clientMessage);
            if (!file.exists()) {
                System.out.println("File does not exist");
            } else {
                FileInputStream fis = new FileInputStream(file);
                FileChannel fc = fis.getChannel();
                ByteBuffer filecontent = ByteBuffer.allocate(1024);
                int bytesRead1 = 0;
                do {
                    bytesRead1 = fc.read(filecontent);
                    filecontent.flip();
                    socketChannel.write(filecontent);
                    filecontent.clear();
                } while (bytesRead1 >= 0);
                fis.close();
            }
        }catch (IOException e){
            System.out.println(e);
        }
    }

    public static void Download(){
        try {
            String str4 = "DOWNLOAD";
            ByteBuffer replyBuffer5 = ByteBuffer.wrap(str4.getBytes());
            socketChannel.write(replyBuffer5);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public static void Append(){
        try {
            String str3 = "APPEND";
            ByteBuffer replyBuffer4 = ByteBuffer.wrap(str3.getBytes());
            socketChannel.write(replyBuffer4);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void Rename(){
        try {
            String str2 = "File name?";
            ByteBuffer replyBuffer3 = ByteBuffer.wrap(str2.getBytes());
            socketChannel.write(replyBuffer3);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void List(){
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


    public SocketChannel socketChannel() throws IOException{
        return socketChannel;
    }
}
