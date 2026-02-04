package org.example.touragency.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class HibernateConfig {

    @Bean
    public DataSource dataSource(
            @Value("${db.url}") String url,
            @Value("${db.username}") String username,
            @Value("${db.password}") String password,
            @Value("${db.driver}") String driver,
            @Value("${db.pool.max-size}") int maxPoolSize,
            @Value("${db.pool.min-idle}") int minIdle
    ) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driver);
        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minIdle);

        return new HikariDataSource(config);
    }

    @Bean
    public Properties hibernateProperties(
            @Value("${hibernate.dialect}") String dialect,
            @Value("${hibernate.show_sql}") boolean showSql,
            @Value("${hibernate.format_sql}") boolean formatSql,
            @Value("${hibernate.hbm2ddl.auto}") String ddlAuto
    ) {
        Properties props = new Properties();
        props.put("hibernate.dialect", dialect);
        props.put("hibernate.show_sql", showSql);
        props.put("hibernate.format_sql", formatSql);
        props.put("hibernate.hbm2ddl.auto", ddlAuto);
        return props;
    }

    @Bean
    public SessionFactory sessionFactory(
            DataSource dataSource,
            Properties hibernateProperties
    ) {
        StandardServiceRegistry registry =
                new StandardServiceRegistryBuilder()
                        .applySetting("hibernate.connection.datasource", dataSource)
                        .applySettings(hibernateProperties)
                        .build();

        MetadataSources sources = new MetadataSources(registry);

        sources.addAnnotatedClass(org.example.touragency.model.entity.User.class);
        sources.addAnnotatedClass(org.example.touragency.model.entity.Tour.class);
        sources.addAnnotatedClass(org.example.touragency.model.entity.Booking.class);
        sources.addAnnotatedClass(org.example.touragency.model.entity.FavouriteTour.class);
        sources.addAnnotatedClass(org.example.touragency.model.entity.Rating.class);
        sources.addAnnotatedClass(org.example.touragency.model.entity.RatingCounter.class);
        sources.addAnnotatedClass(org.example.touragency.model.entity.RefreshToken.class);

        return sources.buildMetadata().buildSessionFactory();
    }
}
