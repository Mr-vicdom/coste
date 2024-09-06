package com.expensetracker.app.transactions.support

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.view.ContextThemeWrapper
import com.expensetracker.app.R

const val SURETY = "Are You Sure?"

class AppDialog(context: Context,text: String,onYesClick: () -> Unit, onNoClick: () -> Unit ): Dialog(context, R.style.NewAppTheme) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

}

fun getChoiceAlertDialog(context: Context, title: String, message: String, onYesClick: () -> Unit = {}, onNoClick: () -> Unit = {}): AlertDialog {


    val contextThemeWrapper: Context = ContextThemeWrapper(context,R.style.AppAlertDialog)

    val builder = AlertDialog.Builder(contextThemeWrapper)

    builder.setTitle(title)
    builder.setMessage(message)

    builder.setPositiveButton("Yes"){ dialog, _ ->
        onYesClick()
        dialog.dismiss()
    }
    builder.setNegativeButton("No"){ dialog,_ ->
        onNoClick()
        dialog.dismiss()
    }

    val alertDialog: AlertDialog = builder.create()

    alertDialog.setCancelable(true)

    return alertDialog
}


fun getWarningAlertDialog(context: Context, title: String, message: String, onOkClick: () -> Unit = {}): AlertDialog {


    val contextThemeWrapper: Context = ContextThemeWrapper(context,R.style.AppAlertDialog)

    val builder = AlertDialog.Builder(contextThemeWrapper)

    builder.setTitle(title)
    builder.setMessage(message)

    builder.setPositiveButton("Ok"){ dialog, _ ->
        onOkClick()
        dialog.dismiss()
    }

    val alertDialog: AlertDialog = builder.create()

    alertDialog.setCancelable(true)

    return alertDialog
}
