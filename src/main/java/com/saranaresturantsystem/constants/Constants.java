package com.saranaresturantsystem.constants;

public class Constants {
    public static final String BEARER = "Bearer";
    public static final String REGISTER_INVITE_CODE = "REGISTER_INVITE_CODE";
    public static final String REGISTER = "REGISTER";
    public static final String CASHIER = "CASHIER";
    public static final String USER = "USER";

    // sale status
    public static final String COMPLETED = "COMPLETED";
    public static final String PENDING = "PENDING";
    public static final String CANCELLED = "CANCELLED";
    public static final String RETURNED = "RETURNED";
    public static final String PARTIAL_RETURNED = "PARTIALLY_RETURNED";
    public static final String SALE = "SALE";

    // payment status (sale level)
    public static final String UNPAID = "UNPAID";
    public static final String PARTIAL = "PARTIAL";
    public static final String PAID = "PAID";

    // payment record status (item level)
    public static final String PAYMENT_COMPLETED = "COMPLETED";
    public static final String PAYMENT_PENDING = "PENDING";
    public static final String PAYMENT_VOIDED = "VOIDED";

    // payment methods
    public static final String PAYMENT_METHOD_CASH = "CASH";
    public static final String PAYMENT_METHOD_ABA = "ABA";
    public static final String PAYMENT_METHOD_BANK_TRANSFER = "BANK_TRANSFER";
    public static final String PAYMENT_METHOD_CARD = "CARD";
    public static final String PAYMENT_METHOD_OTHER = "OTHER";
    public static final String PAYMENT_METHOD_SPLIT = "SPLIT";

    // cash register
    public static final String REGISTER_OPEN = "OPEN";
    public static final String REGISTER_CLOSED = "CLOSED";
    public static final String PURCHASE = "PURCHASE";
    public static final String EXPENSE = "EXPENSE";
    public static final String CANCEL_PURCHASE = "CANCEL_PURCHASE";
    public static final String RETURN_SALE = "RETURN_SALE";

    // stock
    public static final String LOW_STOCK = "LOW_STOCK";
    public static final String IN_STOCK = "IN_STOCK";
    public static final String OUT_STOCK = "OUT_STOCK";

    // active
    public static final String STATUS_ACTIVE = "ACT";
    public static final String STATUS_DELETE = "DEL";
    public static final String STATUS_INIT = "INT";

    // sale
    public static final String SOLD = "SOLD";
    public static final String AVAILABLE = "AVAILABLE";
    public static final String OUT_OF_STOCK = "OUT_OF_STOCK";
    public static final String SYSTEM = "SYSTEM";

    public static String YES = "Y";
    public static String No = "N";

    public static final String BAD_REQUEST = "Bad Request";
    public static final String INTERNAL_SERVER_ERROR = "Internal Server Error";
}
