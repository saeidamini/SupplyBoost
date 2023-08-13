package org.smartmade.supplyboost.distribute;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.smartmade.supplyboost.distribute.DistributeApp;
import org.smartmade.supplyboost.distribute.config.TestSecurityConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { DistributeApp.class, TestSecurityConfiguration.class })
public @interface IntegrationTest {
}
