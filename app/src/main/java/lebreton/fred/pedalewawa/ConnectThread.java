package lebreton.fred.pedalewawa;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import java.io.IOException;
import java.util.UUID;


public class ConnectThread extends Thread {

    private  BluetoothSocket Socket = null;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter blueAdapter = null;
    private Handler hStatut = null;



    public ConnectThread (BluetoothAdapter adapter, Handler handler, BluetoothDevice pedaleWawa){
        blueAdapter =  adapter;

        hStatut = handler;

        BluetoothSocket tmp = null;

        try
        {
            tmp = pedaleWawa.createRfcommSocketToServiceRecord(MY_UUID);
        }
        catch(IOException e) { }
        Socket = tmp;
    }
    public void run() {
       blueAdapter.cancelDiscovery();
        try
        {
            Socket.connect();

            Message msg = hStatut.obtainMessage();
            msg.arg1 = 1;
            hStatut.sendMessage(msg);


        }
        catch
                (IOException connectException) {
            try
            {
                Socket.close();
            }
            catch
                    (IOException closeException) { }
            return;
        }
    }
    public void cancel() {
        try
        {
            Socket.close();
            Message msg = hStatut.obtainMessage();
            msg.arg1 = 2;
            hStatut.sendMessage(msg);
        }
        catch
                (IOException e) { }
    }
}
