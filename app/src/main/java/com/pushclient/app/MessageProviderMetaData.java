package com.pushclient.app;


import android.net.Uri;
import android.provider.BaseColumns;

public class MessageProviderMetaData {
    public static final String AUTHORITY = "com.pushclient.app.MessageProvider";
    public static final String DATABASE_NAME = "pushmessages.db";
    public static final int DATABASE_VERSION = 1;
    public static final String MESSAGES_TABLE_NAME = "messages";

    private MessageProviderMetaData() {}

    public static final class MessageTableMetaData implements BaseColumns {

        private MessageTableMetaData() {}

        public static final String TABLE_NAME = "messages";
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/messages");
        public static final String DEFAULT_SORT_ORDER = "modified DESC";
        public static final String INVOICE_NAME = "name";
        public static final String INVOICE_AMOUNT = "amount";
        public static final String INVOICE_SUBMIT = "submit";
        public static final String INVOICE_COMPLETE = "complete";
        public static final String INVOICE_PREDICT = "predict";
        public static final String CREATED_DATE = "created";
        public static final String MODIFIED_DATE = "modified";
    }
}
