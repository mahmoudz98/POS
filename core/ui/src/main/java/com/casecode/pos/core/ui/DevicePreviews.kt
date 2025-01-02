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
package com.casecode.pos.core.ui

import androidx.compose.ui.tooling.preview.Devices.TABLET
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "phone", device = "spec:shape=Normal,width=360,height=640,unit=dp,dpi=480")
@Preview(
    name = "phone - ar",
    locale = "ar",
    device = "spec:shape=Normal,width=360,height=640,unit=dp,dpi=480",
    uiMode = android.content.res.Configuration.SCREENLAYOUT_LAYOUTDIR_RTL,
)
@Preview(
    name = "phone - landscape",
    device = "spec:width=360dp, height=640dp, orientation=landscape, dpi=420",
)
@Preview(name = "foldable", device = "spec:shape=Normal,width=673,height=841,unit=dp,dpi=480")
@Preview(name = "tablet", device = "spec:shape=Normal,width=1280,height=800,unit=dp,dpi=480")
annotation class DevicePreviews

@Preview(
    name = "Phone - Landscape",
    device = "spec:width=411dp,height=891dp,orientation=landscape,dpi=420",
)
@Preview(
    name = "Phone - Landscape - ar",
    locale = "ar",
    device = "spec:width=411dp,height=891dp,orientation=landscape,dpi=420",
    uiMode = android.content.res.Configuration.SCREENLAYOUT_LAYOUTDIR_RTL,
)
@Preview(name = "Tablet", device = TABLET, showSystemUi = true)
annotation class DeviceLandscapePreviews