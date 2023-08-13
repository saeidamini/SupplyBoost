import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './company.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const CompanyDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const companyEntity = useAppSelector(state => state.company.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="companyDetailsHeading">
          <Translate contentKey="coreApp.company.detail.title">Company</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{companyEntity.id}</dd>
          <dt>
            <span id="firstName">
              <Translate contentKey="coreApp.company.firstName">First Name</Translate>
            </span>
          </dt>
          <dd>{companyEntity.firstName}</dd>
          <dt>
            <span id="lastName">
              <Translate contentKey="coreApp.company.lastName">Last Name</Translate>
            </span>
          </dt>
          <dd>{companyEntity.lastName}</dd>
          <dt>
            <span id="gender">
              <Translate contentKey="coreApp.company.gender">Gender</Translate>
            </span>
          </dt>
          <dd>{companyEntity.gender}</dd>
          <dt>
            <span id="email">
              <Translate contentKey="coreApp.company.email">Email</Translate>
            </span>
          </dt>
          <dd>{companyEntity.email}</dd>
          <dt>
            <span id="phone">
              <Translate contentKey="coreApp.company.phone">Phone</Translate>
            </span>
          </dt>
          <dd>{companyEntity.phone}</dd>
          <dt>
            <span id="addressLine1">
              <Translate contentKey="coreApp.company.addressLine1">Address Line 1</Translate>
            </span>
          </dt>
          <dd>{companyEntity.addressLine1}</dd>
          <dt>
            <span id="addressLine2">
              <Translate contentKey="coreApp.company.addressLine2">Address Line 2</Translate>
            </span>
          </dt>
          <dd>{companyEntity.addressLine2}</dd>
          <dt>
            <span id="city">
              <Translate contentKey="coreApp.company.city">City</Translate>
            </span>
          </dt>
          <dd>{companyEntity.city}</dd>
          <dt>
            <span id="country">
              <Translate contentKey="coreApp.company.country">Country</Translate>
            </span>
          </dt>
          <dd>{companyEntity.country}</dd>
          <dt>
            <span id="companyType">
              <Translate contentKey="coreApp.company.companyType">Company Type</Translate>
            </span>
          </dt>
          <dd>{companyEntity.companyType}</dd>
        </dl>
        <Button tag={Link} to="/company" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/company/${companyEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CompanyDetail;
