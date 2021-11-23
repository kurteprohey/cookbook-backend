package com.testprojects.portfolio.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DriverManagerDataSource
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore
import javax.sql.DataSource

@Configuration
class JDBCTokenConfig {
    @Value("\${spring.datasource.url}")
    private val datasourceUrl: String? = null

    @Value("\${spring.datasource.driver-class-name}")
    private val dbDriverClassName: String? = null

    @Value("\${spring.datasource.username}")
    private val dbUsername: String? = null

    @Value("\${spring.datasource.password}")
    private val dbPassword: String? = null

    @Bean
    fun dataSource(): DataSource {
        val dataSource = DriverManagerDataSource()
        dataSource.setDriverClassName(dbDriverClassName!!)
        dataSource.url = datasourceUrl
        dataSource.username = dbUsername
        dataSource.password = dbPassword
        return dataSource
    }

    @Bean
    fun tokenStore(): TokenStore {
        return JdbcTokenStore(dataSource())
    }
}