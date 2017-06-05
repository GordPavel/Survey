package ru.ssau.config;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@ComponentScan( "ru.ssau.domain" )
@EnableJpaRepositories( "ru.ssau.DAO" )
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
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(){
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource( getDataSource() );
        entityManagerFactoryBean.setPersistenceProviderClass( HibernatePersistenceProvider.class );
        entityManagerFactoryBean.setPackagesToScan( env.getProperty( "entity.manager.packages.to.scan" ) );

        entityManagerFactoryBean.setJpaProperties( getHibernateProperties() );

        return entityManagerFactoryBean;
    }

    @Bean
    public JpaTransactionManager transactionManager(){
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory( entityManagerFactory().getObject() );
        return transactionManager;
    }

}
