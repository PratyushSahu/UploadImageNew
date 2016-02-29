package test.com.imageuploader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by RAJA on 19-02-2016.
 */
public class CustomListViewAdapter extends ArrayAdapter<ImageData> {

    private Context context;
    private List<ImageData> values;

    public CustomListViewAdapter(Context context, List<ImageData> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.lv_row, parent, false);
        TextView tv_fname = (TextView) rowView.findViewById(R.id.tv_filename);
        TextView tv_dtstamp = (TextView) rowView.findViewById(R.id.tv_dtstamp);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.iv_cache);
        tv_fname.setText(values.get(position).getFileName());
        tv_dtstamp.setText(values.get(position).getDtStamp());
        //tv_fname.setText(values[position].getFileName());
        // change the icon for Windows and iPhone
        imageView.setImageBitmap(stringToBitmap(values.get(position).getImgBaseText()));
        return rowView;
    }
    private Bitmap stringToBitmap(String str)
    {
        byte[] decodedString = Base64.decode(str, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }
}
