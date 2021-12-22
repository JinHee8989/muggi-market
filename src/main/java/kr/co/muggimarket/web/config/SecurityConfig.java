package kr.co.muggimarket.web.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthenticationProvider authenticationProvider;


    /**
     * AuthenticationManagerBuilder는 스프링 시큐리티의 인증에 대한 지원을 설정
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }


    /**
     * WebSecurity 패턴은 보안 예외 처리(정적 리소스, html)
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .antMatchers("/image/**");
    }

    /**
     * HttpSecurity 패턴은 보안 처리(접근 막음)
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .antMatchers("/login").permitAll()
                .antMatchers("/sign-up").permitAll();


        http.logout()
                .logoutUrl("/logout").permitAll()
                .logoutSuccessUrl("/");

        http.csrf().disable();

        //스프링시큐리티가 세션을 생성하지도, 기존것을 사용하지도 않음
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        //UsernamePasswordAuthenticationFilter 전에 jwtTokenProvider 필터를 적용
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);


        //exception이 발생하면 우리가 만든 accessDeniedHandler와 authenticationEntryPoint를 쓴다는것
        http.exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler())
                .authenticationEntryPoint(authenticationEntryPoint());

    }






}
