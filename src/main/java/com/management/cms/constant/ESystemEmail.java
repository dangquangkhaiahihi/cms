package com.management.cms.constant;

public enum ESystemEmail {
    MAIL_RESET_PASS("Cấp lại PASSWORD", "reset_pass.html"),
    MAIL_CREATE_ACCOUNT("Cấp tài khoản mới ", "create_user.html");

    private String subject;
    private String template;

    ESystemEmail(String subject, String template) {
        this.subject = subject;
        this.template = template;
    }

    public String getSubject() {
        return subject;
    }

    public String getTemplate() {
        return template;
    }
}


