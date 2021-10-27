package com.acuity.visualisations.rest.util;

import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.filter.HiddenHttpMethodFilter;

import javax.servlet.Filter;
import java.text.SimpleDateFormat;

/**
 *
 * @author ksnd199
 */
@Configuration
public class SpringMVCConfig {

    @Bean
    public Jackson2ObjectMapperBuilder jacksonBuilder() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DaysUtil.JSON_TIMESTAMP_FORMAT);

        Jackson2ObjectMapperBuilder b = new Jackson2ObjectMapperBuilder();
        b.failOnUnknownProperties(false).indentOutput(true).dateFormat(dateFormat);
        return b;
    }

    @Bean
    public Filter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }
}
