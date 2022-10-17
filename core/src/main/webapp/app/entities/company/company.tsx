import React, { useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Input, InputGroup, FormGroup, Form, Row, Col, Table } from 'reactstrap';
import { Translate, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { ICompany } from 'app/shared/model/company.model';
import { searchEntities, getEntities } from './company.reducer';

export const Company = () => {
  const dispatch = useAppDispatch();

  const location = useLocation();
  const navigate = useNavigate();

  const [search, setSearch] = useState('');

  const companyList = useAppSelector(state => state.core.company.entities);
  const loading = useAppSelector(state => state.core.company.loading);

  useEffect(() => {
    dispatch(getEntities({}));
  }, []);

  const startSearching = e => {
    if (search) {
      dispatch(searchEntities({ query: search }));
    }
    e.preventDefault();
  };

  const clear = () => {
    setSearch('');
    dispatch(getEntities({}));
  };

  const handleSearch = event => setSearch(event.target.value);

  const handleSyncList = () => {
    dispatch(getEntities({}));
  };

  return (
    <div>
      <h2 id="company-heading" data-cy="CompanyHeading">
        <Translate contentKey="coreApp.company.home.title">Companies</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="coreApp.company.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/company/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="coreApp.company.home.createLabel">Create new Company</Translate>
          </Link>
        </div>
      </h2>
      <Row>
        <Col sm="12">
          <Form onSubmit={startSearching}>
            <FormGroup>
              <InputGroup>
                <Input
                  type="text"
                  name="search"
                  defaultValue={search}
                  onChange={handleSearch}
                  placeholder={translate('coreApp.company.home.search')}
                />
                <Button className="input-group-addon">
                  <FontAwesomeIcon icon="search" />
                </Button>
                <Button type="reset" className="input-group-addon" onClick={clear}>
                  <FontAwesomeIcon icon="trash" />
                </Button>
              </InputGroup>
            </FormGroup>
          </Form>
        </Col>
      </Row>
      <div className="table-responsive">
        {companyList && companyList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="coreApp.company.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="coreApp.company.firstName">First Name</Translate>
                </th>
                <th>
                  <Translate contentKey="coreApp.company.lastName">Last Name</Translate>
                </th>
                <th>
                  <Translate contentKey="coreApp.company.gender">Gender</Translate>
                </th>
                <th>
                  <Translate contentKey="coreApp.company.email">Email</Translate>
                </th>
                <th>
                  <Translate contentKey="coreApp.company.phone">Phone</Translate>
                </th>
                <th>
                  <Translate contentKey="coreApp.company.addressLine1">Address Line 1</Translate>
                </th>
                <th>
                  <Translate contentKey="coreApp.company.addressLine2">Address Line 2</Translate>
                </th>
                <th>
                  <Translate contentKey="coreApp.company.city">City</Translate>
                </th>
                <th>
                  <Translate contentKey="coreApp.company.country">Country</Translate>
                </th>
                <th>
                  <Translate contentKey="coreApp.company.companyType">Company Type</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {companyList.map((company, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/company/${company.id}`} color="link" size="sm">
                      {company.id}
                    </Button>
                  </td>
                  <td>{company.firstName}</td>
                  <td>{company.lastName}</td>
                  <td>
                    <Translate contentKey={`coreApp.Gender.${company.gender}`} />
                  </td>
                  <td>{company.email}</td>
                  <td>{company.phone}</td>
                  <td>{company.addressLine1}</td>
                  <td>{company.addressLine2}</td>
                  <td>{company.city}</td>
                  <td>{company.country}</td>
                  <td>
                    <Translate contentKey={`coreApp.CompanyType.${company.companyType}`} />
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/company/${company.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/company/${company.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`/company/${company.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="coreApp.company.home.notFound">No Companies found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

export default Company;
