import dayjs from 'dayjs';
import { IInvoice } from 'app/shared/model/distribute/invoice.model';

export interface IShipment {
  id?: number;
  trackingCode?: string | null;
  date?: string;
  details?: string | null;
  invoice?: IInvoice;
}

export const defaultValue: Readonly<IShipment> = {};
