package com.tc.bu.hibernate;

import java.io.Serializable;

public class GeneralSPResponse implements Serializable {
  private static final long serialVersionUID = 6388990599511698931L;
  private int mvnemsgcode;
  private String status;
  private String mvnemsg;

  public GeneralSPResponse() {

  }

  public int getMvnemsgcode() {
    return mvnemsgcode;
  }

  public void setMvnemsgcode(int mvnemsgcode) {
    this.mvnemsgcode = mvnemsgcode;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getMvnemsg() {
    return mvnemsg;
  }

  public void setMvnemsg(String mvnemsg) {
    this.mvnemsg = mvnemsg;
  }

}
