package org.smartmade.supplyboost.core.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.smartmade.supplyboost.core.web.rest.TestUtil;

class CompanyTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Company.class);
        Company company1 = new Company();
        company1.setId(1L);
        Company company2 = new Company();
        company2.setId(company1.getId());
        assertThat(company1).isEqualTo(company2);
        company2.setId(2L);
        assertThat(company1).isNotEqualTo(company2);
        company1.setId(null);
        assertThat(company1).isNotEqualTo(company2);
    }
}
