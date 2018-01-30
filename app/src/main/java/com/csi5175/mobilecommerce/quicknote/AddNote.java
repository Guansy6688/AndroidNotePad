package com.csi5175.mobilecommerce.quicknote;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

/**
 * Implementation of App Note.
 */

public class AddNote extends AppCompatActivity {
    EditText title;
    EditText context;
    Button save;
    TextView date;
    ImageView img;
    Button back;
    PopupMenu popupMenu;
    Menu menu;
    File phoneFile;
    Uri uri;
    ImageButton ib;
    ShareDialog fbShareDialog;

    private static final String DATABASE_NAME = "Mydb";
    private static final String TABLE_NAME = "notes";
    private SQLiteDatabase sqlDB;
    public static final String ID = "_id";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String PATH = "path";
    public static final String TIME = "time";
    public static final Integer CONTENT_MAXLINE = 18;
    public static final Integer CONTENT_MINLINE = 3;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_add);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        // Create or open database
        sqlDB = openOrCreateDatabase(DATABASE_NAME, SQLiteDatabase.CREATE_IF_NECESSARY,null);
        try {
            sqlDB.execSQL("CREATE TABLE " + TABLE_NAME + " (" + ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT," + TITLE + " TEXT NOT NULL,"
                    + TIME+ " TEXT NOT NULL,"+ CONTENT + " TEXT NOT NULL,"
                    + PATH + " TEXT)");
        }catch(Exception e){
        }

        date=(TextView)findViewById(R.id.textView);
        title = (EditText)findViewById(R.id.editText6);
        context=(EditText)findViewById(R.id.editText7);
        img = (ImageView)findViewById(R.id.imageView1) ;
        save = (Button)findViewById(R.id.button5);

        // Show note details
        Intent i = getIntent();
        final String idString = i.getStringExtra(ID);
        if(i.getStringExtra(TITLE)!=null){
            String titleString = i.getStringExtra(TITLE);
            String contextString = i.getStringExtra(CONTENT);
            title.setText(titleString);
            context.setText(contextString);

            if (i.getStringExtra(PATH).equals("null")) {
                img.setVisibility(View.GONE);
                context.setMaxLines(CONTENT_MAXLINE);
            } else {
                img.setVisibility(View.VISIBLE);
                Bitmap bitmap = BitmapFactory.decodeFile(i.getStringExtra(PATH));
                img.setImageBitmap(bitmap);
                context.setMaxLines(CONTENT_MINLINE);
            }
        }
        date.setText(getTime());

        // Facebook Share on QuickNote
        ib = (ImageButton) findViewById(R.id.share_button);
        fbShareDialog = new ShareDialog(this);
        ib.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(fbShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setQuote(title.getText().toString() + "\n\n" + context.getText().toString())
                            .setContentUrl(Uri.parse("https://developers.facebook.com/docs/sharing/opengraph/android"))
                            .setImageUrl(Uri.parse("https://www.cabq.gov/culturalservices/biopark/images/share-on-facebook.png"))
                            .build();
                    fbShareDialog.show(linkContent);
                }
            }
        });

        // Set popup menu for camera button
        popupMenu = new PopupMenu(this, findViewById(R.id.imageButton3));
        menu = popupMenu.getMenu();
        menu.add(Menu.NONE,Menu.FIRST + 0, 0 , "choose from gallery");
        menu.add(Menu.NONE,Menu.FIRST + 1, 1 , "take a picture");
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case Menu.FIRST + 0:
                    //choose a picture from gallery
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, 2);
                    break;

                case Menu.FIRST + 1:
                    //take a picture
                    Intent imgIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    phoneFile = new File(Environment.getExternalStorageDirectory()
                            .getAbsoluteFile() + "/" + getImgTime() + ".jpg");
                    ContentValues contentValues = new ContentValues(1);
                    contentValues.put(MediaStore.Images.Media.DATA, phoneFile.getAbsolutePath());

                    if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                            PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(AddNote.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                    }

                    uri = getApplication().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                    List<ResolveInfo> resInfoList = getPackageManager()
                            .queryIntentActivities(imgIntent, PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        grantUriPermission(packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                                | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    }
                    imgIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(imgIntent, 1);
                    break;

                default:
                    break;
            }
            return false;
            }
        });

        // Delete image confirmation
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddNote.this);

                builder.setTitle("Warning");
                builder.setMessage("Do you want to delete this picture?");
                builder.setPositiveButton("OK",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface arg0,int arg1)
                    {
                        img.setVisibility(View.GONE);
                        context.setMaxLines(CONTENT_MAXLINE);
                        if(phoneFile!=null){
                            if(phoneFile.exists()){
                                phoneFile.delete();
                            }
                        }
                    }
                });

                builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener(){
                    public void onClick (DialogInterface arg0,int arg1)
                    {
                    }

                });
                builder.show();
            }
        });

        // Back button
        back=(Button)findViewById(R.id.button6);
        back.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });

        // Save button
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

                    if(img.getVisibility()==View.VISIBLE && phoneFile == null){

                    }else{
                        cv.put(PATH, phoneFile + "");
                    }

                    if(idString==null) {
                        sqlDB.insertOrThrow(TABLE_NAME,null,cv);
                    }
                    else {
                        sqlDB.update(TABLE_NAME, cv, ID + " = "+idString,null);
                    }
                    Intent i= new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(i);
                }
                else{
                    Toast.makeText(getApplicationContext(),"title or content can not empty",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Get current system time for notes
    private String getTime() {
        //get Time
        long time=System.currentTimeMillis();
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d1=new Date(time);
        String t=format.format(d1);
        return t;
    }

    // Get current system time for notes
    private String getImgTime() {
        //get Img Time
        long time=System.currentTimeMillis();
        SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd-HHmmss");
        Date d1=new Date(time);
        String t=format.format(d1);
        return t;
    }

    // Show popup menu
    public void popupmenu(View v) {
        popupMenu.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            //camera
            case 1:
                if(resultCode == RESULT_OK){
                    img.setVisibility(View.VISIBLE);
                    context.setMaxLines(3);
                    ShowImage(phoneFile.getAbsolutePath());
                }
                break;

            //gallery
            case 2:
                if(resultCode == RESULT_OK && data != null){
                    Uri selectedImage = data.getData();
                    String[] filePathColumns = {MediaStore.Images.Media.DATA};
                    Cursor imgcursor = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
                    imgcursor.moveToFirst();
                    int columnIndex = imgcursor.getColumnIndex(filePathColumns[0]);
                    String imagePath = imgcursor.getString(columnIndex);
                    ShowImage(imagePath);
                }
                break;
        }
    }

    //show image according to path
    private void ShowImage(String imgPath){
        Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
        img.setImageBitmap(bitmap);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        sqlDB.close();
    }
}
