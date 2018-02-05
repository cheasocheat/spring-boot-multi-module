package com.mobiecode.mobieclient.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@PropertySource(value = {"classpath:conf/security.properties"})
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class ApplicationSecurity extends WebSecurityConfigurerAdapter {

    @Autowired
    private Environment env;

    @Autowired
    @Qualifier(value = "mobieDataSource")
    private DataSource dataSource;

    @Autowired
    private AccessDeniedHandler accessDeniedHandler;

    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private LogoutSuccessHandler logoutSuccessHandler;

    @Autowired
    private SecurityProperties securityProperties;

    @Autowired
    private SecurityExpressionHandler<FilterInvocation> securityExpressionHandler;

    @Autowired
    private DaoAuthenticationProvider authProvider;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authProvider);

       /* auth.
                jdbcAuthentication()
                .usersByUsernameQuery(env.getProperty("security.user.query"))
                .authoritiesByUsernameQuery(env.getProperty("security.role.query"))
                .dataSource(dataSource)
                .passwordEncoder(new BCryptPasswordEncoder());*/
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        /**
         * Check Enable CSRF Protection
         */
        if (securityProperties.isEnableCsrf()) {
            http.addFilterAfter(new CsrfHeaderFilter(), CsrfFilter.class);
        } else {
            http.csrf().disable();
        }

        http
                .authorizeRequests()
                .expressionHandler(securityExpressionHandler)
                .antMatchers("/", "/resources/**", "/static/**", "/js/**", "/css/**", "/img/**", "/webjars/**").permitAll()
                .antMatchers("/about").permitAll()
                .antMatchers("/map").permitAll()
                .antMatchers("/map_emb").permitAll()
                .antMatchers("/map_direction").permitAll()
                .antMatchers("/way_point").permitAll()
                .antMatchers("/role").permitAll()
                .antMatchers("/register").permitAll()
                .antMatchers("/home").permitAll()
                .antMatchers("/admin/**").access("hasRole('ADM')")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/account/login")
                .failureUrl("/account/login?error=true")
                .usernameParameter("username")
                .passwordParameter("password")
                .successHandler(authenticationSuccessHandler)
                .permitAll()
                .and()
                .logout()
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .logoutRequestMatcher(new AntPathRequestMatcher("/account/logout"))
//                .logoutSuccessUrl("/account/login?logout")
                .logoutSuccessHandler(logoutSuccessHandler)
                .permitAll()
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler);


    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**", "/webjars/**");
    }


}