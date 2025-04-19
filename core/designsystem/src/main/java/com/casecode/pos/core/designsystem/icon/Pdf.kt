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
import androidx.compose.material.icons.materialIcon
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType.Companion.NonZero
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap.Companion.Round
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.StrokeJoin.Companion.Miter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path

internal val Icons.Filled.Pdf: ImageVector
    get() {
        if (_pdf != null) {
            return _pdf!!
        }
        _pdf =
            materialIcon(name = "Pdf") {
                path(
                    fill = SolidColor(Color(0x00000000)),
                    stroke = SolidColor(Color(0xFF200E32)),
                    strokeLineWidth = 2.0f,
                    strokeLineCap = Round,
                    strokeLineJoin = Miter,
                    strokeLineMiter = 4.0f,
                    pathFillType = NonZero,
                ) {
                    moveTo(4.0f, 4.0f)
                    curveTo(4.0f, 3.448f, 4.448f, 3.0f, 5.0f, 3.0f)
                    horizontalLineTo(14.0f)
                    horizontalLineTo(14.586f)
                    curveTo(14.851f, 3.0f, 15.105f, 3.105f, 15.293f, 3.293f)
                    lineTo(19.707f, 7.707f)
                    curveTo(19.895f, 7.895f, 20.0f, 8.149f, 20.0f, 8.414f)
                    verticalLineTo(20.0f)
                    curveTo(20.0f, 20.552f, 19.552f, 21.0f, 19.0f, 21.0f)
                    horizontalLineTo(5.0f)
                    curveTo(4.448f, 21.0f, 4.0f, 20.552f, 4.0f, 20.0f)
                    verticalLineTo(4.0f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0x00000000)),
                    stroke = SolidColor(Color(0xFF200E32)),
                    strokeLineWidth = 2.0f,
                    strokeLineCap = Round,
                    strokeLineJoin =
                    StrokeJoin.Companion.Round,
                    strokeLineMiter = 4.0f,
                    pathFillType = NonZero,
                ) {
                    moveTo(20.0f, 8.0f)
                    horizontalLineTo(15.0f)
                    verticalLineTo(3.0f)
                }
                path(
                    fill = SolidColor(Color(0x00000000)),
                    stroke = SolidColor(Color(0xFF200E32)),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = Round,
                    strokeLineJoin =
                    StrokeJoin.Companion.Round,
                    strokeLineMiter = 4.0f,
                    pathFillType = NonZero,
                ) {
                    moveTo(11.5f, 13.0f)
                    horizontalLineTo(11.0f)
                    verticalLineTo(17.0f)
                    horizontalLineTo(11.5f)
                    curveTo(12.605f, 17.0f, 13.5f, 16.105f, 13.5f, 15.0f)
                    curveTo(13.5f, 13.895f, 12.605f, 13.0f, 11.5f, 13.0f)
                    close()
                }
                path(
                    fill = SolidColor(Color(0x00000000)),
                    stroke = SolidColor(Color(0xFF200E32)),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = Round,
                    strokeLineJoin =
                    StrokeJoin.Companion.Round,
                    strokeLineMiter = 4.0f,
                    pathFillType = NonZero,
                ) {
                    moveTo(15.5f, 17.0f)
                    verticalLineTo(13.0f)
                    lineTo(17.5f, 13.0f)
                }
                path(
                    fill = SolidColor(Color(0x00000000)),
                    stroke = SolidColor(Color(0xFF200E32)),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = Round,
                    strokeLineJoin =
                    StrokeJoin.Companion.Round,
                    strokeLineMiter = 4.0f,
                    pathFillType = NonZero,
                ) {
                    moveTo(16.0f, 15.0f)
                    horizontalLineTo(17.0f)
                }
                path(
                    fill = SolidColor(Color(0x00000000)),
                    stroke = SolidColor(Color(0xFF200E32)),
                    strokeLineWidth = 1.5f,
                    strokeLineCap = Round,
                    strokeLineJoin =
                    StrokeJoin.Companion.Round,
                    strokeLineMiter = 4.0f,
                    pathFillType = NonZero,
                ) {
                    moveTo(7.0f, 17.0f)
                    lineTo(7.0f, 15.5f)
                    moveTo(7.0f, 15.5f)
                    lineTo(7.0f, 13.0f)
                    lineTo(7.75f, 13.0f)
                    curveTo(8.44f, 13.0f, 9.0f, 13.56f, 9.0f, 14.25f)
                    verticalLineTo(14.25f)
                    curveTo(9.0f, 14.94f, 8.44f, 15.5f, 7.75f, 15.5f)
                    horizontalLineTo(7.0f)
                    close()
                }
            }

        return _pdf!!
    }

private var _pdf: ImageVector? = null