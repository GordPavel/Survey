package ru.ssau.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableJpaRepositories( basePackages = { "ru.ssau.domain" } )
@EnableTransactionManagement
public class PersistenceContext{

    @Autowired
    private Environment environment;

    @Bean( destroyMethod = "close" )
    public DataSource dataSource(){
        HikariConfig dataSourceConfig = new HikariConfig();
        dataSourceConfig.setDriverClassName( environment.getRequiredProperty( "jdbc.driverClassName" ) );
        dataSourceConfig.setJdbcUrl( environment.getRequiredProperty( "jdbc.url" ) );
        dataSourceConfig.setUsername( environment.getRequiredProperty( "jdbc.username" ) );
        dataSourceConfig.setPassword( environment.getRequiredProperty( "jdbc.password" ) );
        return new HikariDataSource( dataSourceConfig );
    }

    @Bean
    LocalContainerEntityManagerFactoryBean entityManagerFactory( DataSource dataSource ){
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource( dataSource );
        entityManagerFactoryBean.setJpaVendorAdapter( new HibernateJpaVendorAdapter() );
        entityManagerFactoryBean.setPackagesToScan( "ru.ssau.domain" );

        Properties jpaProperties = new Properties();

        //Configures the used database dialect. This allows Hibernate to create SQL
        //that is optimized for the used database.
        jpaProperties.put( "hibernate.dialect", environment.getRequiredProperty( "hibernate.dialect" ) );

        //Specifies the action that is invoked to the database when the Hibernate
        //SessionFactory is created or closed.
        jpaProperties.put( "hibernate.hbm2ddl.auto", environment.getRequiredProperty( "hibernate.hbm2ddl.auto" ) );

        //Configures the naming strategy that is used when Hibernate creates
        //new database objects and schema elements
        jpaProperties.put( "hibernate.ejb.naming_strategy",
                           environment.getRequiredProperty( "hibernate.ejb.naming_strategy" ) );

        //If the value of this property is true, Hibernate writes all SQL
        //statements to the console.
        jpaProperties.put( "hibernate.show_sql", environment.getRequiredProperty( "hibernate.show_sql" ) );

        //If the value of this property is true, Hibernate will format the SQL
        //that is written to the console.
        jpaProperties.put( "hibernate.format_sql", environment.getRequiredProperty( "hibernate.format_sql" ) );
        entityManagerFactoryBean.setJpaProperties( jpaProperties );

        return entityManagerFactoryBean;
    }

    @Bean
    JpaTransactionManager transactionManager( EntityManagerFactory entityManagerFactory ){
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory( entityManagerFactory );
        return transactionManager;
    }

}
