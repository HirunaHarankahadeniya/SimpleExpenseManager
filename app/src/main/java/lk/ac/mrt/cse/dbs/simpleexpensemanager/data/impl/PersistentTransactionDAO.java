/*
 * Copyright 2015 Department of Computer Science and Engineering, University of Moratuwa.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *                  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.DbHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * This is an In-Memory implementation of TransactionDAO interface. This is not a persistent storage. All the
 * transaction logs are stored in a LinkedList in memory.
 */
public class PersistentTransactionDAO extends DbHelper implements TransactionDAO {
    private List<Transaction> transactions;

    public PersistentTransactionDAO(Context context) {
        super(context);
        this.transactions = new ArrayList<Transaction>();
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
        String strDate = dateFormat.format(date);

        cv.put(COLUMN_ACCOUNT_NUMBER, accountNo);
        cv.put(COLUMN_DATE, strDate);
        cv.put(COLUMN_AMOUNT, amount);
        cv.put(COLUMN_TYPE, String.valueOf(expenseType));

        db.insert(TRANSACTIONS_TABLE, null, cv);
        db.close();
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        transactions = new ArrayList<Transaction>();
        SQLiteDatabase db = this.getReadableDatabase();
        String querySQL = "SELECT * FROM " + TRANSACTIONS_TABLE + ";";
        Cursor csr = db.rawQuery(querySQL, null);

        if(csr.moveToFirst()){
            do {
                Date date = null;
                try {
                    date = new SimpleDateFormat("yyyy-mm-dd").parse(csr.getString(1));
                } catch (Exception e) {
                }
                String accountNo = csr.getString(2);
                ExpenseType expenseType = ExpenseType.valueOf(csr.getString(3).toUpperCase(Locale.ROOT));
                double amount = csr.getInt(4);

                Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
                transactions.add(transaction);
            }while(csr.moveToNext());
        }

        csr.close();
        db.close();
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        transactions = getAllTransactionLogs();

        int size = transactions.size();
        if (size <= limit) {
            return transactions;
        }
        // return the last <code>limit</code> number of transaction logs
        return transactions.subList(size - limit, size);
    }

}
