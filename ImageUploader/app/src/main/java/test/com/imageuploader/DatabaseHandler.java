package test.com.imageuploader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by RAJA on 19-02-2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper{

    private static final String DATABASE_NAME="data";
    private static final int DATABASE_VERSION= 1;
    private static final String TABLE_IMAGE= "imgData";
    private static final String KEY_ID = "id";
    private static final String KEY_FILE_NAME = "fileName";
    private static final String KEY_TIME_STAMP = "timeStamp";
    private static final String KEY_IMG_BASE_TEXT = "imageText";


    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_IMAGE_TABLE = "CREATE TABLE " + TABLE_IMAGE + "("
                + KEY_ID + " INTEGER PRIMARY KEY, " + KEY_FILE_NAME + " TEXT, "
                + KEY_TIME_STAMP + " TEXT, " + KEY_IMG_BASE_TEXT + " TEXT" + ")";
        db.execSQL(CREATE_IMAGE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGE);

        // Create tables again
        onCreate(db);

    }

    public void insert(ImageData img_data)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_FILE_NAME, img_data.getFileName()); // Contact Name
        values.put(KEY_TIME_STAMP, img_data.getDtStamp()); // Contact Phone
        values.put(KEY_IMG_BASE_TEXT, img_data.getImgBaseText());

        // Inserting Row
        db.insert(TABLE_IMAGE, null, values);
        db.close();
    }
    public  ImageData getImageData(int id)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_IMAGE, new String[] { KEY_ID,
                        KEY_FILE_NAME, KEY_TIME_STAMP, KEY_IMG_BASE_TEXT  }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        ImageData imgData = new ImageData(cursor.getString(1),
                cursor.getString(2), cursor.getString(3));
        // return contact
        return imgData;
    }
    public List<ImageData> getAllImageData()
    {
        List<ImageData> imageList = new ArrayList<ImageData>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_IMAGE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                ImageData contact = new ImageData();
                contact.setFileName(cursor.getString(1));
                contact.setDtStamp(cursor.getString(2));
                contact.setImgBaseText(cursor.getString(3));
                // Adding contact to list
                imageList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return imageList;
    }
}
