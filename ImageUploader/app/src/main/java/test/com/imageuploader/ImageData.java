package test.com.imageuploader;

/**
 * Created by RAJA on 19-02-2016.
 */
public class ImageData {

    private String file_name;
    private String dt_stamp;
    private String img_base_text;
    public ImageData()
    {

    }

    public ImageData(String file_name, String dt_stamp, String img_base_text)
    {
        this.file_name = file_name;
        this.dt_stamp = dt_stamp;
        this.img_base_text = img_base_text;
    }

    public void setFileName(String fn)
    {
        this.file_name = fn;
    }
    public void setDtStamp(String ds)
    {
        this.dt_stamp = ds;
    }
    public void setImgBaseText(String imbt)
    {
        this.img_base_text = imbt;
    }
    public String getFileName(){
        return file_name;
    }
    public String getDtStamp(){
        return dt_stamp;
    }
    public String getImgBaseText(){
        return img_base_text;
    }
}
