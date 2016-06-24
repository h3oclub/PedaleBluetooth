package lebreton.fred.pedalewawa;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnPageChangeListener;
import java.io.File;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class activite2 extends AppCompatActivity  implements OnPageChangeListener{

    private PDFView pdfView;
    public static final String SAMPLE_FILE = "partition.pdf";
    private TextView textView;
    private TextView textMessage;
    private Context ctx;
    public int currentPage = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activite2);


        //ConnectedThread ConnectedThread = new ConnectedThread(mmSocket);
        //ConnectedThread.start();

        Intent intent = getIntent();
        String chemin = intent.getStringExtra("file");
        File file = new File(chemin);


        ctx = this;
        pdfView = (PDFView) findViewById(R.id.pdfView);
        pdfView.fromFile(file)
                .defaultPage(currentPage)
                .onPageChange((activite2) ctx)
                .load();






    }

    @Override
    public void onPageChanged(int arg0, int arg1) {

        Log.d("onPageChanged", arg0 + " " + arg1);
    }




}

