package com.example.tictactoe.main.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Configuration
public class DatabaseConfig {
    @Bean
    public DriverManagerDataSource dataSource(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName("org.postgresql.Driver");
//
//
//        dataSource.setUrl("jdbc:postgresql://localhost:5432/tttbase");
//        dataSource.setUsername("root");
//        dataSource.setPassword("123");

        dataSource.setUrl(System.getenv("JDBC_DATABASE_URL"));
        dataSource.setUsername(System.getenv("JDBC_DATABASE_USERNAME"));
        dataSource.setPassword(System.getenv("JDBC_DATABASE_PASSWORD"));


        return dataSource;
    }

    @Bean
    public AbstractJpaVendorAdapter vendorAdapter(){
        AbstractJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setShowSql(true);
        adapter.setGenerateDdl(true);
        return adapter;
    }
}
