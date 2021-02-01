package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.exception.ExpenseManagerException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database.DbHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;

public class PersistentExpressManager extends  ExpenseManager{
    private final Context context;
    public PersistentExpressManager(Context con)  {
        this.context= con;
        try {
            setup();
        } catch (ExpenseManagerException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setup() throws ExpenseManagerException {
        DbHelper dbHelper = new DbHelper(context);

        TransactionDAO transDAO = new PersistentTransactionDAO(dbHelper) ;
        setTransactionsDAO(transDAO);

        AccountDAO perAccountDAO = new PersistentAccountDAO(dbHelper);
        setAccountsDAO(perAccountDAO);


    }
}
