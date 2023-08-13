package org.smartmade.supplyboost.retail;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.smartmade.supplyboost.retail.RetailApp;
import org.smartmade.supplyboost.retail.config.TestSecurityConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { RetailApp.class, TestSecurityConfiguration.class })
public @interface IntegrationTest {
}
