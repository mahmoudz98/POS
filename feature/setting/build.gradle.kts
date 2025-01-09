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
import com.casecode.pos.Configuration.APPLICATION_ID

plugins {
    alias(libs.plugins.pos.android.feature)
    alias(libs.plugins.pos.android.library.compose)
    alias(libs.plugins.pos.android.library.jacoco)
}

android {
    namespace = "$APPLICATION_ID.feature.setting"
}

dependencies {
    implementation(projects.core.domain)
    implementation(projects.core.printer)

    implementation(libs.appcompat)
    implementation(libs.accompanist.permissions)

    testImplementation(libs.hilt.android.testing)
    testImplementation(projects.core.testing)
    androidTestImplementation(projects.core.testing)
    androidTestImplementation(libs.bundles.androidx.compose.ui.test)
}