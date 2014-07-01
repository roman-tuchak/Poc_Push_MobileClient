package com.pushclient.app;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

import static com.pushclient.app.MessageProviderMetaData.*;


public class MessageInfoActivity extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_info);
        View buttonDelete = findViewById(R.id.button_delete);
        buttonDelete.setOnClickListener(this);
        View buttonOk = findViewById(R.id.button_ok);
        buttonOk.setOnClickListener(this);

        Cursor cursor = null;


        try {
            cursor = getContentResolver().query(getIntent().getData(), null, null, null, null);
            TextView messInfo = (TextView) findViewById(R.id.message_info);
            messInfo.setText(fillTextView(cursor));
        }
        finally {
            if (!cursor.isClosed()) cursor.close();
        }

    }

    private String fillTextView(Cursor cursor) {

        StringBuilder message = new StringBuilder();
        int name = cursor.getColumnIndex(MessageTableMetaData.INVOICE_NAME);
        int amount = cursor.getColumnIndex(MessageTableMetaData.INVOICE_AMOUNT);
        int submit = cursor.getColumnIndex(MessageTableMetaData.INVOICE_SUBMIT);
        int complete = cursor.getColumnIndex(MessageTableMetaData.INVOICE_COMPLETE);
        int predict = cursor.getColumnIndex(MessageTableMetaData.INVOICE_PREDICT);
        int create = cursor.getColumnIndex(MessageTableMetaData.CREATED_DATE);
        int modify = cursor.getColumnIndex(MessageTableMetaData.MODIFIED_DATE);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            message.append("Invoice name:   ").append(cursor.getString(name)).append("\n")
                   .append("Invoice amount: ").append(cursor.getString(amount)).append("\n")
                   .append("Submit date:    ").append(cursor.getString(submit)).append("\n")
                   .append("Complete date:  ").append(cursor.getString(complete)).append("\n")
                   .append("Predicted date: ").append(cursor.getString(predict)).append("\n")
                   .append("Received: ").append(new Date(new Long(cursor.getString(create)))).append("\n")
                   .append("Modified: ").append(new Date(new Long(cursor.getString(modify)))).append("\n");
        }
        return message.toString();
    }

    private void onOk(){
        NavUtils.navigateUpFromSameTask(this);
    }

    private void onDelete() {
        getContentResolver().delete(getIntent().getData(), null, null);
        NavUtils.navigateUpFromSameTask(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_ok) onOk();
        if (v.getId() == R.id.button_delete) onDelete();
    }
}
