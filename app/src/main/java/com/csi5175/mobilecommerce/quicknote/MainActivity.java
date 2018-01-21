package com.csi5175.mobilecommerce.quicknote;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity {
    ImageButton add;
    public ListView listView;
    private static final String DATABASE_NAME = "Mydb";
    private static final String TABLE_NAME = "notes";
    public static final String ID = "_id";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String PATH = "path";
    public static final String TIME = "time";
    private SQLiteDatabase sqlDB;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sqlDB = openOrCreateDatabase(DATABASE_NAME, SQLiteDatabase.CREATE_IF_NECESSARY,null);
        //Toast.makeText(getApplicationContext(),"db open successfully",Toast.LENGTH_SHORT).show();
        try {
            //sqlDB.execSQL("create table " + TABLE_NAME + " (_id text,title text  ,time text, context text)");
            sqlDB.execSQL("CREATE TABLE " + TABLE_NAME + " (" + ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT," + TITLE + " TEXT NOT NULL,"
                    + TIME+ " TEXT NOT NULL,"+ CONTENT + " TEXT NOT NULL,"
                    + PATH + " TEXT)");
        }catch(Exception e){
            // Toast.makeText(getApplicationContext(),"table existed",Toast.LENGTH_SHORT).show();
        }



        final Cursor c = sqlDB.query(TABLE_NAME, null, null, null, null, null, null);

        final SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(this,
                R.layout.listview_detail,
                c,
                new String[]{"title", "time"},
                new int[]{R.id.title1, R.id.time1},
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(listAdapter);

        listView.invalidateViews();


        //enter note detail page
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(getApplicationContext(),"aaaa",Toast.LENGTH_SHORT).show();
                Intent i = new Intent(getApplicationContext(),DetailNote.class);
                i.putExtra("title",c.getString(1));
                i.putExtra("time",c.getString(2));
                i.putExtra("context",c.getString(3));
                i.putExtra("path",c.getString(4));
                startActivity(i);
            }
        });





        //long click to delete
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {

                //Toast.makeText(getApplicationContext(),"Longpress",Toast.LENGTH_SHORT).show();

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("Prompt");
                builder.setMessage("Do you want to delete?");

                builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener(){
                    public void onClick (DialogInterface arg0,int arg1)
                    {
                        // Toast.makeText(getApplicationContext(),"cancel",Toast.LENGTH_SHORT).show();
                    }

                });

                builder.setPositiveButton("OK",new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface arg0,int arg1)
                    {
                        //Toast.makeText(getApplicationContext(),"ok",Toast.LENGTH_SHORT).show();

                        c.moveToPosition(position);
                        String title_name = c.getString(1);
                        sqlDB.delete(TABLE_NAME,"title=?",new String[]{title_name});
                        Intent i=new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(i);
                    }
                });

                builder.show();

                return true;
            }
        });



        add = (ImageButton) findViewById(R.id.imageButton);
        add.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(),AddNote.class);
                startActivity(i);
            }
        });

    }
}

