package com.tc.bu.account;

import java.io.Serializable;

public class AccountToProcess implements Serializable {
  private static final long serialVersionUID = -7941508770048039783L;
  private int accountNo;
  private String realBalance;

  public AccountToProcess() {
    // do nothing
  }

  public AccountToProcess(int accountno, String realBalance) {
    setAccountNo(accountno);
    setRealBalance(realBalance);
  }

  public int getAccountNo() {
    return accountNo;
  }

  public void setAccountNo(int accountNo) {
    this.accountNo = accountNo;
  }

  public String getRealBalance() {
    return realBalance;
  }

  public void setRealBalance(String realBalance) {
    this.realBalance = realBalance;
  }

  @Override
  public String toString() {
    return "AccountToCharge [accountno=" + accountNo + ", realBalance=" + realBalance + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + accountNo;
    result = prime * result + ((realBalance == null) ? 0 : realBalance.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    AccountToProcess other = (AccountToProcess) obj;
    if (accountNo != other.accountNo)
      return false;
    if (realBalance == null) {
      if (other.realBalance != null)
        return false;
    } else if (!realBalance.equals(other.realBalance))
      return false;
    return true;
  }

}
