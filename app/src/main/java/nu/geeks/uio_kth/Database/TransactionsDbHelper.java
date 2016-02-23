package nu.geeks.uio_kth.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import nu.geeks.uio_kth.Transaction;

/**
 *
 * Created by Hannes on 2016-02-20.
 */
public class TransactionsDbHelper extends SQLiteOpenHelper {


    private static final String DATABASE_NAME = "PROJECTCONTENT.DB";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG  = "Content DB";
    private static final String CREATE = "CREATE TABLE " + ProjectProperties.NewTransactionData.TABLE_NAME+
            "(" + ProjectProperties.NewTransactionData.PROJECT_ID + " TEXT," + ProjectProperties.NewTransactionData.PERSON + " TEXT," +
            ProjectProperties.NewTransactionData.amount + " TEXT," + ProjectProperties.NewTransactionData.object + " TEXT);";


    public TransactionsDbHelper(Context context) {
        super(context, DATABASE_NAME,null,DATABASE_VERSION);
        Log.e(TAG, "db created");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE);
        Log.e(TAG, "Table created with " + CREATE);
    }

    public void addInformation(Transaction transaction, SQLiteDatabase db){

        ContentValues contentValues = new ContentValues();

        contentValues.put(ProjectProperties.NewTransactionData.PROJECT_ID, transaction.projectId);
        contentValues.put(ProjectProperties.NewTransactionData.PERSON, transaction.person);
        contentValues.put(ProjectProperties.NewTransactionData.amount, transaction.amount);
        contentValues.put(ProjectProperties.NewTransactionData.object, transaction.object);
        db.insert(ProjectProperties.NewTransactionData.TABLE_NAME, null, contentValues);
        Log.e(TAG, "added new row");
    }

    public Cursor getInformation(SQLiteDatabase db)
    {
        String[] projections = {ProjectProperties.NewTransactionData.PROJECT_ID, ProjectProperties.NewTransactionData.PERSON, ProjectProperties.NewTransactionData.amount, ProjectProperties.NewTransactionData.object};
        return  db.query(ProjectProperties.NewTransactionData.TABLE_NAME, projections, null,null,null,null,null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
