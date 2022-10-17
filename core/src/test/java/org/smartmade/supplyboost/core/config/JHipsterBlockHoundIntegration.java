package org.smartmade.supplyboost.core.config;

import reactor.blockhound.BlockHound;
import reactor.blockhound.integration.BlockHoundIntegration;

public class JHipsterBlockHoundIntegration implements BlockHoundIntegration {

    @Override
    public void applyTo(BlockHound.Builder builder) {
        // Workaround until https://github.com/reactor/reactor-core/issues/2137 is fixed
        builder.allowBlockingCallsInside("reactor.core.scheduler.BoundedElasticScheduler$BoundedState", "dispose");
        builder.allowBlockingCallsInside("reactor.core.scheduler.BoundedElasticScheduler", "schedule");
        builder.allowBlockingCallsInside("org.springframework.validation.beanvalidation.SpringValidatorAdapter", "validate");
        builder.allowBlockingCallsInside("org.smartmade.supplyboost.core.service.MailService", "sendEmailFromTemplate");
        builder.allowBlockingCallsInside("org.smartmade.supplyboost.core.security.DomainUserDetailsService", "createSpringSecurityUser");
        builder.allowBlockingCallsInside("org.elasticsearch.client.indices.CreateIndexRequest", "settings");
    }
}
