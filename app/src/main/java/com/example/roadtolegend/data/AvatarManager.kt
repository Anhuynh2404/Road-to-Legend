package com.example.roadtolegend.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.PanTool
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.GoalCoral
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.PitchGreen
import java.io.File
import java.io.FileOutputStream

data class PresetAvatar(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val themeColor: Color,
    val accentColor: Color
)

object AvatarManager {

    val presetAvatars = listOf(
        PresetAvatar(
            id = "preset_gold_star",
            name = "Ngôi Sao Số 10",
            description = "Biểu tượng huyền thoại rực sáng",
            icon = Icons.Default.Star,
            themeColor = GoldPrimary,
            accentColor = Color(0xFF6B4E00)
        ),
        PresetAvatar(
            id = "preset_striker",
            name = "Sát Thủ Vòng Cấm",
            description = "Bản năng săn bàn huỷ diệt",
            icon = Icons.Default.SportsSoccer,
            themeColor = GoalCoral,
            accentColor = Color(0xFF8B1010)
        ),
        PresetAvatar(
            id = "preset_playmaker",
            name = "Nhạc Trưởng Sân Cỏ",
            description = "Kiến thiết những đường chuyền thiên tài",
            icon = Icons.Default.Bolt,
            themeColor = PitchGreen,
            accentColor = Color(0xFF1B5E20)
        ),
        PresetAvatar(
            id = "preset_shield",
            name = "Lá Chắn Thép",
            description = "Bức tường phòng ngự kiên cố",
            icon = Icons.Default.Security,
            themeColor = Color(0xFF1E88E5),
            accentColor = Color(0xFF0D47A1)
        ),
        PresetAvatar(
            id = "preset_speed",
            name = "Cơn Lốc Đường Biên",
            description = "Tốc độ xé toang mọi hàng thủ",
            icon = Icons.Default.FlashOn,
            themeColor = Color(0xFFFF9800),
            accentColor = Color(0xFFE65100)
        ),
        PresetAvatar(
            id = "preset_keeper",
            name = "Gác Đền Thần Sầu",
            description = "Đôi bàn tay giữ sạch mành lưới",
            icon = Icons.Default.PanTool,
            themeColor = Color(0xFF9C27B0),
            accentColor = Color(0xFF4A148C)
        )
    )

    fun cropCenterSquare(src: Bitmap): Bitmap {
        val width = src.width
        val height = src.height
        val newEdge = minOf(width, height)
        val xOffset = (width - newEdge) / 2
        val yOffset = (height - newEdge) / 2
        val squareBitmap = Bitmap.createBitmap(src, xOffset, yOffset, newEdge, newEdge)
        return if (newEdge > 512) {
            Bitmap.createScaledBitmap(squareBitmap, 512, 512, true)
        } else {
            squareBitmap
        }
    }

    fun saveBitmapToInternalStorage(context: Context, playerId: String, sourceBitmap: Bitmap): String? {
        return try {
            val avatarsDir = File(context.filesDir, "avatars")
            if (!avatarsDir.exists()) {
                avatarsDir.mkdirs()
            }
            val cropped = cropCenterSquare(sourceBitmap)
            val file = File(avatarsDir, "avatar_${playerId.replace("-", "_")}.jpg")
            FileOutputStream(file).use { out ->
                cropped.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun saveUriToInternalStorage(context: Context, playerId: String, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val rawBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            if (rawBitmap != null) {
                saveBitmapToInternalStorage(context, playerId, rawBitmap)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun loadBitmapFromFile(path: String?): Bitmap? {
        if (path.isNullOrBlank()) return null
        return try {
            val file = File(path)
            if (file.exists() && file.isFile) {
                BitmapFactory.decodeFile(path)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun getPresetById(id: String?): PresetAvatar? {
        return presetAvatars.find { it.id == id }
    }
}
