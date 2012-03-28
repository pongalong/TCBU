package com.tc.bu;

import java.util.ArrayList;
import java.util.List;

import com.tc.bu.account.AccountToProcess;
import com.tc.bu.exception.CustomerException;

/**
 * Used to charge accounts manually.
 * 
 * @author Tachikoma
 * 
 */
public class Tester extends TruConnectBackend {
  private static Tester tester = new Tester();

  public static void main(String[] args) {
    System.out.println(">> Processing started...");
    List<AccountToProcess> accounts = Tester.getAccountToChargeList();
    System.out.println(">> Charging the following " + accounts.size() + " accounts:\n");
    printAccountInfo(accounts);
    System.out.println();
    // tester.chargeAccounts(accounts);
  }

  private static void printAccountInfo(List<AccountToProcess> accounts) {
    com.tscp.mvne.Account mvneAcc;
    try {
      for (AccountToProcess account : accounts) {
        mvneAcc = tester.getAccount(account.getAccountNo());
        System.out.println(mvneAcc.getAccountno() + "\t\t" + mvneAcc.getBalance() + "\t\t" + mvneAcc.getContactEmail());
      }
    } catch (CustomerException e) {
      e.printStackTrace();
    }
  }

  private static List<AccountToProcess> getAccountList_20111202() {
    List<AccountToProcess> accountsToCharge = new ArrayList<AccountToProcess>();
    accountsToCharge.add(new AccountToProcess(698951, "-1.8605"));
    accountsToCharge.add(new AccountToProcess(699861, "3.59"));
    accountsToCharge.add(new AccountToProcess(700849, "-5.3"));
    accountsToCharge.add(new AccountToProcess(701655, "-0.22"));
    accountsToCharge.add(new AccountToProcess(703711, "-1.1867"));
    accountsToCharge.add(new AccountToProcess(703893, "5.62"));
    accountsToCharge.add(new AccountToProcess(704194, "-0.32"));
    accountsToCharge.add(new AccountToProcess(706998, "-1.98"));
    accountsToCharge.add(new AccountToProcess(707002, "-0.56"));
    accountsToCharge.add(new AccountToProcess(707014, "-1.43"));
    accountsToCharge.add(new AccountToProcess(707060, "-1.28"));
    accountsToCharge.add(new AccountToProcess(707083, "-0.79"));
    accountsToCharge.add(new AccountToProcess(707120, "2.59"));
    accountsToCharge.add(new AccountToProcess(707193, "-1.47"));
    accountsToCharge.add(new AccountToProcess(707194, "-1.75"));
    accountsToCharge.add(new AccountToProcess(707199, "-1.17"));
    accountsToCharge.add(new AccountToProcess(707250, "0.45"));
    accountsToCharge.add(new AccountToProcess(707273, "-0.25"));
    accountsToCharge.add(new AccountToProcess(707283, "2.02"));
    accountsToCharge.add(new AccountToProcess(707284, "0.5"));
    accountsToCharge.add(new AccountToProcess(707287, "-0.01"));
    accountsToCharge.add(new AccountToProcess(707341, "1.42"));
    accountsToCharge.add(new AccountToProcess(707404, "1.16"));
    accountsToCharge.add(new AccountToProcess(707409, "-0.07"));
    accountsToCharge.add(new AccountToProcess(707414, "-1.67"));
    accountsToCharge.add(new AccountToProcess(707772, "-3.56"));
    accountsToCharge.add(new AccountToProcess(707880, "-1.6551"));
    accountsToCharge.add(new AccountToProcess(708193, "-1.76"));
    accountsToCharge.add(new AccountToProcess(708231, "-4.0738"));
    accountsToCharge.add(new AccountToProcess(708286, "-0.7423"));
    return accountsToCharge;
  }

  private static List<AccountToProcess> getAccountList_20120301() {
    List<AccountToProcess> accountsToCharge = new ArrayList<AccountToProcess>();
    accountsToCharge.add(new AccountToProcess(719807, "-27.9766"));
    return accountsToCharge;
  }

  private static List<AccountToProcess> getAccountList_20120306() {
    List<AccountToProcess> accountsToCharge = new ArrayList<AccountToProcess>();
    accountsToCharge.add(new AccountToProcess(716435, "-47.54"));
    return accountsToCharge;
  }

  private static List<AccountToProcess> getAccountList_20120321() {
    List<AccountToProcess> accountsToCharge = new ArrayList<AccountToProcess>();
    accountsToCharge.add(new AccountToProcess(707940, "-11.11"));
    return accountsToCharge;
  }
}
