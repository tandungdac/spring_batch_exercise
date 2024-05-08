package com.adapt.exercise;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@EnableJpaRepositories(basePackages = "com.adapt.exercise.repository")
@EnableTransactionManagement
public class TestDatabaseConfig {

    @Bean
    public DataSource dataSource() throws SQLException {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/testdb?createDatabaseIfNotExist=true");
        dataSource.setUsername("root");
        dataSource.setPassword("123456");
//        dataSource.setDriverClassName("org.h2.Driver");
//        dataSource.setUrl("jdbc:h2:file:~/testdb");
//        dataSource.setUsername("sa");
//        dataSource.setPassword("");

        String schemaFilePath = "C:\\Users\\USER\\IdeaProjects\\exercise\\src\\test\\resources\\schema.sql";
        Resource schemaFileResource = new FileSystemResource(schemaFilePath);

        DatabasePopulator databasePopulator = new ResourceDatabasePopulator(schemaFileResource);
        databasePopulator.populate(dataSource.getConnection());
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.adapt.exercise.model.entity");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        return em;
    }
}
