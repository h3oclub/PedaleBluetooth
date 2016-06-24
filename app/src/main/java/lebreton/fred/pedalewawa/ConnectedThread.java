package lebreton.fred.pedalewawa;

/**
 * Created by Administrateur on 25/04/2016.
 */
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import com.joanzapata.pdfview.PDFView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class ConnectedThread extends Thread {
    private final BluetoothSocket Socket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private final Handler mHandler;
    private PDFView pdfView = null;
    private int currentPage =0;
    public ConnectedThread(BluetoothSocket socket,Handler handler, PDFView pdfView,int currentPage) {
      Socket = socket;
        mHandler = handler;
        this.pdfView = pdfView;
        this.currentPage = currentPage;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) { }
        mmInStream = tmpIn;
        mmOutStream = tmpOut;
    }
    public void run() {


        byte[] buffer = new byte[1024];
        int begin = 0;
        int bytes = 0;
        while (true) {
            try {
                bytes += mmInStream.read(buffer, bytes, buffer.length - bytes);
                for(int i = begin; i < bytes; i++) {
                    if(buffer[i] == "#".getBytes()[0]) {
                       Message msg = mHandler.obtainMessage(1,begin, i, buffer);
                        byte[] writeBuf = (byte[]) msg.obj;
                        int debut = (int)msg.arg1;
                        int fin = (int)msg.arg2;
                        String writeMessage = new String(writeBuf);
                        writeMessage = writeMessage.substring(debut, fin);
                        if (writeMessage.contains("suivant")){

                            if (currentPage > pdfView.getPageCount()){

                                currentPage = pdfView.getPageCount();
                            }
                            else {

                                currentPage++;
                            }

                        }
                        if (writeMessage.contains("precedent")){

                            if (currentPage < 1){

                                currentPage = 1;
                            }
                            else {

                                currentPage--;
                            }

                        }
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                pdfView.jumpTo(currentPage);
                            }
                        });
                        mHandler.sendMessage(msg);
                        begin = i + 1;
                        if(i == bytes - 1) {
                            bytes = 0;
                            begin = 0;
                        }
                    }
                }
            } catch (IOException e) {
                break;
            }
        }
    }
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);
        } catch
                (IOException e) { }
    }
    public void cancel() {
        try {
            Socket.close();
        } catch (IOException e) { }
    }
}