package com.example.roadtolegend.ui.components

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.roadtolegend.model.Club
import com.example.ui.theme.GoldPrimary

/**
 * Tìm kiếm Resource ID của Logo CLB trong thư mục drawable.
 * Tương thích linh hoạt với cả tên có prefix (logo_vn_hanoi_fc) lẫn không prefix (logo_hanoi, logo_binh_duong)
 * và các biệt danh viết tắt phổ biến.
 */
fun findClubLogoResId(context: Context, club: Club): Int {
    val pkg = context.packageName
    val res = context.resources

    // 1. Tên trực tiếp từ club.id: logo_vn_binh_duong, logo_vn_pvf...
    val idCandidate = "logo_${club.id.lowercase()}"
    val idRes = res.getIdentifier(idCandidate, "drawable", pkg)
    if (idRes != 0) return idRes

    // 2. Tên bỏ prefix quốc gia: vn_binh_duong -> logo_binh_duong
    val noPrefix = club.id.lowercase()
        .removePrefix("vn_")
        .removePrefix("en_")
        .removePrefix("th_")
        .removePrefix("kr_")
        .removePrefix("pt_")
        .removePrefix("de_")
        .removePrefix("it_")
        .removePrefix("fr_")
    val noPrefixCandidate = "logo_$noPrefix"
    val noPrefixRes = res.getIdentifier(noPrefixCandidate, "drawable", pkg)
    if (noPrefixRes != 0) return noPrefixRes

    // 3. Các bí danh (aliases) thường gặp của CLB Việt Nam & Quốc tế
    val aliasCandidates = when {
        club.id.contains("hanoi", ignoreCase = true) -> listOf("logo_hanoi", "logo_hanoi_fc", "logo_vn_hanoi_fc")
        club.id.contains("pvf", ignoreCase = true) -> listOf("logo_pvf_cand", "logo_pvf", "logo_vn_pvf")
        club.id.contains("tphcm", ignoreCase = true) || club.shortName.contains("TP.HCM", ignoreCase = true) -> listOf("logo_tphcm", "logo_tp_hcm", "logo_hcmc")
        club.id.contains("viettel", ignoreCase = true) -> listOf("logo_viettel", "logo_the_cong_viettel")
        club.id.contains("da_nang", ignoreCase = true) -> listOf("logo_da_nang", "logo_shb_da_nang")
        club.id.contains("nam_dinh", ignoreCase = true) -> listOf("logo_nam_dinh", "logo_thep_xanh_nam_dinh")
        club.id.contains("binh_duong", ignoreCase = true) -> listOf("logo_binh_duong", "logo_becamex_binh_duong")
        club.id.contains("thanh_hoa", ignoreCase = true) -> listOf("logo_thanh_hoa", "logo_dong_a_thanh_hoa")
        club.id.contains("binh_phuoc", ignoreCase = true) -> listOf("logo_binh_phuoc", "logo_truong_tuoi_binh_phuoc")
        club.id.contains("ninh_binh", ignoreCase = true) -> listOf("logo_ninh_binh", "logo_phu_dong_ninh_binh")
        club.id.contains("hai_phong", ignoreCase = true) -> listOf("logo_hai_phong", "logo_vn_hai_phong")
        club.id.contains("hagl", ignoreCase = true) -> listOf("logo_hagl", "logo_vn_hagl")
        club.id.contains("slna", ignoreCase = true) -> listOf("logo_slna", "logo_vn_slna")
        club.id.contains("brvt", ignoreCase = true) -> listOf("logo_brvt", "logo_vn_brvt")
        club.id.contains("hue", ignoreCase = true) -> listOf("logo_hue", "logo_vn_hue")
        club.id.contains("cahn", ignoreCase = true) -> listOf("logo_cahn", "logo_vn_cahn")
        else -> emptyList()
    }
    for (alias in aliasCandidates) {
        val aRes = res.getIdentifier(alias, "drawable", pkg)
        if (aRes != 0) return aRes
    }

    // 4. Thử theo tên rút gọn ngắn (shortName) viết liền gạch dưới
    val shortNameSanitized = "logo_" + club.shortName.lowercase()
        .replace(" ", "_")
        .replace("-", "_")
        .replace(".", "")
    val sRes = res.getIdentifier(shortNameSanitized, "drawable", pkg)
    if (sRes != 0) return sRes

    return 0
}

@Composable
fun ClubBadge(
    club: Club,
    size: Dp = 44.dp,
    modifier: Modifier = Modifier,
    isFictionalMode: Boolean = false
) {
    val context = LocalContext.current
    val resId = if (!isFictionalMode) {
        findClubLogoResId(context, club)
    } else 0

    if (resId != 0) {
        Box(
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(size * 0.22f))
                .background(Color.White.copy(alpha = 0.08f))
                .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(size * 0.22f)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = resId),
                contentDescription = club.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(size * 0.88f)
                    .padding(1.dp)
            )
        }
    } else {
        // High-fidelity fallback soccer crest badge with club colors and initials
        val primaryColor = try {
            Color(android.graphics.Color.parseColor(club.primaryColorHex))
        } catch (e: Exception) {
            GoldPrimary
        }

        val secondaryColor = try {
            Color(android.graphics.Color.parseColor(club.secondaryColorHex))
        } catch (e: Exception) {
            Color.White
        }

        val initials = if (club.shortName.length <= 3) {
            club.shortName
        } else {
            val words = club.shortName.split(" ").filter { it.isNotBlank() }
            if (words.size >= 2) {
                "${words[0].first()}${words[1].first()}".uppercase()
            } else {
                club.shortName.take(3).uppercase()
            }
        }

        Box(
            modifier = modifier
                .size(size)
                .clip(RoundedCornerShape(size * 0.22f)),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(size)) {
                val w = this.size.width
                val h = this.size.height

                // Shield Path
                val shieldPath = Path().apply {
                    moveTo(w * 0.15f, 0f)
                    lineTo(w * 0.85f, 0f)
                    quadraticTo(w, h * 0.45f, w * 0.5f, h)
                    quadraticTo(0f, h * 0.45f, w * 0.15f, 0f)
                    close()
                }

                // Background gradient
                drawPath(
                    path = shieldPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(primaryColor, primaryColor.copy(alpha = 0.85f))
                    ),
                    style = Fill
                )

                // Diagonal sash line with secondary color
                drawLine(
                    color = secondaryColor.copy(alpha = 0.55f),
                    start = Offset(0f, h * 0.7f),
                    end = Offset(w, h * 0.2f),
                    strokeWidth = w * 0.18f
                )

                // Golden / Silver border
                drawPath(
                    path = shieldPath,
                    color = Color.White.copy(alpha = 0.8f),
                    style = Stroke(width = w * 0.06f)
                )
            }

            // Initials
            val fontSize = (size.value * 0.32f).sp
            Text(
                text = initials,
                fontSize = fontSize,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = (-0.5).sp
            )
        }
    }
}
