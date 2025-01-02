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
plugins {
    alias(libs.plugins.pos.android.library)
    alias(libs.plugins.pos.hilt)
    alias(libs.plugins.pos.android.library.jacoco)
}

android {
    namespace = "com.casecode.pos.core.printer"
}

dependencies {
    implementation(projects.core.model)
    implementation(projects.core.firebaseServices)
    implementation(projects.core.common)
    api(libs.escpos.thermalprinter.android)

    testImplementation(projects.core.testing)
}