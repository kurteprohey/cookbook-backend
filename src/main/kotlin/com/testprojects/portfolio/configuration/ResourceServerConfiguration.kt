package com.testprojects.portfolio.configuration

import com.testprojects.portfolio.errors.CustomAccessDeniedHandler
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
import org.springframework.security.web.authentication.HttpStatusEntryPoint

@Configuration
@EnableResourceServer
class ResourceServerConfiguration : ResourceServerConfigurerAdapter() {
    override fun configure(resources: ResourceServerSecurityConfigurer) {
        resources.resourceId("api")
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            // .antMatcher("/api/**")
            .authorizeRequests()
            .antMatchers("**/**").permitAll()
            // .antMatchers("/api/signin**").permitAll()
            // .antMatchers(HttpMethod.GET, "/api/recipes").permitAll()
            // .antMatchers(HttpMethod.POST, "/api/recipes").permitAll()
            // .antMatchers(HttpMethod.POST, "/api/recipes/search").permitAll()
            // .antMatchers("/api/actuator/**").permitAll()
            // .antMatchers("actuator/**").permitAll()
            // .antMatchers("/api/**").authenticated()
            // .anyRequest().authenticated()
        http.cors();
    }
}
