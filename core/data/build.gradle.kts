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
    alias(libs.plugins.pos.android.library.jacoco)
    alias(libs.plugins.pos.hilt)
}
android {
    namespace = "com.casecode.pos.core.data"

    @Suppress("UnstableApiUsage")
    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }

}

dependencies {

    api(projects.core.domain)
    api(projects.core.common)
    api(projects.core.datastore)
    implementation(projects.core.firebaseServices)

    implementation(libs.coroutines.android)

    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    // implementation(libs.billing.ktx)

    implementation(libs.hilt.android)
    implementation(libs.zxing.generate.barcode)


    testImplementation(projects.core.testing)
    testImplementation(libs.coroutines.test)

}