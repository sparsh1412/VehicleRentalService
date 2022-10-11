package com.navi.vehiclerental.models;

import com.navi.vehiclerental.enums.Currency;

import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Value
@Getter
@Builder
public class Price {
    private Currency currency;
    private double value;
}
