package com.trendster.harpic.util

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import com.airbnb.lottie.LottieAnimationView
import com.trendster.harpic.R

class LoadingDialog(
    private val activity: Activity,

) {
    private lateinit var dialog: AlertDialog

    fun startLoading() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
        val inflater: LayoutInflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.custom_dialog, null))
        builder.setCancelable(true)

        dialog = builder.create()
        dialog.show()
    }

    fun dismissDialog() {
        dialog.dismiss()
    }
}
