package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;


public class DbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "180471r.db";
    public static final int DATABASE_VERSION = 4;

        //tables
    private static final String TABLE_ACC = "account";
    private static final String TABLE_TRANS = "transactions";

    //account table cols
    private static final String ACC_COL_accountNo = "accountNo";
    private static final String ACC_COL_bankName = "bankName";
    private static final String ACC_COL_accountHolderName = "accountHolderName";
    private static final String ACC_COL_balance = "balance";

    //transaction table cols

    private static final String TRANS_COL_date = "date";
    private static final String TRANS_COL_accountNo = "accountNo";
    private static final String TRANS_COL_expenseType = "expenseType";
    private static final String TRANS_COL_amount = "amount";

    // account create table statement

    private static final String CREATE_TABLE_ACC= "CREATE TABLE " + TABLE_ACC
            + "(" + ACC_COL_accountNo + " TEXT(50) PRIMARY KEY," + ACC_COL_bankName + " TEXT(50),"
            +ACC_COL_accountHolderName +" TEXT(30)," + ACC_COL_balance + " REAL" + ")";

    // transaction create table statement

    private static final String CREATE_TABLE_TRANS= "CREATE TABLE " + TABLE_TRANS
            + "(" + TRANS_COL_date + " DATE," + TRANS_COL_accountNo + " TEXT(50),"
            +TRANS_COL_expenseType +" TEXT(30)," + TRANS_COL_amount + " REAL" + ", FOREIGN KEY (" +TRANS_COL_accountNo +") REFERENCES "+ TABLE_ACC + "(" + ACC_COL_accountNo +"))";
    private final Context context;


    public DbHelper(Context context) {
        
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ACC);
        db.execSQL(CREATE_TABLE_TRANS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_ACC);
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_TRANS);
        onCreate(db);

    }



    //**************queries related to account table********************

    public  Boolean addAccount(Account acc){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ACC_COL_accountNo,acc.getAccountNo());
        values.put(ACC_COL_bankName,acc.getBankName());
        values.put(ACC_COL_accountHolderName,acc.getAccountHolderName());
        values.put(ACC_COL_balance,acc.getBalance());


        long res = db.insert(TABLE_ACC,null , values);
        if(res==-1){
           return false;
        }
        return true;


    }

    public Boolean updateAccount(Account acc){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ACC_COL_accountNo,acc.getAccountNo());
        values.put(ACC_COL_bankName,acc.getBankName());
        values.put(ACC_COL_accountHolderName,acc.getAccountHolderName());
        values.put(ACC_COL_balance,acc.getBalance());

        long res = db.update(TABLE_ACC, values, ACC_COL_accountNo +"=?",new String[]{acc.getAccountNo()} );
        if(res==-1){
            return false;
        }
        return true;

    }

    public  List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_ACC, null);

        if (res.getCount() == 0) {
            return accounts;
        }
        while (res.moveToNext()) {
            String accountNo = res.getString(0);
            String bankName = res.getString(1);
            String accountHolderName = res.getString(2);
            double balance = res.getDouble(3);
            Account temp = new Account(accountNo, bankName, accountHolderName, balance);
            accounts.add(temp);

        }
        return accounts;

    }

    public Account getSingleAccount(String acc){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+TABLE_ACC+" WHERE "+ ACC_COL_accountNo +" =?",new String[]{acc});
        Account account = null;
        if (res.getCount() != 0) {
            while (res.moveToNext()) {
                String accountNo = res.getString(0);
                String bankName = res.getString(1);
                String accountHolderName = res.getString(2);
                double balance = res.getDouble(3);
                account = new Account(accountNo, bankName, accountHolderName, balance);
            }
        }
        return account;
    }

    public boolean deleteAccount(String accNO){
        SQLiteDatabase db = this.getWritableDatabase();
        long res = db.delete(TABLE_ACC, ACC_COL_accountNo+"= "+accNO,null);
        if (res> 0){
            return true;
        }
        return false;

    }


    // **********************transaction table related queries***********************

    public boolean enterTransaction(Transaction transaction){

        DateFormat format = new SimpleDateFormat("m-d-yyyy", Locale.ENGLISH);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        System.out.println(transaction.getDate());
        values.put(TRANS_COL_date,format.format(transaction.getDate()));
        values.put(TRANS_COL_accountNo,transaction.getAccountNo());
        values.put(TRANS_COL_expenseType,transaction.getExpenseType().toString());
        values.put(TRANS_COL_amount,transaction.getAmount());


        long res = db.insert(TABLE_TRANS,null,values);
        if(res == -1){
            return false;
        }
        return true;
    }

    public  List<Transaction> getAllTransactions() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+TABLE_TRANS,null);

        List<Transaction> transactions=new ArrayList<>();
        DateFormat format = new SimpleDateFormat("m-d-yyyy", Locale.ENGLISH);
        if (res.getCount() >= 0) {

            while (res.moveToNext()) {
                String accountNo = res.getString(1);
                ExpenseType expenseType = ExpenseType.valueOf(res.getString(2));
                Date date = new Date();
                try {
                    date = format.parse(res.getString(0));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                double amount = res.getDouble(3);
                transactions.add(new Transaction(date, accountNo, expenseType, amount));
            }
        }
        return transactions;


    }

    public  List<Transaction> getAllTransactionsLimited(int limit)  {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM "+ TABLE_TRANS + " LIMIT " + limit,null);

        List<Transaction> transactions=new ArrayList<>();
        DateFormat format = new SimpleDateFormat("m-d-yyyy", Locale.ENGLISH);
        if (res.getCount() >= 0) {

            while (res.moveToNext()) {
                System.out.print(res);
                String accountNo = res.getString(1);
                ExpenseType expenseType = ExpenseType.valueOf(res.getString(2));
                Date date = new Date();
                try {
                    date = format.parse(res.getString(0));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                double amount = res.getDouble(3);
                transactions.add(new Transaction(date, accountNo, expenseType, amount));
            }
        }
        return transactions;

    }













}
