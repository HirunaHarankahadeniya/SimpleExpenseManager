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

package lk.ac.mrt.cse.dbs.simpleexpensemanager;



import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.test.ApplicationTestCase;


import androidx.test.core.app.ApplicationProvider;

import org.junit.BeforeClass;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.ExpenseManager;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.PersistentDemoExpenseManager;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest{

    private static ExpenseManager expenseManager;

    @BeforeClass
    public static void  create(){
        Context context = ApplicationProvider.getApplicationContext();
        expenseManager = new PersistentDemoExpenseManager(context);
    }

    @Test
    public void addAccountTest(){
        expenseManager.getAccountsDAO().addAccount(new Account("1902w","BOC","Hiruna", 100));
        assertTrue(expenseManager.getAccountNumbersList().contains("1902w"));
    }

    @Test
    public void addTransactionTest(){
        int before_size = expenseManager.getTransactionLogs().size();
        try {
            expenseManager.updateAccountBalance("12345A",21, 6, 2022, ExpenseType.EXPENSE, "1000");
        } catch (InvalidAccountException e) {
            e.printStackTrace();
        }
        int after_size  = expenseManager.getTransactionLogs().size();
        assertTrue(before_size<after_size);
    }
}