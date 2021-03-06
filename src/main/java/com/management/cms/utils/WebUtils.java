package com.management.cms.utils;

import com.management.cms.security.UserDetailsImpl;
import com.mifmif.common.regex.Generex;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

public class WebUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebUtils.class);

    public static String urlLinkImage(List<String> domains, String domainTarget, String linkImgDB) {
        if(StringUtils.isEmpty(linkImgDB)){
            LOGGER.info("linkImgDb is empty");
            return null;
        }
        LOGGER.info("set domain target for: {}", linkImgDB);
        String linkImgTarget;
        for(String url : domains) {
            if(linkImgDB.contains(url)) {
                linkImgTarget = linkImgDB.replace(url, domainTarget);
                LOGGER.info("link img target: {}", linkImgTarget);
                return linkImgTarget;
            }
        }
        return linkImgDB;
    }

    public static String genderRandomByRegex(String regex) {
        Generex generex = new Generex(regex);
        return generex.random();
    }

    public static UserDetailsImpl getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipalName = authentication.getName();
        UserDetailsImpl userPrincipal = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userPrincipal;
    }

    public static long getTimeStamp(LocalDateTime localDateTime) {
        if (localDateTime == null) return 0;
        return localDateTime.toEpochSecond(ZoneOffset.UTC) - 7 * 60 * 60;
    }
}
