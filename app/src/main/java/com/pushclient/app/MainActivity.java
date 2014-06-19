package com.pushclient.app;

import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentProvider;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.pushclient.app.R;

public class MainActivity extends ListActivity implements AdapterView.OnItemClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.mainscreen);

        ContentValues cv = new ContentValues();
        cv.put(MessageProviderMetaData.MessageTableMetaData.INVOICE_NAME, "Oil change");
        cv.put(MessageProviderMetaData.MessageTableMetaData.INVOICE_SUBMIT, "19 June 2014");
        cv.put(MessageProviderMetaData.MessageTableMetaData.INVOICE_AMOUNT, "100");

        ContentResolver cr = getApplicationContext().getContentResolver();
        Uri uri = MessageProviderMetaData.MessageTableMetaData.CONTENT_URI;
        Uri insertedUri = cr.insert(uri, cv);

        Cursor cursor = cr.query(MessageProviderMetaData.MessageTableMetaData.CONTENT_URI,
                        null,
                        null,
                        null,
                        MessageProviderMetaData.MessageTableMetaData.DEFAULT_SORT_ORDER);

        String[] cols = new String[]{MessageProviderMetaData.MessageTableMetaData.INVOICE_NAME};
        int[] views = new int[] {android.R.id.text2};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, cols, views);
        this.setListAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Uri selectedMessage = ContentUris.withAppendedId(MessageProviderMetaData.MessageTableMetaData.CONTENT_URI, id);
        startActivity(new Intent(Intent.ACTION_VIEW, selectedMessage));
    }
}
