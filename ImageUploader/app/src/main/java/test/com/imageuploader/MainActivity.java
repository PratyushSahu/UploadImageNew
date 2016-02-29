package test.com.imageuploader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String PATH = "/sdcard/test";
    private ListView lv;
    private TextView tv;
    private List<ImageData> img_data;
    private CustomListViewAdapter adapter;

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //do something based on the intent's action
            tv.setVisibility(View.INVISIBLE);
            Log.d("ILUD", "Receiver is Working");
            Bundle b = intent.getExtras();
            img_data.add(new ImageData(b.getString("filename"), b.getString("date"), b.getString("imgstring")));
            adapter= new CustomListViewAdapter(MainActivity.this, img_data);
            lv.setAdapter(adapter);
        }
    };;
    IntentFilter filter ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get data from android db and show it in tyhe listview
//        if(isDbAvailable())
//        {
//            //Get data and then show in lv
//        }
//        else {
//            //Create Database
//            createDb();
//        }


        lv= (ListView)findViewById(R.id.lv_history);
        tv = (TextView)findViewById(R.id.tv_status);

        filter = new IntentFilter();
        filter.addAction("com.imageuploader.IMAGEDATA");
        //filter.addAction("SOME_OTHER_ACTION");



        DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        img_data = db.getAllImageData();
        adapter = new CustomListViewAdapter(this, img_data);
        if(img_data.isEmpty())
        {
             tv.setVisibility(View.VISIBLE);
        }
        else
        {
            lv.setAdapter(adapter);
        }

        registerReceiver(receiver, filter);

        if (isNetworkAvailable(this)) {
            //Start Service
            Intent i = new Intent(this, ImageUploader.class);
            startService(i);
            Log.i("ILUD", "started intent");

        } else {
            Toast.makeText(this, "Please get Connected to Internet.", Toast.LENGTH_SHORT).show();
        }

    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
    private void createDb()
    {

    }
    private boolean isDbAvailable()
    {
        return false;
    }
}
