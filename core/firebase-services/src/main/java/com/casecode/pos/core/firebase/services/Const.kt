/*
 * Designed and developed 2024 by Mahmood Abdalhafeez
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://opensource.org/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.casecode.pos.core.firebase.services

// collection path
const val USERS_COLLECTION_PATH = "users"
const val ITEMS_COLLECTION_PATH = "items"
const val SUPPLIERS_COLLECTION_PATH = "suppliers"
const val SUPPLIER_INVOICES_COLLECTION_PATH = "suppliersInvoice"
const val PRINTER_INFO_COLLECTION_PATH = "printerInfo"
const val CUSTOMERS_COLLECTION_PATH = "customers"
const val BUSINESS_FIELD = "business"
const val BRANCHES_FIELD = "branches"
const val SUBSCRIPTIONS_COLLECTION_PATH = "subscriptions"

// Fields in customers:
const val CUSTOMER_CODE_FIELD = "customerCode"
const val CUSTOMER_NAME_FIELD = "name"

// business
const val BUSINESS_STORE_TYPE_FIELD = "storeType"
const val BUSINESS_IS_COMPLETED_STEP_FIELD = "isCompletedStep"
const val BUSINESS_EMAIL_FIELD = "email"
const val BUSINESS_PHONE_NUMBER_FIELD = "phoneNumber"

// branches
const val BRANCHES_CODE_FIELD = "branchCode"
const val BRANCHES_NAME_FIELD = "branchName"
const val BRANCHES_PHONE_NUMBER_FIELD = "phoneNumber"

// fields in subscriptions
const val SUBSCRIPTION_BUSINESS_FIELD = "subscriptionsBusiness"
const val SUBSCRIPTION_COST_FIELD = "cost"
const val SUBSCRIPTION_TYPE_FIELD = "type"
const val SUBSCRIPTION_DURATION_FIELD = "duration"
const val SUBSCRIPTION_PERMISSIONS_FIELD = "permissions"

// fields in Employees
const val EMPLOYEES_FIELD = "employees"
const val EMPLOYEE_NAME_FIELD = "name"
const val EMPLOYEE_PHONE_NUMBER_FIELD = "phoneNumber"
const val EMPLOYEE_PASSWORD_FIELD = "password"
const val EMPLOYEE_BRANCH_NAME_FIELD = "branchName"
const val EMPLOYEE_PERMISSION_FIELD = "permission"

// fields in item.
const val ITEM_NAME_FIELD = "name"
const val ITEM_CATEGORY_FIELD = "category"
const val ITEM_SUPPLIER_NAME_FIELD = "supplier_name"
const val ITEM_COST_PRICE_FIELD = "cost_price"
const val ITEM_PRICE_FIELD = "price"
const val ITEM_QUANTITY_FIELD = "quantity"
const val ITEM_REORDER_LEVEL_FIELD = "reorder_level"
const val ITEM_QTY_PER_PACK_FIELD = "qty_per_pack"
const val ITEM_PACK_NAME_FIELD = "pack_name"
const val ITEM_SKU_FIELD = "sku"
const val ITEM_IMAGE_URL_FIELD = "image_url"
const val ITEM_UNIT_OF_MEASUREMENT_FIELD = "unitOfMeasurement"
const val ITEM_DELETED_FIELD = "deleted"

// fields in supplier.
const val SUPPLIER_ID_FIELD = "id"
const val SUPPLIER_COMPANY_NAME_FIELD = "companyName"
const val SUPPLIER_CONTACT_NAME_FIELD = "contactName"
const val SUPPLIER_CONTACT_EMAIL_FIELD = "contactEmail"
const val SUPPLIER_CONTACT_PHONE_FIELD = "contactPhone"
const val SUPPLIER_ADDRESS_FIELD = "address"
const val SUPPLIER_CATEGORY_FIELD = "category"

// Fields in supplier invoices
const val SUPPLIER_INVOICE_ID_FIELD = "invoiceId"
const val SUPPLIER_INVOICE_BILL_NUMBER_FIELD = "billNumber"
const val SUPPLIER_INVOICE_SUPPLIER_ID_FIELD = "supplierId"
const val SUPPLIER_INVOICE_ISSUE_DATE_FIELD = "issueDate"
const val SUPPLIER_INVOICE_DUE_DATE_FIELD = "dueDate"
const val SUPPLIER_INVOICE_CREATED_BY_FIELD = "createdBy"
const val SUPPLIER_INVOICE_TOTAL_AMOUNT_FIELD = "totalAmount"
const val SUPPLIER_INVOICE_DISCOUNT_TYPE_FIELD = "discountType"
const val SUPPLIER_INVOICE_AMOUNT_DISCOUNTED_FIELD = "amountDiscount"
const val SUPPLIER_INVOICE_PAYMENT_DETAILS_FIELD = "paymentDetails"
const val SUPPLIER_INVOICE_PAYMENT_STATUS_FIELD = "paymentStatus"
const val SUPPLIER_INVOICE_ITEMS_FIELD = "invoiceItems"

// fields in payment details of supplier invoice
const val SUPPLIER_INVOICE_PAYMENT_DETAILS_ID_FIELD = "paymentId"
const val SUPPLIER_INVOICE_PAYMENT_DETAILS_DATE_FIELD = "paymentDate"
const val SUPPLIER_INVOICE_PAYMENT_DETAILS_CREATED_BY_FIELD = "createdBy"
const val SUPPLIER_INVOICE_PAYMENT_DETAILS_METHOD_FIELD = "paymentMethod"
const val SUPPLIER_INVOICE_PAYMENT_DETAILS_AMOUNT_PAID_FIELD = "amountPaid"

// fields in Invoice.
const val INVOICE_FIELD = "invoices"
const val INVOICE_NAME_FIELD = "number"
const val INVOICE_DATE_FIELD = "date"
const val INVOICE_CREATED_BY_FIELD = "createdBy"
const val INVOICE_CUSTOMER_FIELD = "customer"
const val INVOICE_ITEMS_FIELD = "invoiceItems"

const val ITEM_PATH_FIELD = "item"
const val IMAGES_PATH_FIELD = "images"

// Fields in PrinterInfo
const val PRINTER_INFO_NAME_FIELD = "name"
const val PRINTER_INFO_NAME_DEVICE_FIELD = "nameDevice"
const val PRINTER_INFO_USB_NAME_DEVICE_FIELD = "nameUsbDevice"
const val PRINTER_INFO_CONNECTION_TYPE_FIELD = "connectionType"
const val PRINTER_INFO_ADDRESS_FIELD = "address"
const val PRINTER_INFO_PORT_FIELD = "port"
const val PRINTER_INFO_IS_CURRENT_SELECTED_FIELD = "isCurrentSelected"
const val PRINTER_INFO_SIZE_FIELD = "size"