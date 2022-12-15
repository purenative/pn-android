package pn.android.theme

import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import pn.android.R

val SFProDisplayFamily = FontFamily(
    Font(R.font.sf_pro_display_regular, FontWeight.Normal),
    Font(R.font.sf_pro_display_medium, FontWeight.Medium),
    Font(R.font.sf_pro_display_semibold, FontWeight.SemiBold),
    Font(R.font.sf_pro_display_bold, FontWeight.Bold)
)

object PnRegularTextStyles {

    private val RegularTextStyle = TextStyle(
        fontFamily = SFProDisplayFamily,
        fontWeight = FontWeight.Normal,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    )

    val Caption2 = RegularTextStyle.copy(fontSize = 11.sp)
    val Caption2Grey500 = Caption2.copy(color = PnColors.Grey500)
    val Caption2White = Caption2.copy(color = PnColors.White)

    val Caption1 = RegularTextStyle.copy(fontSize = 12.sp)
    val Caption1Primary700 = RegularTextStyle.copy(color = PnColors.Primary700)
    val Caption1Grey700 = RegularTextStyle.copy(color = PnColors.Grey700)

    val Footnote = RegularTextStyle.copy(fontSize = 13.sp)
    val FootnoteGrey900 = Footnote.copy(color = PnColors.Grey900)
    val FootnoteGrey800 = Footnote.copy(color = PnColors.Grey800)
    val FootnoteGrey500 = Footnote.copy(color = PnColors.Grey500)

    val Subheadline = RegularTextStyle.copy(fontSize = 15.sp)
    val SubheadlineGrey400 = Subheadline.copy(color = PnColors.Grey400)
    val SubheadlinePrimary500 = Subheadline.copy(color = PnColors.Primary500)

    val Callout = RegularTextStyle.copy(fontSize = 16.sp)
    val CalloutGrey400 = RegularTextStyle.copy(color = PnColors.Grey400)
    val CalloutGrey900 = RegularTextStyle.copy(color = PnColors.Grey900)

    val Body = RegularTextStyle.copy(fontSize = 17.sp)
    val BodyWhite = Body.copy(color = PnColors.White)
    val BodyBlack = Body.copy(color = PnColors.Black)
    val BodyGrey900 = Body.copy(color = PnColors.Grey900)
    val BodyGrey600 = Body.copy(color = PnColors.Grey600)
    val BodyGrey400 = Body.copy(color = PnColors.Grey400)
    val BodyGrey200 = Body.copy(color = PnColors.Grey200)
    val BodyPrimary500 = Body.copy(color = PnColors.Primary500)

    val Title3 = RegularTextStyle.copy(fontSize = 20.sp)
    val Title2 = RegularTextStyle.copy(fontSize = 22.sp)
    val Title1 = RegularTextStyle.copy(fontSize = 28.sp)
    val LargeTitle = RegularTextStyle.copy(fontSize = 34.sp)
}

object PnMediumTextStyles {

    private val MediumTextStyle = TextStyle(
        fontFamily = SFProDisplayFamily,
        fontWeight = FontWeight.Medium,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    )

    val Caption1 = MediumTextStyle.copy(fontSize = 12.sp)
    val Caption1Primary700 = Caption1.copy(color = PnColors.Primary700)
    val Caption1Grey700 = Caption1.copy(color = PnColors.Grey700)

    val Body = MediumTextStyle.copy(fontSize = 17.sp)
    val BodyBlack = Body.copy(color = PnColors.Black)
}

object PnSemiBoldTextStyles {

    private val SemiBoldTextStyle = TextStyle(
        fontFamily = SFProDisplayFamily,
        fontWeight = FontWeight.SemiBold
    )

    val Caption2 = SemiBoldTextStyle.copy(fontSize = 11.sp)
    val Footnote = SemiBoldTextStyle.copy(fontSize = 13.sp)
    val Subheadline = SemiBoldTextStyle.copy(fontSize = 15.sp)
    val SubheadlineGrey900 = Subheadline.copy(color = PnColors.Grey900)
    val Callout = SemiBoldTextStyle.copy(fontSize = 16.sp)
    val Body = SemiBoldTextStyle.copy(fontSize = 17.sp)
    val BodyGrey900 = Body.copy(color = PnColors.Grey900)
    val Headline = SemiBoldTextStyle.copy(fontSize = 17.sp)
    val HeadlineGrey900 = SemiBoldTextStyle.copy(color = PnColors.Grey900)
    val Title3 = SemiBoldTextStyle.copy(fontSize = 20.sp)
    val Title3Grey900 = Title3.copy(color = PnColors.Grey900)
    val HeadlinePrimary500 = Headline.copy(color = PnColors.Primary500)

}

object PnBoldTextStyles {

    private val BoldTextStyle = TextStyle(
        fontFamily = SFProDisplayFamily,
        fontWeight = FontWeight.Bold,
        platformStyle = PlatformTextStyle(
            includeFontPadding = false
        )
    )

    val Title2 = BoldTextStyle.copy(fontSize = 22.sp)

    val Title1 = BoldTextStyle.copy(fontSize = 28.sp)
    val Title1Black = Title1.copy(color = PnColors.Black)

    val LargeTitle = BoldTextStyle.copy(fontSize = 34.sp)
    val LargeTitleGrey900 = LargeTitle.copy(color = PnColors.Grey900)

}