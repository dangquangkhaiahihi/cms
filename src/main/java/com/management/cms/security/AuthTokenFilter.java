package com.management.cms.security;

import com.google.gson.Gson;
import com.management.cms.constant.Commons;
import com.management.cms.model.enitity.AccessTokenMgo;
import com.management.cms.model.response.ResponseAuthJwt;
import com.management.cms.repository.AccessTokenMgoRepository;
import com.management.cms.utils.JwtUtils;
import com.management.cms.utils.Utils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private AccessTokenMgoRepository accessTokenMgoRepository;
    private Gson gson = Utils.getWmfGson();

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            //tách lấy token từ header của request
            String jwt = Utils.parseJwt(request);
            logger.info("parseJwt token: {}", jwt);

            //nếu bản thân token = null hoặc không hợp lệ thì ko vào cái if này
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                //check token có trong DB không + check status active + check chưa hết hạn
                AccessTokenMgo accessTokenMgo = accessTokenMgoRepository.findByToken(jwt);
                if (ObjectUtils.isEmpty(accessTokenMgo) || !accessTokenMgo.getStatus().equals(Commons.STATUS_ACTIVE) ||
                        accessTokenMgo.getExpireDate().isBefore(LocalDateTime.now()) ==  true
                  ){
                    throw new IOException("Token hết hạn");
                }
                ResponseAuthJwt responseAuthJwt = jwtUtils.getInfoAuthFromJwtToken(jwt);
                logger.info("res auth jwt: {}", gson.toJson(responseAuthJwt));

                UserDetails partnerDetails = userDetailsService.loadUserByUsername(responseAuthJwt.getUserName());
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(partnerDetails, null, partnerDetails.getAuthorities());
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            else {

            }
        } catch (Exception e) {
            logger.debug("Cannot set user authentication", e);
            throw new IOException(e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
