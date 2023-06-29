package pn.android.compose.components.masks

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.absoluteValue

/**
 * Class that transforms the visual representation of the input value in different views
 * */
class MaskVisualTransformation(private val mask: String) : VisualTransformation {

    private val specialSymbolsIndices = mask.indices.filter { mask[it] != '#' }

    /**
     * a function that determines where new values entered into the view will be placed
     * */
    override fun filter(text: AnnotatedString): TransformedText {
        var out = ""
        var maskIndex = 0
        text.forEach { char ->
            while (specialSymbolsIndices.contains(maskIndex)) {
                out += mask[maskIndex]
                maskIndex++
            }
            out += char
            maskIndex++
        }
        return TransformedText(AnnotatedString(out), offsetTranslator())
    }

    /**
     * Provides changed bidirectional offset mapping between original and transformed text
     * */
    private fun offsetTranslator() = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            val offsetValue = offset.absoluteValue
            if (offsetValue == 0) return 0
            var numberOfHashtags = 0
            val masked = mask.takeWhile {
                if (it == '#') numberOfHashtags++
                numberOfHashtags < offsetValue
            }
            return masked.length + 1
        }

        override fun transformedToOriginal(offset: Int): Int {
            return mask.take(offset.absoluteValue).count { it == '#' }
        }
    }
}

@Preview
@Composable
private fun MaskUsagePreview() {

    //val mask = remember { "#####-###" }
    val mask = remember { "+7 (###) ###-##-##" }
    val maskLength = remember(mask) { mask.count { it == '#' } }

    var text by remember { mutableStateOf("") }

    OutlinedTextField(
        value = text,
        onValueChange = { it ->
            if (it.length <= maskLength) {
                text = it.filter { it.isDigit() }
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        visualTransformation = MaskVisualTransformation(mask)
    )

}