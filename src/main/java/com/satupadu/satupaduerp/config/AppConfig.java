package com.satupadu.satupaduerp.config;

import javax.sql.DataSource;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

// konfigurasi Spring: objek apa saja yang dibuat dan dikelola oleh Spring (bean)
@Configuration
@ComponentScan("com.satupadu.satupaduerp")   // cari class ber-anotasi @Component, @Repository, @Service
@EnableTransactionManagement                  // aktifkan @Transactional
@EnableAspectJAutoProxy                       // aktifkan AOP (@Aspect)
public class AppConfig {

    // koneksi ke database MySQL
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl("jdbc:mysql://localhost:3306/simerp");
        ds.setUsername("root");
        ds.setPassword("");
        return ds;
    }

    // SessionFactory Hibernate dibuat Spring memakai DataSource di atas;
    // pengaturan Hibernate lainnya (dialek, mapping class) tetap dari hibernate.cfg.xml
    @Bean
    public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
        LocalSessionFactoryBean factory = new LocalSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setConfigLocation(new ClassPathResource("hibernate.cfg.xml"));
        return factory;
    }

    // Spring DAO: error dari Hibernate/MySQL di class @Repository diubah menjadi DataAccessException
    @Bean
    public static PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    // pengatur transaksi: buka, commit, dan rollback dilakukan Spring
    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory, DataSource dataSource) {
        HibernateTransactionManager tm = new HibernateTransactionManager(sessionFactory);
        tm.setDataSource(dataSource);
        return tm;
    }
}
