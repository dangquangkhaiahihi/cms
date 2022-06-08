package com.management.cms.utils;

import com.google.gson.*;
import com.management.cms.constant.Commons;
import com.management.cms.security.UserDetailsImpl;
import com.management.cms.model.enitity.UserDoc;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Convert from utils.py
 */
public class Utils {
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    public static UserDetailsImpl getUserDetail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return null;
        }
        if (auth instanceof AnonymousAuthenticationToken) {
            logger.info("user is anonymousAuthentication");
        }
        if (auth.getPrincipal() instanceof UserDetailsImpl) {
            return (UserDetailsImpl) auth.getPrincipal();
        } else {
            return (UserDetailsImpl) auth.getDetails();
        }
    }

    public static UserDoc getCurrentUser() {
        UserDetailsImpl userDetail = getUserDetail();
        return userDetail.getUser();
    }

    public static Gson getWmfGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(
                Date.class,
                new JsonDeserializer() {
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

                    @Override
                    public Date deserialize(
                            JsonElement jsonElement,
                            Type type,
                            JsonDeserializationContext jsonDeserializationContext)
                            throws JsonParseException {
                        try {
                            return df.parse(jsonElement.getAsString());
                        } catch (ParseException e) {
                            return null;
                        }
                    }
                });
        Gson gson = gsonBuilder.create();
        return gson;
    }

    public static String trim(String name) {
        if (StringUtils.isNotBlank(name)) {
            return name.trim();
        }
        return name;
    }

    public static String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        logger.info("headerAuth: {}", headerAuth);
//    if (StringUtils.isNotBlank(headerAuth) && headerAuth.startsWith("Bearer")) {
        if (StringUtils.isNotBlank(headerAuth)) {
            return headerAuth.substring(7, headerAuth.length());
        }
        return null;
    }

//    public static LocalDateTime convertStringToLocalDateTime(String source) {
//        try {
//            return Commons.DATE_FORMAT.parse(source).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    private static final String[] IP_HEADERS = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_X_FORWARDED",
            "HTTP_X_CLUSTER_CLIENT_IP",
            "HTTP_CLIENT_IP",
            "HTTP_FORWARDED_FOR",
            "HTTP_FORWARDED",
            "HTTP_VIA",
            "REMOTE_ADDR"
    };

    public static String getRequestIP(HttpServletRequest request) {
        for (String header : IP_HEADERS) {
            String value = request.getHeader(header);
            if (value == null || value.isEmpty()) {
                continue;
            }
            String[] parts = value.split("\\s*,\\s*");
            return parts[0];
        }
        return request.getRemoteAddr();
    }

    public static LocalDateTime convertStringToLocalDateTime01(String source) throws Exception{
        try {
            return Commons.DATE_FORMAT_01.parse(source).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static String convertDateToString(LocalDateTime date) {
        return date.format(DateTimeFormatter.ofPattern(Commons.OUTPUT_DATE_FORMAT));
    }
//
//    public static LocalDateTime convertStringToDate(String date) {
//        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(Commons.INPUT_DATE_FORMAT));
//    }
}
