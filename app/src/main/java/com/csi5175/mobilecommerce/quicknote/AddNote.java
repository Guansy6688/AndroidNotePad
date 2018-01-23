package com.csi5175.mobilecommerce.quicknote;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 1/16/2018.
 */

public class AddNote extends AppCompatActivity {
    EditText title;
    EditText context;
    Button save;
    TextView date;
    ImageView img;
    ImageButton imgbtn;
    Button back;
    File phoneFile;


    private static final String DATABASE_NAME = "Mydb";
    private static final String TABLE_NAME = "notes";
    private SQLiteDatabase sqlDB;
    public static final String ID = "_id";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String PATH = "path";
    public static final String TIME = "time";

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_add);


        sqlDB = openOrCreateDatabase(DATABASE_NAME, SQLiteDatabase.CREATE_IF_NECESSARY,null);
        //Toast.makeText(getApplicationContext(),"db open successfully",Toast.LENGTH_SHORT).show();
        try {
          //  sqlDB.execSQL("create table " + TABLE_NAME + " (_id text ,title text  ,weather text ,time text, context text)");
            sqlDB.execSQL("CREATE TABLE " + TABLE_NAME + " (" + ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT," + TITLE + " TEXT NOT NULL,"
                    + TIME+ " TEXT NOT NULL,"+ CONTENT + " TEXT NOT NULL,"
                    + PATH + " TEXT)");
        }catch(Exception e){
            // Toast.makeText(getApplicationContext(),"表格已经存在",Toast.LENGTH_SHORT).show();
        }


        date=(TextView)findViewById(R.id.textView);
        title = (EditText)findViewById(R.id.editText6);
        context=(EditText)findViewById(R.id.editText7);
        img = (ImageView)findViewById(R.id.imageView1) ;
        save = (Button)findViewById(R.id.button5);

        Intent i = getIntent();
        final String idString = i.getStringExtra(ID);
        Toast.makeText(getApplicationContext(),idString,Toast.LENGTH_SHORT).show();

        if(i.getStringExtra(TITLE)!=null){
            String titleString = i.getStringExtra(TITLE);
//    String timec = i.getStringExtra(TIME);
            String contextString = i.getStringExtra(CONTENT);
            title.setText(titleString);
            context.setText(contextString);

            if (i.getStringExtra(PATH)==null) {
                img.setVisibility(View.GONE);
            } else {
                img.setVisibility(View.VISIBLE);
                Bitmap bitmap = BitmapFactory.decodeFile(i.getStringExtra(
                        PATH));
                img.setImageBitmap(bitmap);
            }
        }

        date.setText(getTime());
 //       Toast.makeText(getApplicationContext(),t,Toast.LENGTH_SHORT).show();



        imgbtn = (ImageButton)findViewById(R.id.imageButton3);
        imgbtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                img.setVisibility(View.VISIBLE);

                //                Intent imgIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                phoneFile = new File(Environment.getExternalStorageDirectory()
//                        .getAbsoluteFile() + "/" + getTime() + ".jpg");
//                imgIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(phoneFile));
//                startActivityForResult(imgIntent, 1);

                Intent imgIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                if (imgIntent.resolveActivity(getPackageManager()) != null) {

                    String filename = getTime() + ".jpg";
                    phoneFile= new File(Environment.getExternalStorageDirectory(), filename);

                    Uri fileUri = FileProvider7.getUriForFile(getApplicationContext(), phoneFile);

                    List<ResolveInfo> resInfoList = getPackageManager()
                            .queryIntentActivities(imgIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        grantUriPermission(packageName, fileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    }

                    Toast.makeText(getApplicationContext(),fileUri.getPath(),Toast.LENGTH_SHORT).show();

                    imgIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    startActivityForResult(imgIntent, 1);
                }



            }
        });



        back=(Button)findViewById(R.id.button6);
        back.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });

        save.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!title.getText().toString().equals("") && !context.getText().toString().equals("")) {

                    String titleString = title.getText().toString();
                    String timeString = date.getText().toString();
                    String contextString = context.getText().toString();

                    //title text ,time text, context text
                    ContentValues cv = new ContentValues();
                    cv.put(TITLE, titleString);
                    cv.put(TIME, timeString);
                    cv.put(CONTENT, contextString);
                    cv.put(PATH, phoneFile + "");

                    if(idString==null) {
                        sqlDB.insertOrThrow(TABLE_NAME,null,cv);
                    }
                    else {
                        sqlDB.update(TABLE_NAME, cv, ID + " = "+idString,null);
                    }
                    //Toast.makeText(getApplicationContext(),"save successful,you can go back now!",Toast.LENGTH_SHORT).show();
                    Intent i= new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(i);

                }else{
                    Toast.makeText(getApplicationContext(),"title or context can not empty",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private String getTime() {
        //get Time
        long time=System.currentTimeMillis();
        SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd-HHmmss");
        Date d1=new Date(time);
        String t=format.format(d1);
        return t;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            Toast.makeText(getApplicationContext(),phoneFile.getAbsolutePath(),Toast.LENGTH_SHORT).show();

            Bitmap bitmap = BitmapFactory.decodeFile(phoneFile
                    .getAbsolutePath());
            img.setImageBitmap(bitmap);
        }
    }
}
