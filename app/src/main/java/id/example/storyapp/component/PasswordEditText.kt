package id.example.storyapp.component

import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout
import id.example.storyapp.R

class PasswordEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    private var parentLayout: TextInputLayout? = null

    init {
        configureInputType()
        setupTextChangeListener()
    }

    private fun configureInputType() {
        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
    }

    private fun setupTextChangeListener() {
        addTextChangedListener { text ->
            updateErrorState(text)
        }
    }

    private fun updateErrorState(input: CharSequence?) {
        parentLayout?.error = if (input.isNullOrEmpty() || input.length < 8) {
            context.getString(R.string.error_password)
        } else {
            null
        }
    }

    fun setParentLayout(layout: TextInputLayout) {
        parentLayout = layout
    }
}
