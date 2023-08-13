package org.smartmade.supplyboost.notification;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;
import org.smartmade.supplyboost.notification.MongoDbTestContainerExtension;
import org.smartmade.supplyboost.notification.NotificationApp;
import org.smartmade.supplyboost.notification.config.TestSecurityConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { NotificationApp.class, TestSecurityConfiguration.class })
@ExtendWith(MongoDbTestContainerExtension.class)
public @interface IntegrationTest {
}
