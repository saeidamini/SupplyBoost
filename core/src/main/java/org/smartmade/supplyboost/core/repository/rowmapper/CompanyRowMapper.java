package org.smartmade.supplyboost.core.repository.rowmapper;

import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.smartmade.supplyboost.core.domain.Company;
import org.smartmade.supplyboost.core.domain.enumeration.CompanyType;
import org.smartmade.supplyboost.core.domain.enumeration.Gender;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Company}, with proper type conversions.
 */
@Service
public class CompanyRowMapper implements BiFunction<Row, String, Company> {

    private final ColumnConverter converter;

    public CompanyRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Company} stored in the database.
     */
    @Override
    public Company apply(Row row, String prefix) {
        Company entity = new Company();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setFirstName(converter.fromRow(row, prefix + "_first_name", String.class));
        entity.setLastName(converter.fromRow(row, prefix + "_last_name", String.class));
        entity.setGender(converter.fromRow(row, prefix + "_gender", Gender.class));
        entity.setEmail(converter.fromRow(row, prefix + "_email", String.class));
        entity.setPhone(converter.fromRow(row, prefix + "_phone", String.class));
        entity.setAddressLine1(converter.fromRow(row, prefix + "_address_line_1", String.class));
        entity.setAddressLine2(converter.fromRow(row, prefix + "_address_line_2", String.class));
        entity.setCity(converter.fromRow(row, prefix + "_city", String.class));
        entity.setCountry(converter.fromRow(row, prefix + "_country", String.class));
        entity.setCompanyType(converter.fromRow(row, prefix + "_company_type", CompanyType.class));
        return entity;
    }
}
