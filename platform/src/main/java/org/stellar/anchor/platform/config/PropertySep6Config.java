package org.stellar.anchor.platform.config;

import static org.stellar.anchor.config.Sep6Config.DepositInfoGeneratorType.CUSTODY;
import static org.stellar.anchor.config.Sep6Config.DepositInfoGeneratorType.SELF;
import static org.stellar.anchor.util.StringHelper.isEmpty;

import lombok.*;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.stellar.anchor.api.sep.AssetInfo;
import org.stellar.anchor.asset.AssetService;
import org.stellar.anchor.config.CustodyConfig;
import org.stellar.anchor.config.Sep6Config;

@Data
public class PropertySep6Config implements Sep6Config, Validator {
  boolean enabled;
  Features features;
  DepositInfoGeneratorType depositInfoGeneratorType;
  CustodyConfig custodyConfig;
  AssetService assetService;

  public PropertySep6Config(CustodyConfig custodyConfig, AssetService assetService) {
    this.custodyConfig = custodyConfig;
    this.assetService = assetService;
  }

  @Override
  public boolean supports(@NonNull Class<?> clazz) {
    return Sep6Config.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(@NonNull Object target, @NonNull Errors errors) {
    if (enabled) {
      if (features == null) {
        errors.rejectValue("features", "sep6-features-invalid", "sep6.features is not defined");
      } else if (features.isAccountCreation()) {
        errors.rejectValue(
            "features",
            "sep6-features-account-creation-invalid",
            "sep6.features.account_creation: account creation is not supported");
      } else if (features.isClaimableBalances()) {
        errors.rejectValue(
            "features",
            "sep6-features-claimable-balances-invalid",
            "sep6.features.claimable_balances: claimable balances are not supported");
      }
      validateDepositInfoGeneratorType(errors);
    }
  }

  void validateDepositInfoGeneratorType(Errors errors) {
    if (custodyConfig.isCustodyIntegrationEnabled() && CUSTODY != depositInfoGeneratorType) {
      errors.rejectValue(
          "depositInfoGeneratorType",
          "sep6-deposit-info-generator-type",
          String.format(
              "[%s] deposit info generator type is not supported when custody integration is enabled",
              depositInfoGeneratorType.toString().toLowerCase()));
    } else if (!custodyConfig.isCustodyIntegrationEnabled()
        && CUSTODY == depositInfoGeneratorType) {
      errors.rejectValue(
          "depositInfoGeneratorType",
          "sep6-deposit-info-generator-type",
          "[custody] deposit info generator type is not supported when custody integration is disabled");
    }

    if (SELF == depositInfoGeneratorType) {
      for (AssetInfo asset : assetService.listStellarAssets()) {
        if (!asset.getCode().equals("native") && isEmpty(asset.getDistributionAccount())) {
          errors.rejectValue(
              "depositInfoGeneratorType",
              "sep6-deposit-info-generator-type",
              "[self] deposit info generator type is not supported when distribution account is not set");
        }
      }
    }
  }
}