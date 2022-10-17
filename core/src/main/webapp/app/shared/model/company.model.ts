import { Gender } from 'app/shared/model/enumerations/gender.model';
import { CompanyType } from 'app/shared/model/enumerations/company-type.model';

export interface ICompany {
  id?: number;
  firstName?: string;
  lastName?: string;
  gender?: Gender;
  email?: string;
  phone?: string;
  addressLine1?: string;
  addressLine2?: string | null;
  city?: string;
  country?: string;
  companyType?: CompanyType;
}

export const defaultValue: Readonly<ICompany> = {};
