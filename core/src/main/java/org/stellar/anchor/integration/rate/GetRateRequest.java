package org.stellar.anchor.integration.rate;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetRateRequest {
  Type type;

  @SerializedName("sell_asset")
  String sellAsset;

  @SerializedName("buy_asset")
  String buyAsset;

  @SerializedName("sell_amount")
  String sellAmount;

  @SerializedName("buy_amount")
  String buyAmount;

  @SerializedName("country_code")
  String countryCode;

  @SerializedName("sell_delivery_method")
  String sellDeliveryMethod;

  @SerializedName("buy_delivery_method")
  String buyDeliveryMethod;

  @SerializedName("client_domain")
  String clientDomain;

  String id;
  String account;
  String memo;

  @SerializedName("memo_type")
  String memoType;

  public enum Type {
    @SerializedName("indicative")
    INDICATIVE("indicative"),

    @SerializedName("firm")
    FIRM("firm");

    private final String name;

    Type(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return name;
    }
  }
}