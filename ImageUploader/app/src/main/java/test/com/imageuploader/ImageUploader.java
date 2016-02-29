package test.com.imageuploader;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by RAJA on 17-02-2016.
 */
public class ImageUploader extends Service{

    private int serverResponseCode = 0;
    private NotificationManager notificationManager;
    private int count=1;
    private int total = 0;


    public boolean isRunning=false;

    @Override
    public void onCreate() {

        notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        isRunning=true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        new ImageUploaderAt().execute("/sdcard/test", "http://requestb.in/zab2eeza");
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        isRunning=false;
    }

    private class ImageUploaderAt extends AsyncTask<String, String, String>
    {
        //ProgressDialog pd;

        @Override
        protected void onPreExecute()
        {
            Log.i("ILUD", "started");
        }

        @Override
        protected String doInBackground(String... data)
        {
            String file_path = data[0];
            String url = data[1];
            List<ImageData> img_data;
            DatabaseHandler db = new DatabaseHandler(getApplicationContext());
            img_data = db.getAllImageData();
            List<String> file_names = new ArrayList<>();
            for(ImageData img_dt: img_data)
            {
                file_names.add(img_dt.getFileName());
            }
            List<String> img_file_paths = getListFiles(new File(file_path));
            List<String> new_file_paths = new ArrayList<>();
            int size = img_file_paths.size();
            for (int i = 0; i < size; i++) {
                if (!file_names.contains(new File(img_file_paths.get(i)).getName())) {
                    new_file_paths.add(img_file_paths.get(i));
                }
            }
            total = new_file_paths.size();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            Calendar c = Calendar.getInstance();
            for(String img_path: new_file_paths)
            {
                uploadFile(img_path, url);
                String file_name = new File(img_path).getName();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(img_path, options);
                String img_text = bitmapToString(scaleBitmap(bitmap, 80, 80));
                new DatabaseHandler(getApplicationContext()).insert(new ImageData(file_name, df.format(c.getTime()), img_text));
                Intent RTRetur = new Intent("com.imageuploader.IMAGEDATA");
                RTRetur.putExtra("filename", file_name);
                RTRetur.putExtra("date", df.format(c.getTime()));
                RTRetur.putExtra("imgstring", img_text);
                sendBroadcast(RTRetur);

                count+=1;
                //Broadcast send for updating the listview dynamically and
                //add the new file name to the database
            }
            //stopService();
            return null;
        }

        @Override
        public void onProgressUpdate(String... args)
        {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ImageUploader.this);
            mBuilder.setSmallIcon(R.mipmap.ic_launcher);
            mBuilder.setContentTitle("Uploading:" + args[0]);
            mBuilder.setContentText("Uploaded : " + args[1]+"% " + String.valueOf(count) + "of" + String.valueOf(total));
            notificationManager.notify(1, mBuilder.build());
        }
        @Override
        protected void onPostExecute(String result)
        {
            stopService(new Intent(ImageUploader.this, ImageUploader.class));

        }
        private Bitmap scaleBitmap(Bitmap bitmap, int wantedWidth, int wantedHeight) {
            Bitmap output = Bitmap.createBitmap(wantedWidth, wantedHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);
            Matrix m = new Matrix();
            m.setScale((float) wantedWidth / bitmap.getWidth(), (float) wantedHeight / bitmap.getHeight());
            canvas.drawBitmap(bitmap, m, new Paint());

            return output;
        }
        private String bitmapToString(Bitmap bitmap)
        {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream .toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        }
        private List<String> getListFiles(File parentDir) {
            ArrayList<String> inFiles = new ArrayList<String>();
            File[] files = parentDir.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    continue;
                } else {
                    if(file.getName().endsWith(".jpg")){
                        inFiles.add(file.getPath());
                    }
                }
            }
            return inFiles;
        }
        public int uploadFile(String sourceFileUri, String upLoadServerUri) {


            String fileName = sourceFileUri;

            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize, max_size;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            File sourceFile = new File(sourceFileUri);

                try {


                    FileInputStream fileInputStream = new FileInputStream(sourceFile);
                    URL url = new URL(upLoadServerUri);


                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("uploaded_file", fileName);

                    dos = new DataOutputStream(conn.getOutputStream());

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=" + fileName + lineEnd);

                    dos.writeBytes(lineEnd);
                    bytesAvailable = fileInputStream.available();
                    max_size = bytesAvailable;
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {

                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        float percentage_complete = ((max_size-bytesAvailable)/max_size) * 100;
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                        Log.d("Percentage", String.valueOf(percentage_complete));
                        publishProgress(sourceFile.getName(),String.valueOf(percentage_complete));

                    }
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                    serverResponseCode = conn.getResponseCode();
                    String serverResponseMessage = conn.getResponseMessage();

                    Log.i("uploadFile", "HTTP Response is : "
                            + serverResponseMessage + ": " + serverResponseCode);

                    if(serverResponseCode == 200){
                        Log.i("uploadFile", "HTTP Response is : "
                                + serverResponseMessage + ": " + serverResponseCode);

                    }

                    fileInputStream.close();
                    dos.flush();
                    dos.close();

                } catch (MalformedURLException ex) {

                    Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
                } catch (Exception e) {
                    Log.e("Upload file Exception", "Exception : "
                            + e.getMessage(), e);
                }
                return serverResponseCode;

        }
    }
}
