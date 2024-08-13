package sit.int221.BEOR2;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {
    @Primary
    @Bean(name = "itbkk_v2")
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource itbkkV2DataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "itbkk_shared")
    @ConfigurationProperties(prefix = "spring.datasource-share")
    public DataSource itbkkSharedDataSource() {
        return DataSourceBuilder.create().build();
    }
}
