package ru.ssau.config;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class HibernateConfig{

    @Autowired
    private Environment env;

    @Bean
    public DataSource getDataSource(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName( env.getRequiredProperty( "jdbc.driverClassName" ) );
        dataSource.setUrl( env.getRequiredProperty( "jdbc.url" ) );
        dataSource.setUsername( env.getRequiredProperty( "jdbc.username" ) );
        dataSource.setPassword( env.getRequiredProperty( "jdbc.password" ) );
        return dataSource;
    }

    @Bean
    public LocalSessionFactoryBean getSessionFactory(){
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource( getDataSource() );
        sessionFactory.setPackagesToScan( "ru.ssau.domain" );
        sessionFactory.setHibernateProperties( getHibernateProperties() );
        return sessionFactory;
    }

    private Properties getHibernateProperties(){
        Properties properties = new Properties();
        properties.put( "hibernate.dialect", env.getRequiredProperty( "hibernate.dialect" ) );
        properties.put( "hibernate.show_sql", env.getRequiredProperty( "hibernate.show_sql" ) );
        properties.put( "hibernate.batch.size", env.getRequiredProperty( "hibernate.batch.size" ) );
        properties.put( "hibernate.hbm2ddl.auto", env.getRequiredProperty( "hibernate.hbm2ddl.auto" ) );
        properties.put( "hibernate.current.session.context.class",
                        env.getRequiredProperty( "hibernate.current.session.context.class" ) );
        return properties;
    }

    @Bean
    public HibernateTransactionManager transactionManager( SessionFactory sessionFactory ){
        HibernateTransactionManager txManager = new HibernateTransactionManager();
        txManager.setSessionFactory( sessionFactory );
        return txManager;
    }
}
