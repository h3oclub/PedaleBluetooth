package lebreton.fred.pedalewawa;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.net.Socket;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private Button connection;
    private Button explorer;
    private Button commencer;
    private Button deconnecte;
    private TextView textConnection;
    private TextView textExplore;
    private BluetoothDevice pedale = null;
    private int PICKFILE_RESULT_CODE=1;
    private String src = "";

// Recupère l'etat de la connection et affche dans un textView
    final Handler handlerStatus = new Handler() {
        public void handleMessage(Message msg) {
            int co = msg.arg1;
            if (co == 1) {
                textConnection.setText("");
                textConnection.setText("Connecté");
            } else if (co == 2) {
                textConnection.setText("");
                textConnection.append("Déconnecté");
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connection = (Button)findViewById(R.id.Connection);
        deconnecte = (Button) findViewById(R.id.Déconnection);
        explorer = (Button)findViewById(R.id.Explorateur);
        commencer = (Button)findViewById(R.id.go);
        textExplore = (TextView) findViewById(R.id.textExplorateur);
        textConnection = (TextView)findViewById(R.id.textConnection);



// clic sur bouton "Partition ?"
        explorer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
                chooseFile.setType("application/pdf");
                startActivityForResult(Intent.createChooser(chooseFile, "Choose a file"),PICKFILE_RESULT_CODE);

            }
        });

// clic sur bouton "Commençer"
        final Intent i = new Intent(this,activite2.class);
        commencer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(textConnection.getText()=="Connecté" && src != "" ) {
                    i.putExtra("file", src);
                    startActivity(i);
                }
                else{
                    if(textConnection.getText()!="Connecté") {

                        Toast.makeText(getApplicationContext(), "Se connecter avant de commençer", Toast.LENGTH_LONG).show();
                    }
                    else{

                        Toast.makeText(getApplicationContext(), "Choisir un fichier avant de commençer", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


// Activation du bluetooth sur telephone
       final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (!bluetoothAdapter.isEnabled()) {
            bluetoothAdapter.enable();
        }
// Recherche de la pedale dans liste des devices déjà apparaillés
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice devicePaired : pairedDevices) {
                if (devicePaired.getAddress().contains("98:D3:31:70:16:A8")) {
                    pedale = devicePaired;
                } else {
                    Log.d("pas de pédale decteté", "");
                }
            }

        }


// Clic sur bouton "Connecter"
       final ConnectThread ConnectThread = new ConnectThread(bluetoothAdapter, handlerStatus, pedale);
        connection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectThread.start();
            }

        });

// Clic sur bouton "Déconnecter"
        deconnecte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               ConnectThread.cancel();
            }
        });


    }

// Résultat de l'intent choix fichier
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICKFILE_RESULT_CODE && resultCode == Activity.RESULT_OK) {
            Uri content_describer = data.getData();
            src = content_describer.getPath();

            textExplore.setText("");
            textExplore.setText(""+src);
        }
    }



}

