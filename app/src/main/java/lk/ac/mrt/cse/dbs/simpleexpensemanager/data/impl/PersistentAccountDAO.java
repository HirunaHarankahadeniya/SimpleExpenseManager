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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.DbHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

/**
 * This is an In-Memory implementation of the AccountDAO interface. This is not a persistent storage. A HashMap is
 * used to store the account details temporarily in the memory.
 */
public class PersistentAccountDAO extends DbHelper implements AccountDAO {
    private List<Account> accountsList;
    private List<String> accountNumbersList;

    public PersistentAccountDAO(Context context) {
        super(context);
        this.accountsList = new ArrayList<Account>();
        this.accountNumbersList = new ArrayList<String>();
    }

    @Override
    public List<String> getAccountNumbersList() {
        this.accountNumbersList = new ArrayList<String>();
        SQLiteDatabase db=this.getReadableDatabase();
        String querySql = "SELECT " + COLUMN_ACCOUNT_NUMBER + " FROM " + ACCOUNTS_TABLE +";";
        Cursor csr = db.rawQuery(querySql,null);

        if(csr.moveToFirst()) {
            do {
                String accountNo = csr.getString(0);
                this.accountNumbersList.add(accountNo);
            } while (csr.moveToNext());
        }

        csr.close();
        db.close();
        return this.accountNumbersList;
    }

    @Override
    public List<Account> getAccountsList() {
        this.accountsList = new ArrayList<Account>();
        SQLiteDatabase db=this.getReadableDatabase();
        String querySql = "SELECT * FROM " + ACCOUNTS_TABLE + ";";
        Cursor csr = db.rawQuery(querySql,null);

        if(csr.moveToFirst()) {
            do {
                String accountNo = csr.getString(1);
                String bankName = csr.getString(2);
                String accountHolder = csr.getString(3);
                double accountBalance = csr.getInt(4);
                Account account = new Account(accountNo, bankName, accountHolder, accountBalance);
                this.accountsList.add(account);
            } while (csr.moveToNext());
        }

        csr.close();
        db.close();
        return this.accountsList;

    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {

        SQLiteDatabase db=this.getReadableDatabase();
        String querySql = "SELECT * FROM " + ACCOUNTS_TABLE + " WHERE " + COLUMN_ACCOUNT_NUMBER + "= '" + accountNo +"' ;";
        Cursor csr = db.rawQuery(querySql,null);

        String bankName = csr.getString(1);
        String accountHolder = csr.getString(2);
        double accountBalance = csr.getInt(3);
        Account account = null;

        if (csr.moveToFirst()) {
            account = new Account(accountNo, bankName, accountHolder, accountBalance);
        }
        else {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        csr.close();
        db.close();
        return account;
    }

    @Override
    public void addAccount(Account account) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_ACCOUNT_NUMBER, account.getAccountNo());
        cv.put(COLUMN_BANK_NAME, account.getBankName());
        cv.put(COLUMN_ACCOUNT_HOLDER, account.getAccountHolderName());
        cv.put(COLUMN_ACCOUNT_BALANCE, account.getBalance());

        db.insert(ACCOUNTS_TABLE, null, cv);
        db.close();
        //accounts.put(account.getAccountNo(), account);
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {

        SQLiteDatabase db = this.getWritableDatabase();
        String selection = COLUMN_ACCOUNT_NUMBER + " = ?";
        String[] selectArgs = {accountNo};
        int deletedRows = db.delete(ACCOUNTS_TABLE, selection,selectArgs);

        if (deletedRows == 0) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        db.close();
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {

        SQLiteDatabase db=this.getWritableDatabase();
        String readQuerySql = "SELECT " + COLUMN_ACCOUNT_BALANCE+ " FROM " + ACCOUNTS_TABLE + " WHERE " + COLUMN_ACCOUNT_NUMBER + "= '" + accountNo +"' ;";
        Cursor csr = db.rawQuery(readQuerySql,null);
        double accountBalance;
        if(csr.moveToFirst()){
            accountBalance = csr.getDouble(0);
            switch(expenseType) {
                case EXPENSE:
                    accountBalance -= amount;
                    break;
                case INCOME:
                    accountBalance += amount;
                    break;
            }
        }else{
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }

        String updateQuerySql = "UPDATE " + ACCOUNTS_TABLE + " SET " + COLUMN_ACCOUNT_BALANCE + "= " + accountBalance + " WHERE " + COLUMN_ACCOUNT_NUMBER + "= '" + accountNo +"' ;";
        db.execSQL(updateQuerySql);
        csr.close();;
        db.close();

    }
}
