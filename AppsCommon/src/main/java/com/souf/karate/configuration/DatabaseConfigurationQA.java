package com.souf.karate.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Objects;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.souf.karate.repository.qa",
        entityManagerFactoryRef = "qaEntityManager",
        transactionManagerRef = "qaTransactionManager"
)
@ConditionalOnProperty(
        value="db.enabled",
        havingValue = "true")
public class DatabaseConfigurationQA {

    private final Environment env;

    @Autowired
    public DatabaseConfigurationQA(Environment env){
        this.env = env;
    }

    @Bean
    public DataSource qaDataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(Objects.requireNonNull(env.getProperty("qa.db.driverClassName")));
        dataSource.setUrl(env.getProperty("qa.db.url"));
        dataSource.setUsername(env.getProperty("qa.db.username"));
        dataSource.setPassword(env.getProperty("qa.db.password"));
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean qaEntityManager() {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(qaDataSource());
        em.setPackagesToScan("com.souf.karate");
        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        final HashMap<String,String> properties = new HashMap<>();
        properties.put("hibernate.dialect",env.getProperty("qa.db.hibernate.dialect"));
        properties.put("hibernate.ddl-auto",env.getProperty("qa.db.hibernate.ddl-auto"));
        properties.put("hibernate.show_sql",env.getProperty("qa.db.hibernate.show_sql"));
        properties.put("hibernate.id.new_generator_mappings",
                env.getProperty("qa.db.hibernate.id.new_generator_mappings"));
        em.setJpaPropertyMap(properties);
        return em;
    }

    @Bean
    public PlatformTransactionManager qaTransactionManager() {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(qaEntityManager().getObject());
        return transactionManager;
    }
}
