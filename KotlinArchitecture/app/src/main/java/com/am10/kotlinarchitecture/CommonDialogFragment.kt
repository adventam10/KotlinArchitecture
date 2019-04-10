package com.am10.kotlinarchitecture

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import java.util.*

class CommonDialogFragment: DialogFragment() {

    private var listener: DialogListener? = null

    companion object {
        const val DIALOG_TAG = "CommonDialogFragment"
        const val ARGUMENTS_TITLE = "ARGUMENTS_TITLE"
        const val ARGUMENTS_MESSAGE = "ARGUMENTS_MESSAGE"
        const val ARGUMENTS_POSITIVE_TITLE = "ARGUMENTS_POSITIVE_TITLE"
        const val ARGUMENTS_NEGATIVE_TITLE = "ARGUMENTS_NEGATIVE_TITLE"
        const val ARGUMENTS_SHOW_NEGATIVE_FLAG = "ARGUMENTS_SHOW_NEGATIVE_FLAG"

        fun newInstance(title: String, message: String,
                        positiveTitle: String, negativeTitle: String,
                        isShowNegative: Boolean): CommonDialogFragment {
            val fragment = CommonDialogFragment()
            fragment.isCancelable = false
            val bundle = Bundle()
            bundle.putString(ARGUMENTS_TITLE, title)
            bundle.putString(ARGUMENTS_MESSAGE, message)
            bundle.putString(ARGUMENTS_POSITIVE_TITLE, positiveTitle)
            bundle.putString(ARGUMENTS_NEGATIVE_TITLE, negativeTitle)
            bundle.putBoolean(ARGUMENTS_SHOW_NEGATIVE_FLAG, isShowNegative)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var title = ""
        var message = ""
        var positiveTitle = ""
        var negativeTitle = ""
        var isShowNegative = false
        arguments?.let {
            title = it.getString(ARGUMENTS_TITLE)
            message = it.getString(ARGUMENTS_MESSAGE)
            positiveTitle = it.getString(ARGUMENTS_POSITIVE_TITLE)
            negativeTitle = it.getString(ARGUMENTS_NEGATIVE_TITLE)
            isShowNegative = it.getBoolean(ARGUMENTS_SHOW_NEGATIVE_FLAG)
        }

        val builder = AlertDialog.Builder(activity)
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.common_dialog, null)
        builder.setView(dialogView)

        val titleTextView: TextView = dialogView.findViewById(R.id.textView_title)
        titleTextView.text = title

        val messageTextView: TextView = dialogView.findViewById(R.id.textView_message)
        messageTextView.movementMethod = ScrollingMovementMethod.getInstance()
        messageTextView.text = message

        val positiveButton: Button = dialogView.findViewById(R.id.button_positive)
        positiveButton.text = positiveTitle
        positiveButton.setOnClickListener{ v ->
            listener?.onPositiveClick()
            dismiss()
        }

        val negativeButton: Button = dialogView.findViewById(R.id.button_negative)
        if (isShowNegative) {
            negativeButton.visibility = View.VISIBLE
            negativeButton.text = negativeTitle
            negativeButton.setOnClickListener{ v ->
                listener?.onNegativeClick()
                dismiss()
            }
        } else {
            negativeButton.visibility = View.GONE
        }
        
        return builder.create()
    }

    /**
     * リスナーを追加する
     *
     * @param listener
     */
    fun setDialogListener(listener: DialogListener) {
        this.listener = listener
    }

    /**
     * リスナーを削除する
     */
    fun removeDialogListener() {
        this.listener = null
    }

    interface DialogListener : EventListener {

        /**
         * okボタンが押されたイベントを通知する
         */
        fun onPositiveClick()

        /**
         * cancelボタンが押されたイベントを通知する
         */
        fun onNegativeClick()
    }
}