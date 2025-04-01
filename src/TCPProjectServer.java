import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.file.*;
import java.util.Scanner;
import java.util.concurrent.*;

public class TCPProjectServer {
    private static final String FILEPATH = "Server Files/";
    private static final int PORT = 1068;
    private static boolean isRunning = true;
    private static ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(PORT));
        System.out.println("Server is running on port " + PORT);

        //accept loop in separate thread
        Thread acceptThread = new Thread(() -> {
            while (isRunning) {
                try {
                    SocketChannel clientChannel = serverChannel.accept();
                    if (clientChannel != null) {
                        System.out.println("Accepted connection from: " + clientChannel.getRemoteAddress());
                        handleClient(clientChannel);
                    }
                } catch (IOException e) {
                    if (isRunning) e.printStackTrace();
                }
            }
        });

        acceptThread.start();

        //admin thread for shutdown
        Scanner adminScanner = new Scanner(System.in);
        while (isRunning) {
            String input = adminScanner.nextLine();
            if (input.equalsIgnoreCase("Q")) {
                System.out.println("Shutting down server...");
                isRunning = false;
                serverChannel.close();
                threadPool.shutdown();
                break;
            }
        }
    }

    private static void handleClient(SocketChannel clientChannel) {
        Thread clientThread = new Thread(() -> {
            try (SocketChannel channel = clientChannel) {
                ByteBuffer buffer = ByteBuffer.allocate(1024);

                while (isRunning && channel.isConnected()) {
                    buffer.clear();
                    int bytesRead = channel.read(buffer);
                    if (bytesRead == -1) break;
                    buffer.flip();
                    byte[] commandBytes = new byte[bytesRead];
                    buffer.get(commandBytes);
                    String command = new String(commandBytes).trim();

                    switch (command) {
                        case "U":
                            new UploadTask(channel).run();
                            break;
                        case "D":
                            new DownloadTask(channel).run();
                            break;
                        case "L":
                            sendFileList(channel);
                            break;
                        case "R":
                            handleDelete(channel);
                            break;
                        case "A":
                            handleRename(channel);
                            break;
                        case "E":
                            sendMessage(channel, "CLOSING CONNECTION");
                            return;
                        default:
                            sendMessage(channel, "Invalid command");
                    }
                }
            } catch (IOException e) {
                System.out.println("Client disconnected.");
            }
        });
        clientThread.start();
    }

    private static void sendMessage(SocketChannel channel, String message) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes());
        channel.write(buffer);
    }

    private static void sendFileList(SocketChannel channel) throws IOException {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(FILEPATH))) {
            StringBuilder list = new StringBuilder();
            for (Path entry : stream) {
                if (Files.isRegularFile(entry)) {
                    list.append(entry.getFileName().toString()).append(", ");
                }
            }
            if (list.length() > 0) list.setLength(list.length() - 2);
            sendMessage(channel, list.toString());
        }
    }

    private static void handleDelete(SocketChannel channel) throws IOException {
        sendMessage(channel, "File name?");
        String fileName = receiveMessage(channel);
        Path filePath = Paths.get(FILEPATH, fileName);
        if (Files.exists(filePath)) {
            Files.delete(filePath);
            sendMessage(channel, "File deleted successfully");
        } else {
            sendMessage(channel, "File not found");
        }
    }

    private static void handleRename(SocketChannel channel) throws IOException {
        sendMessage(channel, "File name?");
        String oldName = receiveMessage(channel);
        sendMessage(channel, "Insert New Name:");
        String newName = receiveMessage(channel);
        Path oldPath = Paths.get(FILEPATH, oldName);
        Path newPath = Paths.get(FILEPATH, newName);
        if (Files.exists(oldPath)) {
            Files.move(oldPath, newPath, StandardCopyOption.REPLACE_EXISTING);
            sendMessage(channel, "File renamed successfully");
        } else {
            sendMessage(channel, "File not found");
        }
    }

    private static String receiveMessage(SocketChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int bytesRead = channel.read(buffer);
        if (bytesRead == -1) throw new IOException("Client closed connection");
        buffer.flip();
        byte[] data = new byte[bytesRead];
        buffer.get(data);
        return new String(data).trim();
    }

    static class UploadTask implements Runnable {
        private final SocketChannel channel;

        public UploadTask(SocketChannel channel) {
            this.channel = channel;
        }

        public void run() {
            try {
                sendMessage(channel, "Enter file name:");
                String fileName = receiveMessage(channel);

                ByteBuffer sizeBuffer = ByteBuffer.allocate(8);
                channel.read(sizeBuffer);
                sizeBuffer.flip();
                long fileSize = sizeBuffer.getLong();

                Path filePath = Paths.get(FILEPATH, fileName);
                try (FileChannel fileOut = FileChannel.open(filePath, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    long bytesReceived = 0;
                    while (bytesReceived < fileSize) {
                        buffer.clear();
                        int read = channel.read(buffer);
                        if (read == -1) break;
                        bytesReceived += read;
                        buffer.flip();
                        fileOut.write(buffer);
                    }
                }
                sendMessage(channel, "File uploaded successfully");
            } catch (IOException e) {
                System.err.println("Upload failed: " + e);
            }
        }
    }

    static class DownloadTask implements Runnable {
        private final SocketChannel channel;

        public DownloadTask(SocketChannel channel) {
            this.channel = channel;
        }

        public void run() {
            try {
                sendMessage(channel, "File name for download?");
                String fileName = receiveMessage(channel);
                Path filePath = Paths.get(FILEPATH, fileName);
                if (!Files.exists(filePath)) {
                    ByteBuffer errBuffer = ByteBuffer.allocate(8);
                    errBuffer.putLong(-1).flip();
                    channel.write(errBuffer);
                    return;
                }

                long fileSize = Files.size(filePath);
                ByteBuffer sizeBuffer = ByteBuffer.allocate(8);
                sizeBuffer.putLong(fileSize).flip();
                channel.write(sizeBuffer);

                try (FileChannel fileChannel = FileChannel.open(filePath, StandardOpenOption.READ)) {
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    int bytesRead;
                    while ((bytesRead = fileChannel.read(buffer)) != -1) {
                        buffer.flip();
                        channel.write(buffer);
                        buffer.clear();
                    }
                }
            } catch (IOException e) {
                System.err.println("Download failed: " + e.getMessage());
            }
        }
    }
}

