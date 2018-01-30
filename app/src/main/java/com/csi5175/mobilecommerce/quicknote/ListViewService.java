package com.csi5175.mobilecommerce.quicknote;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of ListView Service.
 */

public class ListViewService extends RemoteViewsService {
    public static final String INITENT_DATA = "extra_data";
    private static final String DATABASE_NAME = "Mydb";
    private SQLiteDatabase sqlDB;
    private static final String TABLE_NAME = "notes";
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    private class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
        private Context mContext;
        private List<String> titleList = new ArrayList<>();
        private List<String> contentList = new ArrayList<>();

        public ListRemoteViewsFactory(Context context, Intent intent) {
            mContext = context;
        }
        @SuppressLint("WrongConstant")
        @Override
        public void onCreate() {
            // Create or open database
            sqlDB = openOrCreateDatabase(DATABASE_NAME, SQLiteDatabase.CREATE_IF_NECESSARY,null);
            Cursor c = sqlDB.query(TABLE_NAME, null, null, null, null, null, null);
            while(c.moveToNext()){
                // Initialize lists
                titleList.add(c.getString(c.getColumnIndex("title")));
                contentList.add(c.getString(c.getColumnIndex("content")));
            }
        }

        @SuppressLint("WrongConstant")
        @Override
        public void onDataSetChanged() {
            titleList.clear();
            contentList.clear();
            sqlDB = openOrCreateDatabase(DATABASE_NAME, SQLiteDatabase.CREATE_IF_NECESSARY,null);
            Cursor c = sqlDB.query(TABLE_NAME, null, null, null, null, null, null);
            while(c.moveToNext()){
                // Update lists
                titleList.add(c.getString(c.getColumnIndex("title")));
                contentList.add(c.getString(c.getColumnIndex("content")));
            }
        }

        @Override
        public void onDestroy() {
            titleList.clear();
            contentList.clear();
            sqlDB.close();
        }

        @Override
        public int getCount() {
            return titleList.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews views = new RemoteViews(mContext.getPackageName(), android.R.layout.simple_list_item_1);
            views.setTextViewText(android.R.id.text1, titleList.get(position));

            Bundle extras = new Bundle();
            extras.putString(ListViewService.INITENT_DATA, contentList.get(position));
            Intent changeIntent = new Intent();
            changeIntent.setAction(NewAppWidget.CHANGE_IMAGE);
            changeIntent.putExtras(extras);

            views.setOnClickFillInIntent(android.R.id.text1, changeIntent);
            return views;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
