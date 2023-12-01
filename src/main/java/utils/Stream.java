package utils;

import java.io.*;
import java.net.Socket;

public class Stream {

    public BufferedReader bufferedReader;
//    public BufferedWriter bufferedWriter;
    public PrintWriter printWriter;


    public Stream(Socket socket) throws IOException {

        this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.printWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
    }

    public void closeStreams() throws IOException {
//        bufferedWriter.close();
        printWriter.close();
        bufferedReader.close();
    }

}
