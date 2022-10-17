import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { ICompany } from 'app/shared/model/company.model';
import { Gender } from 'app/shared/model/enumerations/gender.model';
import { CompanyType } from 'app/shared/model/enumerations/company-type.model';
import { getEntity, updateEntity, createEntity, reset } from './company.reducer';

export const CompanyUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const companyEntity = useAppSelector(state => state.core.company.entity);
  const loading = useAppSelector(state => state.core.company.loading);
  const updating = useAppSelector(state => state.core.company.updating);
  const updateSuccess = useAppSelector(state => state.core.company.updateSuccess);
  const genderValues = Object.keys(Gender);
  const companyTypeValues = Object.keys(CompanyType);

  const handleClose = () => {
    navigate('/company');
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    const entity = {
      ...companyEntity,
      ...values,
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          gender: 'MALE',
          companyType: 'SUPPLIER',
          ...companyEntity,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="coreApp.company.home.createOrEditLabel" data-cy="CompanyCreateUpdateHeading">
            <Translate contentKey="coreApp.company.home.createOrEditLabel">Create or edit a Company</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="company-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('coreApp.company.firstName')}
                id="company-firstName"
                name="firstName"
                data-cy="firstName"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('coreApp.company.lastName')}
                id="company-lastName"
                name="lastName"
                data-cy="lastName"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField label={translate('coreApp.company.gender')} id="company-gender" name="gender" data-cy="gender" type="select">
                {genderValues.map(gender => (
                  <option value={gender} key={gender}>
                    {translate('coreApp.Gender.' + gender)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('coreApp.company.email')}
                id="company-email"
                name="email"
                data-cy="email"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  pattern: {
                    value: /^[^@\s]+@[^@\s]+\.[^@\s]+$/,
                    message: translate('entity.validation.pattern', { pattern: '^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$' }),
                  },
                }}
              />
              <ValidatedField
                label={translate('coreApp.company.phone')}
                id="company-phone"
                name="phone"
                data-cy="phone"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('coreApp.company.addressLine1')}
                id="company-addressLine1"
                name="addressLine1"
                data-cy="addressLine1"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('coreApp.company.addressLine2')}
                id="company-addressLine2"
                name="addressLine2"
                data-cy="addressLine2"
                type="text"
              />
              <ValidatedField
                label={translate('coreApp.company.city')}
                id="company-city"
                name="city"
                data-cy="city"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('coreApp.company.country')}
                id="company-country"
                name="country"
                data-cy="country"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('coreApp.company.companyType')}
                id="company-companyType"
                name="companyType"
                data-cy="companyType"
                type="select"
              >
                {companyTypeValues.map(companyType => (
                  <option value={companyType} key={companyType}>
                    {translate('coreApp.CompanyType.' + companyType)}
                  </option>
                ))}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/company" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default CompanyUpdate;
