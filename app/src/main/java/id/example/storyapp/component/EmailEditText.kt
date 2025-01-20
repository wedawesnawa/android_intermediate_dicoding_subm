package id.example.storyapp.component

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputLayout
import id.example.storyapp.R

class EmailEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    private var parentLayout: TextInputLayout? = null

    init {
        addTextChangedListener { text ->
            updateErrorState(text)
        }
    }

    private fun updateErrorState(input: CharSequence?) {
        parentLayout?.error = if (!isValidInput(input)) {
            context.getString(R.string.error_email)
        } else {
            null
        }
    }

    private fun isValidInput(input: CharSequence?): Boolean {
        return !input.isNullOrEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(input)
            .matches()
    }

    fun setParentLayout(layout: TextInputLayout) {
        parentLayout = layout
    }
}
