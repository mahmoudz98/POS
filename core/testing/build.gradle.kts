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
    alias(libs.plugins.pos.android.library)
    alias(libs.plugins.pos.hilt)
}

android {
    namespace = "$APPLICATION_ID.core.testing"
}

dependencies {

    api(projects.core.data)
    api(projects.core.firebaseServices)
    api(projects.core.notifications)
    implementation(libs.googleid)
    api(libs.coroutines.test)
    api(libs.mockk.android) {
        exclude(group = "org.junit.jupiter", module = "junit-jupiter")
    }

    // implementation(libs.mockk.agent)
    implementation(libs.androidx.test.runner)
    implementation(libs.androidx.test.rules)
    implementation(libs.hilt.android.testing)
    // implementation(libs.play.services.auth)
}