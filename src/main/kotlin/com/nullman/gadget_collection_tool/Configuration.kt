package com.nullman.gadget_collection_tool

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.util.*
import javax.persistence.EntityManagerFactory
import javax.sql.DataSource

@Configuration
@EnableJpaRepositories
@EnableTransactionManagement
open class Configuration {
    @Bean
    open fun objectMapper(): ObjectMapper {
        return ObjectMapper()
    }

    @Bean
    open fun entityManagerFactory(): LocalContainerEntityManagerFactoryBean {
        val vendorAdapter = HibernateJpaVendorAdapter()
        return LocalContainerEntityManagerFactoryBean().apply {
            dataSource = dataSource()
            setPackagesToScan("com.nullman.gadget_collection_tool")
            jpaVendorAdapter = vendorAdapter
            setJpaProperties(jpaProperties())
        }
    }

    @Bean
    open fun dataSource(): DataSource {
//        return DriverManagerDataSource().apply {
//            setDriverClassName(jpaProperties().getProperty("connection.driver_class"))
//            url = jpaProperties().getProperty("hibernate.connection.url")
//            username = "root"
//            password = ""
//        }
        return EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .build()
    }

    @Bean
    open fun transactionManager(emf: EntityManagerFactory): PlatformTransactionManager {
        return JpaTransactionManager().apply {
            entityManagerFactory = emf
        }
    }

    @Bean
    open fun exceptionTranslation(): PersistenceExceptionTranslationPostProcessor {
        return PersistenceExceptionTranslationPostProcessor()
    }

    private fun jpaProperties(): Properties {
        return Properties().apply {
            setProperty("connection.driver_class", "org.h2.Driver")
            setProperty("hibernate.connection.url", "jdbc:h2:./db/repository")
            setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect")
            setProperty("hibernate.hbm2ddl.auto", "create-drop")
            setProperty("hibernate.show_sql", "true")
        }
    }
}
