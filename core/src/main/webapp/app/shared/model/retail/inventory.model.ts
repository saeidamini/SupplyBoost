import { IProduct } from 'app/shared/model/retail/product.model';

export interface IInventory {
  id?: number;
  amount?: number;
  product?: IProduct;
}

export const defaultValue: Readonly<IInventory> = {};
