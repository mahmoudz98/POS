package com.casecode.data.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter


/**
 * Author: Mahmoud Abdalhafeez
 * Created: 1/5/2024
 * Description:
 */

const val WIDTH = 512
const val HEIGHT = 512

/**
 * Example:
 *       try {
 *             Bitmap bitmap = encodeAsBitmap(STR);
 *             imageView.setImageBitmap(bitmap);
 *         } catch (WriterException ex) {
 *             ex.printStackTrace();
 *         }
 *     }
 */
@Throws(WriterException::class)
fun String.encodeAsBitmap(): Bitmap {
    val writer = QRCodeWriter()
    val bitMatrix: BitMatrix = writer.encode(this, BarcodeFormat.QR_CODE, WIDTH, HEIGHT)

    val w: Int = bitMatrix.width
    val h: Int = bitMatrix.height
    val pixels = IntArray(w * h)
    for (y in 0 until h) {
        for (x in 0 until w) {
            pixels[y * w + x] = if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE
        }
    }

    val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    bitmap.setPixels(pixels, 0, w, 0, 0, w, h)
    return bitmap
}