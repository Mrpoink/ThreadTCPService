import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.sleep;

public class TCPProjectClient {
    private static final String FILEPATH = "Client Files/";
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(4);

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: java TCPProjectClient <serverIP> <port>");
            return;
        }

        SocketChannel channel = SocketChannel.open();
        channel.connect(new InetSocketAddress(args[0], Integer.parseInt(args[1])));
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nCommands: L (List), R (Remove), A (Rename), D (Download), U (Upload), E (Exit)");
            System.out.print("Enter command: ");
            String command = scanner.nextLine();

            if (command.equals("E")) {
                send(channel, command);
                System.out.println(receive(channel));
                break;
            } else if (command.equals("D")) {
                System.out.print("Enter file name to download: ");
                String fileName = scanner.nextLine();
                threadPool.submit(() -> downloadFile(channel, fileName));
            } else if (command.equals("U")) {
                System.out.print("Enter file name to upload: ");
                String fileName = scanner.nextLine();
                threadPool.submit(() -> uploadFile(channel, fileName));
            } else {
                send(channel, command);
                System.out.println(receive(channel));

                if (command.equals("R") || command.equals("A")) {
                    System.out.print("Enter file name: ");
                    String oldName = scanner.nextLine();
                    send(channel, oldName);
                    System.out.println(receive(channel));

                    if (command.equals("A")) {
                        System.out.print("Enter new name: ");
                        String newName = scanner.nextLine();
                        send(channel, newName);
                    }

                    System.out.println(receive(channel));
                }
            }
        }

        threadPool.shutdown();
        channel.close();
        scanner.close();
    }

    private static void send(SocketChannel channel, String msg) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());
            channel.write(buffer);
        } catch (IOException e) {
            System.err.println("Send failed: " + e.getMessage());
        }
    }

    private static String receive(SocketChannel channel) {
        try {
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            int bytesRead = channel.read(buffer);
            if (bytesRead == -1) return "Connection closed.";
            buffer.flip();
            return new String(buffer.array(), 0, bytesRead);
        } catch (IOException e) {
            return "Receive failed: " + e.getMessage();
        }
    }

    private static void downloadFile(SocketChannel channel, String fileName) {
        try {
            send(channel, "D");
            System.out.println(receive(channel)); // prompt
            send(channel, fileName);

            ByteBuffer sizeBuffer = ByteBuffer.allocate(8);
            channel.read(sizeBuffer);
            sizeBuffer.flip();
            long fileSize = sizeBuffer.getLong();

            if (fileSize == -1) {
                System.out.println("File not found.");
                return;
            }

            FileOutputStream fos = new FileOutputStream(FILEPATH + fileName);
            FileChannel outChannel = fos.getChannel();
            ByteBuffer fileBuffer = ByteBuffer.allocate(1024);
            long bytesReceived = 0;

            while (bytesReceived < fileSize) {
                fileBuffer.clear();
                int bytesRead = channel.read(fileBuffer);
                if (bytesRead == -1) break;
                bytesReceived += bytesRead;
                fileBuffer.flip();
                outChannel.write(fileBuffer);
            }

            outChannel.close();
            fos.close();
            System.out.println("Download complete: " + fileName);
        } catch (IOException e) {
            System.err.println("Download failed: " + e);
        }
    }

    private static void uploadFile(SocketChannel channel, String fileName) {
        try {
            //Thread.sleep(10000);                      //ITS HERE, THE THING IS HERE GUYS I FOUND IT
            File file = new File(FILEPATH + fileName);
            if (!file.exists()) {
                System.out.println("File does not exist.");
                return;
            }

            send(channel, "U");
            System.out.println(receive(channel)); // prompt
            send(channel, fileName);

            long fileSize = file.length();
            ByteBuffer sizeBuffer = ByteBuffer.allocate(8);
            sizeBuffer.putLong(fileSize);
            sizeBuffer.flip();
            channel.write(sizeBuffer);

            FileInputStream fis = new FileInputStream(file);
            FileChannel inChannel = fis.getChannel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            long bytesSent = 0;

            while (bytesSent < fileSize) {

                buffer.clear();
                int bytesRead = inChannel.read(buffer);
                if (bytesRead == -1) break;
                bytesSent += bytesRead;
                buffer.flip();
                channel.write(buffer);
            }

            inChannel.close();
            fis.close();
            System.out.println(receive(channel)); // upload success msg
        } catch (IOException e) {
            System.err.println("Upload failed: " + e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}