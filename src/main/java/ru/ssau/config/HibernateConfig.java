package ru.ssau.config;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;


@Configuration
@EnableTransactionManagement
public class HibernateConfig{

    @Autowired
    private Environment environment;

    /**
     * Компонент источника данных
     */
    @Bean
    public DataSource dataSource(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName( environment.getProperty( "jdbc.driverClassName" ) );
        dataSource.setUrl( environment.getProperty( "jdbc.url" ) );
        dataSource.setUsername( environment.getProperty( "jdbc.username" ) );
        dataSource.setPassword( environment.getProperty( "jdbc.password" ) );
        return dataSource;
    }

    /**
     * Свойства Hibernate в виде объекта класса Properties
     */
    @Bean
    public Properties hibernateProperties(){
        Properties properties = new Properties();
        properties.put( "hibernate.dialect", environment.getProperty( "hibernate.dialect" ) );
        properties.put( "hibernate.show_sql", environment.getProperty( "hibernate.show_sql" ) );
        properties.put( "hibernate.hbm2ddl.auto", environment.getProperty( "hibernate.hbm2ddl.auto" ) );
        return properties;
    }

    /**
     * Фабрика сессий Hibernate
     */
    @Bean
    @SuppressWarnings( "deprecation" )
    public SessionFactory sessionFactory(){
        return new LocalSessionFactoryBuilder( dataSource() ).scanPackages( "ru.ssau.domain" ).addProperties(
                hibernateProperties() )
                // используем устаревший метод, так как Spring не оставляет нам выбора
                .buildSessionFactory();
    }

    /**
     * Менеджер транзакций
     */
    @Bean
    public HibernateTransactionManager transactionManager( SessionFactory sessionFactory ){
        HibernateTransactionManager htm = new HibernateTransactionManager();
        htm.setSessionFactory( sessionFactory );
        return htm;
    }


}
