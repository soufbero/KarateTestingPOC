package com.souf.karate.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
        basePackages = "com.souf.karate.repository.dev",
        entityManagerFactoryRef = "devEntityManager",
        transactionManagerRef = "devTransactionManager"
)
@ConditionalOnProperty(
        value="db.enabled",
        havingValue = "true")
public class DatabaseConfigurationDEV {

    private final Environment env;

    @Autowired
    public DatabaseConfigurationDEV(Environment env){
        this.env = env;
    }

    @Bean
    @Primary
    public DataSource devDataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(Objects.requireNonNull(env.getProperty("dev.db.driverClassName")));
        dataSource.setUrl(env.getProperty("dev.db.url"));
        dataSource.setUsername(env.getProperty("dev.db.username"));
        dataSource.setPassword(env.getProperty("dev.db.password"));
        return dataSource;
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean devEntityManager() {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(devDataSource());
        em.setPackagesToScan("com.souf.karate");
        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        final HashMap<String,String> properties = new HashMap<>();
        properties.put("hibernate.dialect",env.getProperty("dev.db.hibernate.dialect"));
        properties.put("hibernate.ddl-auto",env.getProperty("dev.db.hibernate.ddl-auto"));
        properties.put("hibernate.show_sql",env.getProperty("dev.db.hibernate.show_sql"));
        properties.put("hibernate.id.new_generator_mappings",
                env.getProperty("dev.db.hibernate.id.new_generator_mappings"));
        em.setJpaPropertyMap(properties);
        return em;
    }

    @Bean
    @Primary
    public PlatformTransactionManager devTransactionManager() {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(devEntityManager().getObject());
        return transactionManager;
    }
}
