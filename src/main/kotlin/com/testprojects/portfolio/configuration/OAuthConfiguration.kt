package com.testprojects.portfolio.configuration

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter


@Configuration
@EnableAuthorizationServer
class OAuthConfiguration(
    private val authenticationManager: AuthenticationManager,
    private val passwordEncoder: PasswordEncoder,
    private val userService: UserDetailsService
) :
    AuthorizationServerConfigurerAdapter() {
    @Value("\${jwt.clientId:cookbook}")
    private val clientId: String? = null
    @Value("\${jwt.client-secret:secret}")
    private val clientSecret: String? = null
    @Value("\${jwt.signing-key:123}")
    private val jwtSigningKey: String? = null
    @Value("\${jwt.accessTokenValidititySeconds:2592000}") // 30 days
    private val accessTokenValiditySeconds = 0
    @Value("\${jwt.authorizedGrantTypes:password,authorization_code,refresh_token}")
    private val authorizedGrantTypes: Array<String> = arrayOf()
    @Value("\${jwt.refreshTokenValiditySeconds:2592000}") // 30 days
    private val refreshTokenValiditySeconds = 0

    @Autowired
    private val tokenStore: TokenStore? = null

    @Throws(Exception::class)
    override fun configure(clients: ClientDetailsServiceConfigurer) {
        clients.inMemory()
            .withClient(clientId)
            .secret(passwordEncoder.encode(clientSecret))
            .accessTokenValiditySeconds(accessTokenValiditySeconds)
            .refreshTokenValiditySeconds(refreshTokenValiditySeconds)
            .authorizedGrantTypes(*authorizedGrantTypes)
            .scopes("read", "write")
            .resourceIds("api")
    }

    override fun configure(endpoints: AuthorizationServerEndpointsConfigurer) {
        endpoints
            .tokenStore(this.tokenStore)
            .accessTokenConverter(accessTokenConverter())
            .userDetailsService(userService)
            .authenticationManager(authenticationManager)
    }

    @Bean
    fun accessTokenConverter(): JwtAccessTokenConverter {
        return JwtAccessTokenConverter()
    }

}
