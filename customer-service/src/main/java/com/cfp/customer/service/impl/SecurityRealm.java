package com.cfp.customer.service.impl;

public enum SecurityRealm {
  securityRealm;

  public String getRealm(AuthLevel authLevel) {
    return authLevel.name();
  }
}
