package com.acuity.visualisations.rest.test.annotation;

import com.acuity.visualisations.rest.test.config.ApplicationBootOnlyWebDisableSecurity;
import com.acuity.visualisations.rest.test.config.DisableAutowireRequiredInitializer;
import org.junit.Ignore;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author glen
 */
// Not working
@Ignore
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, properties = {
        "server.port=0",
        "defaultRolesList=DEFAULT_ROLE"
})
@ContextConfiguration(
        inheritInitializers = true,
        initializers = DisableAutowireRequiredInitializer.class, // this doesnt work
        classes = {ApplicationBootOnlyWebDisableSecurity.class}
)
@WebAppConfiguration()
public @interface ITSpringApplicationConfiguration {
}
