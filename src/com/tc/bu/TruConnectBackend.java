package com.tc.bu;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.ws.WebServiceException;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tc.bu.account.AccountToProcess;
import com.tc.bu.exception.CustomerException;
import com.tc.bu.exception.PaymentException;
import com.tc.bu.hibernate.HibernateUtil;
import com.tc.bu.util.email.EmailHelper;
import com.tscp.mvne.Account;
import com.tscp.mvne.CreditCard;
import com.tscp.mvne.CustPmtMap;
import com.tscp.mvne.CustTopUp;
import com.tscp.mvne.Customer;
import com.tscp.mvne.PaymentUnitResponse;
import com.tscp.mvne.TruConnect;
import com.tscp.mvne.TruConnectService;

public class TruConnectBackend {
  private static final String wsdl = "http://10.10.30.188:8080/TSCPMVNE/TruConnectService?WSDL";
  private static final String nameSpace = "http://mvne.tscp.com/";
  private static final String serviceName = "TruConnectService";
  private static final String EMAIL_ERROR = "truconnect_alerts@telscape.net";
  public static final String SWITCH_STATUS_ACTIVE = "A";
  public static final String SWITCH_STATUS_SUSPENDED = "S";
  public static final String SWITCH_STATUS_DISCONNECTED = "C";
  public static final String SWITCH_STATUS_RESERVED = "R";
  public static final String SWITCH_STATUS_SWAPPED = "P";

  private static TruConnectService service;
  private static TruConnect port;

  static final Logger logger = LoggerFactory.getLogger(TruConnectBackend.class);
  static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
  static final SimpleDateFormat ldf = new SimpleDateFormat("MM/dd/yyyy");
  static final DecimalFormat df = new DecimalFormat("0.00");

  public static void main(String[] args) {
    TruConnectBackend tcb = new TruConnectBackend();
    try {
      tcb.chargeAccounts();
    } catch (WebServiceException wsException) {
      if (wsException.getMessage().indexOf("Attempted to read or write protected memory") >= 0) {
        System.err.println("Memory corrupt. Exiting the process.");
        System.exit(1);
      }
    }
  }

  public TruConnectBackend() {
    try {
      service = new TruConnectService(new URL(wsdl), new QName(nameSpace, serviceName));
      port = service.getTruConnectPort();
    } catch (MalformedURLException url_ex) {
      logger.error("Unable to reach webservice. {}", url_ex.getMessage());
    }
  }

  protected void chargeAccounts() {
    chargeAccounts(getAccountToChargeList());
  }

  protected static int getTopupQuantity(String topupAmount, String balance) {
    return getTopupQuantity(Double.parseDouble(topupAmount), Double.parseDouble(balance));
  }

  protected static int getTopupQuantity(double topupAmount, double balance) {
    int quantity = 0;
    while (balance < 2.0) {
      ++quantity;
      balance += topupAmount;
    }
    return quantity;
  }

  /**
   * 1. Load the customer info and account info 2. Get their set top-up amount
   * 3. Calculate the total number of top-ups required 4. Submit the payment
   * 
   * @param accountList
   */
  protected void chargeAccounts(List<AccountToProcess> accountList) {
    for (AccountToProcess accountToCharge : accountList) {
      try {
        Account account = getAccount(accountToCharge.getAccountNo());
        Customer customer = getCustomer(accountToCharge.getAccountNo());
        logger.debug("Getting default payment method for customer {}", customer.getId());
        int defaultPaymentId = getDefaultPaymentId(customer);
        logger.debug("Getting top-up amount for customer {}", customer.getId());
        CustTopUp topup = getTopupAmount(customer, account);
        logger.debug("Calculating total top-up amount");
        int topUpQuantity = getTopupQuantity(topup.getTopupAmount(), account.getBalance());
        Double chargeAmount = Double.parseDouble(topup.getTopupAmount()) * topUpQuantity;
        logger.info("Customer will be topped up {} times. Total charge is ${}.", topUpQuantity, NumberFormat.getCurrencyInstance().format(chargeAmount));
        try {
          Object[] loggingArgs = { customer.getId(), account.getAccountno(), defaultPaymentId, df.format(chargeAmount) };
          logger.info("Submitting Payment for Customer {} for account {} with pmtId {} for ${}.", loggingArgs);
          PaymentUnitResponse response = makePayment(customer, defaultPaymentId, account, null, df.format(chargeAmount));
          if (response != null) {
            logger.info("   PaymentUnit Response ");
            logger.info("   AuthCode   :: " + response.getAuthcode());
            logger.info("   ConfCode   :: " + response.getConfcode());
            logger.info("   ConfDescr  :: " + response.getConfdescr());
            logger.info("   CvvCode    :: " + response.getCvvcode());
            logger.info("   TransId    :: " + response.getTransid());
          } else {
            logger.error("PaymentUnit returned no response");
          }
        } catch (PaymentException p_ex) {
          logger.info("Sending failure notification to {}", EMAIL_ERROR);
          String body = EmailHelper.getPaymentFailureBody(account, "", "", topup.getTopupAmount(), "", "paymentId=" + defaultPaymentId, ldf.format(new Date()),
            p_ex.getMessage());
          EmailHelper.sendEmail(EMAIL_ERROR, "Error processing payment for Account " + account.getAccountno(), body);
          logger.debug("Email sent");
        }
        logger.info("Done with Account {}", accountToCharge.getAccountNo());
      } catch (CustomerException c_ex) {
        logger.warn("Skipping Account " + accountToCharge.getAccountNo() + ". Error: " + c_ex.getMessage(), c_ex);
      }
    }
  }

