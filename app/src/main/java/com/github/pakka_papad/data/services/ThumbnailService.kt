package com.github.pakka_papad.data.services

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import androidx.core.graphics.scale
import androidx.core.net.toUri
import java.io.File

interface ThumbnailService {
    fun createThumbnail(imageUris: List<String?>): String?
}

class ThumbnailServiceImpl(
    private val context: Context,
): ThumbnailService {

    companion object {
        private const val IMAGE_SIZE = 1600
        private const val PARTS = 3
        private const val DEGREES = 9f
        private const val THUMBNAIL_DIR = "thumbnails"
    }

    override fun createThumbnail(imageUris: List<String?>): String? {
        val options = BitmapFactory.Options().apply {
            outHeight = 200
            outWidth = 200
        }
        val bitmaps = imageUris.asSequence()
            .filterNotNull()
            .mapNotNull {
                BitmapFactory.decodeStream(
                    context.contentResolver.openInputStream(it.toUri()),
                    null,
                    options
                )
            }
            .take(9)
            .toList()
        if (bitmaps.isEmpty()) return null
        val arranged = arrangeBitmaps(bitmaps)
        val mergedImage = create(arranged, IMAGE_SIZE, PARTS)
        val finalImage = rotate(mergedImage, IMAGE_SIZE, DEGREES)
        val result = saveImage(finalImage)
        finalImage.recycle()
        mergedImage.recycle()
        return result
    }

    private fun saveImage(image: Bitmap): String? {
        val dir = File(context.filesDir, THUMBNAIL_DIR)
        if (!dir.exists()) {
            val created = dir.mkdir()
            if (!created) return null
        }
        val file = File(dir, "thumb-${System.currentTimeMillis()}.png")
        val result = image.compress(Bitmap.CompressFormat.PNG, 100, file.outputStream())
        if (!result) return null
        return file.path
    }

    private fun arrangeBitmaps(list: List<Bitmap>): List<Bitmap> {
        return when {
            list.size == 1 -> {
                val item = list[0]
                listOf(item, item, item, item, item, item, item, item, item)
            }
            list.size == 2 -> {
                val item1 = list[0]
                val item2 = list[1]
                listOf(item1, item2, item1, item2, item1, item2, item1, item2, item1)
            }
            list.size == 3 -> {
                val item1 = list[0]
                val item2 = list[1]
                val item3 = list[2]
                listOf(item1, item2, item3, item3, item1, item2, item2, item3, item1)
            }
            list.size == 4 -> {
                val item1 = list[0]
                val item2 = list[1]
                val item3 = list[2]
                val item4 = list[3]
                listOf(item1, item2, item3, item4, item1, item2, item3, item4, item1)
            }
            list.size < 9 -> { // 5 to 8
                val item1 = list[0]
                val item2 = list[1]
                val item3 = list[2]
                val item4 = list[3]
                val item5 = list[4]
                listOf(item1, item2, item3, item4, item5, item2, item3, item4, item1)
            }
            else -> list // case 9
        }
    }

    private fun create(images: List<Bitmap>, imageSize: Int, parts: Int): Bitmap {
        val result = Bitmap.createBitmap(imageSize, imageSize, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val onePartSize = imageSize / parts

        images.forEachIndexed { i, bitmap ->
            val bit = bitmap.scale(onePartSize, onePartSize)
            canvas.drawBitmap(
                bit,
                (onePartSize * (i % parts)).toFloat() + (i % 3) * 50,
                (onePartSize * (i / parts)).toFloat() + (i / 3) * 50,
                paint
            )
            bit.recycle()
        }
        return result
    }

    private fun rotate(bitmap: Bitmap, imageSize: Int, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)

        val rotated = Bitmap.createBitmap(bitmap, 0, 0, imageSize, imageSize, matrix, true)
        bitmap.recycle()
        val cropStart = imageSize * 25 / 100
        val cropEnd: Int = (cropStart * 1.5).toInt()
        val cropped = Bitmap.createBitmap(
            rotated,
            cropStart,
            cropStart,
            imageSize - cropEnd,
            imageSize - cropEnd
        )
        rotated.recycle()

        return cropped
    }

}