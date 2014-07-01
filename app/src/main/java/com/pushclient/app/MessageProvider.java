package com.pushclient.app;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.sql.SQLException;
import java.util.HashMap;

import static com.pushclient.app.MessageProviderMetaData.MESSAGES_TABLE_NAME;
import static com.pushclient.app.MessageProviderMetaData.MessageTableMetaData;

public class MessageProvider extends ContentProvider {
    public MessageProvider() {
    }

    private static HashMap<String, String> sMessagesProjectionMap;
    static {
        sMessagesProjectionMap = new HashMap<String, String>();
        sMessagesProjectionMap.put(MessageTableMetaData._ID,
                                   MessageTableMetaData._ID);
        sMessagesProjectionMap.put(MessageTableMetaData.INVOICE_NAME,
                                   MessageTableMetaData.INVOICE_NAME);
        sMessagesProjectionMap.put(MessageTableMetaData.INVOICE_AMOUNT,
                                   MessageTableMetaData.INVOICE_AMOUNT);
        sMessagesProjectionMap.put(MessageTableMetaData.INVOICE_SUBMIT,
                                   MessageTableMetaData.INVOICE_SUBMIT);
        sMessagesProjectionMap.put(MessageTableMetaData.INVOICE_COMPLETE,
                                   MessageTableMetaData.INVOICE_COMPLETE);
        sMessagesProjectionMap.put(MessageTableMetaData.INVOICE_PREDICT,
                                   MessageTableMetaData.INVOICE_PREDICT);
        sMessagesProjectionMap.put(MessageTableMetaData.CREATED_DATE,
                                   MessageTableMetaData.CREATED_DATE);
        sMessagesProjectionMap.put(MessageTableMetaData.MODIFIED_DATE,
                                   MessageTableMetaData.MODIFIED_DATE);
    }