  /**
   * Now handled in TSCPMVNE.TruConnect.submitPaymentById.
   * 
   * @return
   */
  @Deprecated
  protected static List<AccountToProcess> getAccountsToHotLineList() {
    logger.info("Fetching accounts to suspend...");
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();
    Query q = session.getNamedQuery("sp_fetch_accts_to_hotline");
    List<AccountToProcess> accountList = q.list();
    session.getTransaction().commit();
    return accountList;
  }

  /**
   * Now handled in TSCPMVNE.TruConnect.submitPaymentById.
   * 
   * @return
   */
  @Deprecated
  protected static List<AccountToProcess> getAccountsToRestoreList() {
    logger.info("Fetching accounts to restore...");
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();
    Query q = session.getNamedQuery("sp_fetch_accts_to_restore");
    List<AccountToProcess> accountList = q.list();
    session.getTransaction().commit();
    return accountList;
  }

  protected static List<AccountToProcess> getAccountToChargeList() {
    logger.info("Fetching accounts to charge...");
    Session session = HibernateUtil.getSessionFactory().getCurrentSession();
    session.beginTransaction();
    Query q = session.getNamedQuery("sp_fetch_accts_to_charge");
    List<AccountToProcess> accountList = q.list();
    logger.info("   ...{} accounts will be topped-up", accountList.size());
    session.getTransaction().commit();
    return accountList;
  }

  protected Account getAccount(int accountNo) throws CustomerException {
    Account account = port.getAccountInfo(accountNo);
    if (account == null) {
      throw new CustomerException("Error fetching Account " + accountNo);
    } else if (account.getContactEmail() == null || account.getContactEmail().trim().isEmpty()) {
      throw new CustomerException("Error fetching Email Address for account " + account.getAccountno());
    } else {
      return account;
    }
  }

  private Customer getCustomer(int accountNo) throws CustomerException {
    Customer customer = new Customer();
    try {
      customer.setId(port.getCustFromAccount(accountNo).getCustId());
    } catch (NullPointerException np_ex) {
      logger.warn("Unable to get Customer with account {}. MSG: {}", accountNo, np_ex.getMessage());
      throw new CustomerException("Unable to get Customer with account " + accountNo);
    }
    if (customer.getId() == 0) {
      logger.warn("Customer with account {} returned a 0 CustID", accountNo);
      throw new CustomerException("Customer with account " + accountNo + " returned a 0 CustID");
    }
    return customer;
  }

  private int getDefaultPaymentId(Customer customer) throws CustomerException {
    List<CustPmtMap> custPaymentMap = port.getCustPaymentList(customer.getId(), 0);
    if (custPaymentMap != null && custPaymentMap.size() > 0) {
      return custPaymentMap.get(0).getPaymentid();
    } else {
      throw new CustomerException("Error retrieving Payments for Customer " + customer.getId());
    }
  }

  private CustTopUp getTopupAmount(Customer customer, Account tscpMvneAccount) throws CustomerException {
    CustTopUp custTopUp = port.getCustTopUpAmount(customer, tscpMvneAccount);
    if (custTopUp == null || custTopUp.getTopupAmount() == null || custTopUp.getTopupAmount().trim().isEmpty()) {
      throw new CustomerException("Customer topup amount has not been set");
    } else {
      return custTopUp;
    }
  }

  private PaymentUnitResponse makePayment(Customer customer, int paymentId, Account account, CreditCard creditCard, String amount) throws PaymentException {
    logger.debug("Making payment for CustomerId " + customer.getId() + " against Pmt ID " + paymentId + " in the Amount of $" + amount + ".");
    String sessionid = "CID" + customer.getId() + "T" + getTimeStamp() + "AUTO";
    try {
      PaymentUnitResponse response = port.submitPaymentByPaymentId(sessionid, customer, paymentId, account, amount);
      return response;
    } catch (WebServiceException wse) {
      logger.warn("WebService Exception thrown :: " + wse.getMessage());
      // will catch this exception at main()
      if (wse.getMessage().indexOf("Attempted to read or write protected memory") >= 0) {
        throw wse;
      }
      if (wse.getCause() != null) {
        logger.warn("Immediate WSException Cause was :: " + wse.getCause().getMessage());
      }
      throw new PaymentException(wse.getMessage());
    }
  }

  private static final String getTimeStamp() {
    return sdf.format(new Date());
  }
}
