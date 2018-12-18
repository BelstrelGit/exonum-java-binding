package com.exonum.binding.transaction;

import static com.exonum.binding.common.hash.Hashing.sha256;

import com.exonum.binding.common.hash.HashCode;
import com.google.common.base.Objects;
import java.util.Arrays;

/**
 * An Exonum raw transaction. It is mainly used for interaction with the Exonum core
 * as well as for transferring transactions between nodes within the network.
 */
public final class RawTransaction {
  private final short serviceId;
  private final short transactionId;
  private final byte[] payload;

  private RawTransaction(short serviceId, short transactionId, final byte[] payload) {
    this.serviceId = serviceId;
    this.transactionId = transactionId;
    this.payload = payload.clone();
  }

  /**
   * Returns a service identifier which the transaction belongs to.
   */
  public short getServiceId() {
    return serviceId;
  }

  /**
   * Returns the transaction identifier.
   */
  public short getTransactionId() {
    return transactionId;
  }

  /**
   * Returns the transaction payload which contains actual transaction data.
   */
  public byte[] getPayload() {
    return payload.clone();
  }

  /**
   * Returns the SHA-256 hash of the transaction data.
   */
  public HashCode hash() {
    return sha256().hashBytes(getPayload());
  }

  /**
   * Returns the new builder for the transaction.
   */
  public static Builder newBuilder() {
    return new Builder();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RawTransaction that = (RawTransaction) o;
    return serviceId == that.serviceId
        && transactionId == that.transactionId
        && Arrays.equals(payload, that.payload);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(serviceId, transactionId) + Arrays.hashCode(payload);
  }

  public static final class Builder {
    private Short serviceId;
    private Short transactionId;
    private byte[] payload;

    /**
     * Sets service identifier to the transaction.
     */
    public RawTransaction.Builder serviceId(short serviceId) {
      this.serviceId = serviceId;
      return this;
    }

    /**
     * Sets transaction identifier to the transaction.
     */
    public RawTransaction.Builder transactionId(short transactionId) {
      this.transactionId = transactionId;
      return this;
    }

    /**
     * Sets payload to the transaction.
     */
    public RawTransaction.Builder payload(byte[] payload) {
      this.payload = payload.clone();
      return this;
    }

    /**
     * Creates the raw transaction instance.
     */
    public RawTransaction build() {
      checkRequiredFieldsSet();

      return new RawTransaction(this.serviceId, this.transactionId, this.payload);
    }

    private void checkRequiredFieldsSet() {
      String undefinedFields = "";
      undefinedFields = this.serviceId == null ? undefinedFields + " serviceId" : undefinedFields;
      undefinedFields =
          this.transactionId == null ? undefinedFields + " transactionId" : undefinedFields;
      undefinedFields = this.payload == null ? undefinedFields + " payload" : undefinedFields;
      if (!undefinedFields.isEmpty()) {
        throw new IllegalStateException(
            "Following field(s) are required but weren't set: " + undefinedFields);
      }
    }

    private Builder() {
    }
  }
}
