package com.myaccounts.service.myaccountsservice.components;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class PropsSesionComponent {

    private Long accesstime = 1000 * 60 * 7L;
    private Long loginTime = 1000 * 60 * 60 * 24 * 7L;
    @Getter
    private String sameStite = "Lax";
    @Getter
    private Boolean security = false;
    @Getter
    private String path = "";
    @Getter
    private Boolean httpOnly = false;

    public Date getAccesstime() {
        return new Date(System.currentTimeMillis() + accesstime);
    }

    public Date getLoginTime() {
        return new Date(System.currentTimeMillis() + loginTime);
    }

    public Long getLoginTimeCookie() {
        return loginTime/1000;
    }
}