package com.customworld.enums;

public enum ChannelOption {
    MOBILE_MONEY("mobile_money"),
    BANK_TRANSFER("bank_transfer"),
    CARD("card");

    private final String notchPayValue;

    ChannelOption(String notchPayValue) {
        this.notchPayValue = notchPayValue;
    }

    public String getNotchPayValue() {
        return notchPayValue;
    }
}