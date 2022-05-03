package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {

    public static final String ACCOUNTS_TABLE = "Accounts_Table";
    public static final String COLUMN_ACCOUNT_NUMBER = "accountNumber";
    public static final String COLUMN_BANK_NAME = "bankName";
    public static final String COLUMN_ACCOUNT_HOLDER = "accountHolder";
    public static final String COLUMN_ACCOUNT_BALANCE = "accountBalance";
    public static final String TRANSACTIONS_TABLE = "Transactions_Table";
    public static final String COLUMN_TRANSACTION_NUMBER = "transactionNumber";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_AMOUNT = "amount";

    public DbHelper(@Nullable Context context) {
        super(context, "190215x.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createAccountsTableStatement = "CREATE TABLE " + ACCOUNTS_TABLE + "(" + COLUMN_ACCOUNT_NUMBER + " text PRIMARY KEY, " + COLUMN_BANK_NAME + " text, " + COLUMN_ACCOUNT_HOLDER + " text, " + COLUMN_ACCOUNT_BALANCE + " real);";
        sqLiteDatabase.execSQL(createAccountsTableStatement);
        String createTransactionsTableStatement = "CREATE TABLE " + TRANSACTIONS_TABLE + "(" + COLUMN_TRANSACTION_NUMBER + " integer PRIMARY KEY AUTOINCREMENT, " + COLUMN_DATE + " text, " + COLUMN_ACCOUNT_NUMBER + " text, " + COLUMN_TYPE + " text, " + COLUMN_AMOUNT + " real);";
        sqLiteDatabase.execSQL(createTransactionsTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }

}
