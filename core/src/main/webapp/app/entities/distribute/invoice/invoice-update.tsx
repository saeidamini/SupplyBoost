import React, { useState, useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Row, Col, FormText } from 'reactstrap';
import { isNumber, Translate, translate, ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { IInvoice } from 'app/shared/model/distribute/invoice.model';
import { InvoiceStatus } from 'app/shared/model/enumerations/invoice-status.model';
import { PaymentMethod } from 'app/shared/model/enumerations/payment-method.model';
import { getEntity, updateEntity, createEntity, reset } from './invoice.reducer';

export const InvoiceUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const invoiceEntity = useAppSelector(state => state.core.invoice.entity);
  const loading = useAppSelector(state => state.core.invoice.loading);
  const updating = useAppSelector(state => state.core.invoice.updating);
  const updateSuccess = useAppSelector(state => state.core.invoice.updateSuccess);
  const invoiceStatusValues = Object.keys(InvoiceStatus);
  const paymentMethodValues = Object.keys(PaymentMethod);

  const handleClose = () => {
    navigate('/invoice' + location.search);
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
    values.date = convertDateTimeToServer(values.date);
    values.paymentDate = convertDateTimeToServer(values.paymentDate);

    const entity = {
      ...invoiceEntity,
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
      ? {
          date: displayDefaultDateTime(),
          paymentDate: displayDefaultDateTime(),
        }
      : {
          status: 'PAID',
          paymentMethod: 'CREDIT_CARD',
          ...invoiceEntity,
          date: convertDateTimeFromServer(invoiceEntity.date),
          paymentDate: convertDateTimeFromServer(invoiceEntity.paymentDate),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="coreApp.distributeInvoice.home.createOrEditLabel" data-cy="InvoiceCreateUpdateHeading">
            <Translate contentKey="coreApp.distributeInvoice.home.createOrEditLabel">Create or edit a Invoice</Translate>
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
                  id="invoice-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('coreApp.distributeInvoice.code')}
                id="invoice-code"
                name="code"
                data-cy="code"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('coreApp.distributeInvoice.date')}
                id="invoice-date"
                name="date"
                data-cy="date"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('coreApp.distributeInvoice.details')}
                id="invoice-details"
                name="details"
                data-cy="details"
                type="text"
              />
              <ValidatedField
                label={translate('coreApp.distributeInvoice.status')}
                id="invoice-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {invoiceStatusValues.map(invoiceStatus => (
                  <option value={invoiceStatus} key={invoiceStatus}>
                    {translate('coreApp.InvoiceStatus.' + invoiceStatus)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('coreApp.distributeInvoice.paymentMethod')}
                id="invoice-paymentMethod"
                name="paymentMethod"
                data-cy="paymentMethod"
                type="select"
              >
                {paymentMethodValues.map(paymentMethod => (
                  <option value={paymentMethod} key={paymentMethod}>
                    {translate('coreApp.PaymentMethod.' + paymentMethod)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('coreApp.distributeInvoice.paymentDate')}
                id="invoice-paymentDate"
                name="paymentDate"
                data-cy="paymentDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('coreApp.distributeInvoice.paymentAmount')}
                id="invoice-paymentAmount"
                name="paymentAmount"
                data-cy="paymentAmount"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  validate: v => isNumber(v) || translate('entity.validation.number'),
                }}
              />
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/invoice" replace color="info">
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

export default InvoiceUpdate;
