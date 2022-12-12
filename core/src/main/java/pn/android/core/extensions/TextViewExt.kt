package pn.android.core.extensions

import android.text.Html
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View
import android.widget.TextView

fun makeLinkClickable(
    strBuilder: SpannableStringBuilder,
    span: URLSpan,
    color: Int,
    onClickUrl: (String) -> Unit) {

    val start = strBuilder.getSpanStart(span)
    val end = strBuilder.getSpanEnd(span)
    val flags = strBuilder.getSpanFlags(span)
    val clickable = object : ClickableSpan() {
        override fun updateDrawState(ds: TextPaint) {
            ds.color = color
            ds.isUnderlineText = false
        }

        override fun onClick(widget: View) {
            onClickUrl.invoke(span.url)
        }
    }
    strBuilder.setSpan(clickable, start, end, flags)
    strBuilder.removeSpan(span)
}

fun TextView.setTextViewHTML(html: String, color: Int, onClickUrl: (String) -> Unit) {
    val sequence = Html.fromHtml(html)
    val strBuilder = SpannableStringBuilder(sequence)
    val urls = strBuilder.getSpans(0, sequence.length, URLSpan::class.java)
    for (span in urls) {
        makeLinkClickable(strBuilder, span, color, onClickUrl)
    }
    this.text = strBuilder
    this.movementMethod = LinkMovementMethod.getInstance()
}