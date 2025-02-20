package org.postgresql.util;

public interface InetAddressConnectionSpec extends ConnectionSpec {

  String getHost();

  int getPort();

  @Override
  default String getConnectedAddress() {
    return getHost().concat(":").concat(String.valueOf(getPort()));
  }
}