    private static final UriMatcher sUriMatcher;
    private static final int INCOMING_MESSAGE_COLLECTION_URI_INDICATOR = 1;
    private static final int INCOMING_SINGLE_MESSAGE_URI_INDICATOR = 2;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(MessageProviderMetaData.AUTHORITY,
                           "messages",
                           INCOMING_MESSAGE_COLLECTION_URI_INDICATOR);
        sUriMatcher.addURI(MessageProviderMetaData.AUTHORITY,
                           "messages/#",
                           INCOMING_SINGLE_MESSAGE_URI_INDICATOR);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context,
                  MessageProviderMetaData.DATABASE_NAME, null,
                  MessageProviderMetaData.DATABASE_VERSION
            );
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + MessageTableMetaData.TABLE_NAME + " ("
            + MessageTableMetaData._ID + " INTEGER PRIMARY KEY, "
            + MessageTableMetaData.INVOICE_NAME + " TEXT, "
            + MessageTableMetaData.INVOICE_AMOUNT + " TEXT, "
            + MessageTableMetaData.INVOICE_SUBMIT + " TEXT, "
            + MessageTableMetaData.INVOICE_COMPLETE + " TEXT, "
            + MessageTableMetaData.INVOICE_PREDICT + " TEXT, "
            + MessageTableMetaData.CREATED_DATE + " INTEGER, "
            + MessageTableMetaData.MODIFIED_DATE + " INTEGER"
            + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int i, int i2) {
            db.execSQL("DROP TABLE IF EXIST "
                + MessageTableMetaData.TABLE_NAME);
            onCreate(db);
        }
    }

    private DatabaseHelper mOpenHelper;

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        // Implement this to handle requests to delete one or more rows.
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case INCOMING_MESSAGE_COLLECTION_URI_INDICATOR:
                count = db.delete(MessageTableMetaData.TABLE_NAME, where, whereArgs);
                break;
            case INCOMING_SINGLE_MESSAGE_URI_INDICATOR:
                String rowId = uri.getPathSegments().get(1);
                count = db.delete(MessageTableMetaData.TABLE_NAME,
                                  MessageTableMetaData._ID + "=" + rowId
                                          + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""),
                                  whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case INCOMING_MESSAGE_COLLECTION_URI_INDICATOR:
                return MessageTableMetaData.CONTENT_TYPE;
            case INCOMING_SINGLE_MESSAGE_URI_INDICATOR:
                return MessageTableMetaData.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);

        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        if (sUriMatcher.match(uri) != INCOMING_MESSAGE_COLLECTION_URI_INDICATOR)
            throw new IllegalArgumentException("Unknown URI " + uri);

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        Long currentTime = System.currentTimeMillis();

        //Values check
        if (!values.containsKey(MessageTableMetaData.CREATED_DATE))
            values.put(MessageTableMetaData.CREATED_DATE, currentTime);
        if (!values.containsKey(MessageTableMetaData.MODIFIED_DATE))
            values.put(MessageTableMetaData.MODIFIED_DATE, currentTime);
        if (!values.containsKey(MessageTableMetaData.INVOICE_NAME))
            throw new IllegalArgumentException("Failed to insert row because Invoice name needed " + uri);
        if (!values.containsKey(MessageTableMetaData.INVOICE_AMOUNT))
            values.put(MessageTableMetaData.INVOICE_AMOUNT, "Unknown amount");
        if (!values.containsKey(MessageTableMetaData.INVOICE_SUBMIT))
            values.put(MessageTableMetaData.INVOICE_SUBMIT, "Unknown date");
        if (!values.containsKey(MessageTableMetaData.INVOICE_COMPLETE))
            values.put(MessageTableMetaData.INVOICE_COMPLETE, "Unknown date");
        if (!values.containsKey(MessageTableMetaData.INVOICE_PREDICT))
            values.put(MessageTableMetaData.INVOICE_PREDICT, "Unknown date");

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long rowId = db.insert(MessageTableMetaData.TABLE_NAME,
                             MessageTableMetaData.INVOICE_NAME,
                             values);

        if (rowId > 0) {
            Uri insertedMessageUri = ContentUris.withAppendedId(MessageTableMetaData.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(insertedMessageUri, null);
            return insertedMessageUri;
        }
        throw new RuntimeException("Failed insert new row into " + uri);
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String where,
            String[] whereArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)) {
            case INCOMING_MESSAGE_COLLECTION_URI_INDICATOR:
                queryBuilder.setTables(MessageTableMetaData.TABLE_NAME);
                queryBuilder.setProjectionMap(sMessagesProjectionMap);
                break;
            case INCOMING_SINGLE_MESSAGE_URI_INDICATOR:
                queryBuilder.setTables(MessageTableMetaData.TABLE_NAME);
                queryBuilder.setProjectionMap(sMessagesProjectionMap);
                queryBuilder.appendWhere(MessageTableMetaData._ID + "=" + uri.getPathSegments().get(1));
                break;
            default: throw new IllegalArgumentException("Unknown URI " + uri);
        }

        String orderBy;
        if (TextUtils.isEmpty(sortOrder)) {
            orderBy = MessageTableMetaData.DEFAULT_SORT_ORDER;
        } else {
            orderBy = sortOrder;
        }

        return queryBuilder.query(mOpenHelper.getReadableDatabase(),
                                  projection,
                                  where,
                                  whereArgs,
                                  null,
                                  null,
                                  orderBy);
    }

    @Override
    public int update(Uri uri, ContentValues values, String where,
            String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;

        switch (sUriMatcher.match(uri)) {
            case INCOMING_MESSAGE_COLLECTION_URI_INDICATOR:
                count = db.update(MessageTableMetaData.TABLE_NAME,
                                  values,
                                  where,
                                  whereArgs);
                break;
            case INCOMING_SINGLE_MESSAGE_URI_INDICATOR:
                String rowId = uri.getPathSegments().get(1);
                count = db.update(MessageTableMetaData.TABLE_NAME,
                                  values,
                                  MessageTableMetaData._ID + "=" + rowId
                                          + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""),
                                  whereArgs);
                break;
            default: throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
