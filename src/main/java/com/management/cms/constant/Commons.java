package com.management.cms.constant;

import java.text.SimpleDateFormat;

public class Commons {
    public static final String SVC_SUCCESS_00 = "SVC-SUCCESS-00";
    public static final String SVC_ERROR_99 = "SVC-ERROR-99";
    public static final String OK = "ok";
    public static final String SENDER = "khdn";
    public static final Integer STATUS_ACTIVE = 1;
    public static final Integer STATUS_INACTIVE = 0;
    public static final Integer STATUS_WAITING_FOR_ACTIVATED = 2;

    public static final Integer ENABLED = 1;
    public static final Integer DISABLED = 0;

    public static final String DEFAULT_PASSWORD = "123456zx";

    public static final Integer HAVE_WAITING_PACKAGE = 1;
    public static final Integer DO_NOT_HAVE_WAITING_PACKAGE = 0;

    public static final Integer HAVE_NOT_RESET_PASS = 0;
    public static final Integer DID_RESET_PASS = 1;

    public static final String DATE_TIME_VN = "dd-MM-yyyy HH:mm:ss";
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    public static final SimpleDateFormat DATE_FORMAT_01 = new SimpleDateFormat("yyyy-MM-dd");
    public static final String OUTPUT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String INPUT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
}
