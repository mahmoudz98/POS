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
package com.casecode.pos.feature.item

import androidx.annotation.StringRes

enum class FilterStockState(
    @StringRes val label: Int,
) {
    All(label = R.string.feature_item_filter_all_label),
    InStock(label = R.string.feature_item_filter_in_stock_label),
    OutOfStock(label = R.string.feature_item_filter_out_stock_label),
    LowLevelStock(label = R.string.feature_item_filter_low_stock_label),
}

enum class SortPriceState(
    @StringRes val label: Int,
) {
    None(label = R.string.feature_item_sort_price_none_label),
    LowToHigh(label = R.string.feature_item_sort_price_low_to_high_label),
    HighToLow(label = R.string.feature_item_sort_price_high_to_low_label),
}