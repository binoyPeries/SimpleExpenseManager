package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.database.DbHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {
    private final DbHelper dbHelper;

    public PersistentAccountDAO(DbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }


    @Override
    public List<String> getAccountNumbersList() {
        List<String> accountNos = new ArrayList<>();
        List<Account> accounts = dbHelper.getAllAccounts();
        if(accounts.size()>0){
            for(Account a:accounts){
                accountNos.add(a.getAccountNo());
            }
            return accountNos;
        }
        return accountNos;

    }

    @Override
    public List<Account> getAccountsList() {
        return dbHelper.getAllAccounts();
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        return dbHelper.getSingleAccount(accountNo);
    }

    @Override
    public void addAccount(Account account) {
        dbHelper.addAccount(account);


    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        dbHelper.deleteAccount(accountNo);

    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        if(accountNo == null){
            throw new InvalidAccountException("Account number not valid");

        }
        Account account = dbHelper.getSingleAccount(accountNo);
        double balance = account.getBalance();
        if(expenseType == ExpenseType.INCOME){
            account.setBalance(balance+amount);
        }else if (expenseType == ExpenseType.EXPENSE){
            account.setBalance(balance-amount);

        }
        if(account.getBalance()<0 ){
            throw new InvalidAccountException("Insufficient credits to withdraw");
        }

        else{
            dbHelper.updateAccount(account);
        }


    }
}
