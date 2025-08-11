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
package com.casecode.pos.core.firebase

// Root Collections

// Sub-collections within a Business
const val BRANCHES_SUBCOLLECTION_PATH = "branches"
const val TAX_RATES_SUBCOLLECTION_PATH = "taxRates"
const val SUBSCRIPTION_SUBCOLLECTION_PATH = "subscription"
const val BILLING_EVENTS_SUBCOLLECTION_PATH = "billingEvents"

// Field Names for the Business Document
const val BUSINESS_OWNER_UID_FIELD = "ownerUid"
const val BUSINESS_VERTICAL_FIELD = "vertical"
const val BUSINESS_COMPANY_CODE_FIELD = "companyCode" // More specific than just "businessId"
const val BUSINESS_CURRENCY_CODE_FIELD = "currencyCode"

// Field Names for Subscription and Billing
const val SUB_CURRENT_DOC_ID = "current" // Fixed document ID for the current subscription
const val SUB_PLAN_ID_FIELD = "planId"
const val SUB_CREDIT_BALANCE_FIELD = "creditBalance"
const val BILLING_EVENT_DATE_FIELD = "eventDate"