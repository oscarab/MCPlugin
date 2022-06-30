package pers.oscar.mcplugin.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pers.oscar.mcplugin.security.filter.LoginFilter;
import pers.oscar.mcplugin.security.handle.LoginFailureHandle;
import pers.oscar.mcplugin.security.handle.LoginSuccessHandle;
import pers.oscar.mcplugin.security.handle.MyAccessDeniedHandler;
import pers.oscar.mcplugin.security.handle.MyLogoutSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableGlobalAuthentication
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private MyAccessDeniedHandler myAccessDeniedHandler;
    private MyLogoutSuccessHandler myLogoutSuccessHandler;
    private LoginFailureHandle loginFailureHandle;
    private LoginSuccessHandle loginSuccessHandle;
    @Autowired
    public void setCustomizeAccessDeniedHandler(MyAccessDeniedHandler myAccessDeniedHandler) {
        this.myAccessDeniedHandler = myAccessDeniedHandler;
    }
    @Autowired
    public void setCustomizeLogoutSuccessHandler(MyLogoutSuccessHandler myLogoutSuccessHandler) {
        this.myLogoutSuccessHandler = myLogoutSuccessHandler;
    }
    @Autowired
    public void setLoginFailureHandle(LoginFailureHandle loginFailureHandle) {
        this.loginFailureHandle = loginFailureHandle;
    }
    @Autowired
    public void setLoginSuccessHandle(LoginSuccessHandle loginSuccessHandle) {
        this.loginSuccessHandle = loginSuccessHandle;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers("/auth/**").permitAll()
                    .antMatchers("/plugins/page/**").permitAll()
                    .antMatchers("/plugins/user/**").permitAll()
                    .antMatchers("/plugins/fuzzy").permitAll()
                    .antMatchers("/plugins/exact").permitAll()
                    .antMatchers("/plugins/hot").permitAll()
                    .antMatchers("/plugins/{id:\\d+}/info").permitAll()
                    .antMatchers("/comment/{id:\\d+}/**").permitAll()
                    .antMatchers("/document/{id:\\d+}/info").permitAll()
                    .antMatchers("/document/user/**").permitAll()
                    .antMatchers("/copy/user/**").permitAll()
                    .antMatchers("/user/{id:\\d+}").permitAll()
                    .antMatchers("/user/search/**").permitAll()
                    .antMatchers("/admin/**").hasRole("admin")
                    .antMatchers("/plugins/create").hasAnyRole("dev", "admin")
                    .antMatchers("/plugins/save").hasAnyRole("dev", "admin")
                    .anyRequest().authenticated()
                .and()
                    .logout()
                    .logoutUrl("/auth/logout")
                    .logoutSuccessHandler(myLogoutSuccessHandler)
                .and()
                    .exceptionHandling()
                    .authenticationEntryPoint(myAccessDeniedHandler)
                    .accessDeniedHandler(myAccessDeniedHandler)
                .and()
                    .csrf().disable();
        http.addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsConverter());
        auth.inMemoryAuthentication()
                .passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/image/**");
    }

    @Bean
    public LoginFilter loginFilter() throws Exception {
        LoginFilter loginFilter = new LoginFilter(authenticationManager());
        loginFilter.setAuthenticationSuccessHandler(loginSuccessHandle);
        loginFilter.setAuthenticationFailureHandler(loginFailureHandle);
        loginFilter.setAuthenticationManager(authenticationManagerBean());
        loginFilter.setFilterProcessesUrl("/auth/login");
        return loginFilter;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsConverter userDetailsConverter() {
        return new UserDetailsConverter();
    }
}
