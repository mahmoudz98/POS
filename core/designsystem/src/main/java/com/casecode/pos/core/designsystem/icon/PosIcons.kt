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
package com.casecode.pos.core.designsystem.icon

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ShortText
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HideImage
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.PrintDisabled
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.StackedLineChart
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.SupervisedUserCircle
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material.icons.outlined.Print
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.FilterListOff
import androidx.compose.material.icons.rounded.Inventory2
import androidx.compose.material.icons.rounded.PointOfSale
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.ShoppingBag
import androidx.compose.material.icons.rounded.StackedLineChart
import androidx.compose.material.icons.rounded.ViewDay
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * POS icons. Material icons are [ImageVector]s, custom icons are drawable resource IDs.
 */
object PosIcons {
    val Menu = Icons.Filled.Menu
    val Pos = Icons.Filled.PointOfSale
    val PosBorder = Icons.Rounded.PointOfSale
    val Reports = Icons.Filled.StackedLineChart
    val ReportsBorder = Icons.Rounded.StackedLineChart
    val Invoices = Icons.Filled.Receipt
    val Items = Icons.Filled.Store
    val Inventory = Icons.Filled.Inventory2
    val InventoryBorder = Icons.Rounded.Inventory2
    val Supplier = Icons.Filled.SupportAgent
    val Purchase = Icons.Filled.ShoppingBag
    val PurchaseBorder = Icons.Rounded.ShoppingBag
    val Bill = Icons.Filled.Receipt
    val Employee = Icons.Filled.SupervisorAccount
    val Settings = Icons.Filled.Settings
    val SettingsBorder = Icons.Rounded.Settings
    val SignOut = Icons.AutoMirrored.Filled.Logout
    val EmptyImage = Icons.Filled.HideImage
    val EmptySearch = Icons.Filled.ImageSearch
    val Print = Icons.Outlined.Print
    val QrCodeScanner = Icons.Filled.QrCodeScanner
    val QrCodeError = Icons.Filled.QrCode2
    val Search = Icons.Rounded.Search
    val Add = Icons.Rounded.Add
    val Filter = Icons.Rounded.FilterList
    val FilterClear = Icons.Rounded.FilterListOff

    val Calender = Icons.Filled.CalendarToday
    val Delete = Icons.Filled.Delete
    val PrinterDisabled = Icons.Filled.PrintDisabled
    val UserAdman = Icons.Filled.SupervisedUserCircle
    val Placeholder = Icons.Filled.Image
    val Email = Icons.Filled.Email
    val Phone = Icons.Filled.Phone

    val ArrowBack = Icons.AutoMirrored.Rounded.ArrowBack
    val Bookmark = Icons.Rounded.Bookmark
    val BookmarkBorder = Icons.Rounded.BookmarkBorder
    val Check = Icons.Rounded.Check
    val Track = Icons.Filled.TrackChanges
    val Close = Icons.Rounded.Close
    val MoreVert = Icons.Default.MoreVert
    val ShortText = Icons.AutoMirrored.Rounded.ShortText
    val ViewDay = Icons.Rounded.ViewDay
    val AddPhoto = Icons.Filled.AddAPhoto
}