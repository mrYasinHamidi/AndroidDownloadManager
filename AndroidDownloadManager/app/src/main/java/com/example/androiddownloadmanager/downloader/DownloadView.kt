package com.example.androiddownloadmanager.downloader

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.example.androiddownloadmanager.DownloadInfo
import com.example.androiddownloadmanager.DownloadState
import com.example.androiddownloadmanager.R
import com.example.androiddownloadmanager.databinding.DownloadViewBinding
import com.example.androiddownloadmanager.utility.format
import com.example.androiddownloadmanager.utility.getSize
import java.io.File

class DownloadView : LinearLayout {


    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context,
        attrs
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr)


    //initializing view and connect to xml via Data Binding
    private val binding: DownloadViewBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.download_view,
        this,
        true
    )
    private var info: DownloadInfo? = null

    fun setDownloadInformation(info: DownloadInfo) {
        if (this.info == null) {
            this.info = info
            setup()
        }
    }

    private fun setup() {
        info?.let {
            binding.dlTxtName.text = it.name
            binding.dlTxtSize.text = getSize(it.size)
            binding.dlTxtSpeed.text = ""
            binding.dlTxtTime.text = ""
            when (it.state) {
                DownloadState.STOP -> binding.circleProgress.progress =
                    (File(it.path).length() / it.size * 100).toInt()
                DownloadState.ERROR -> binding.circleProgress.progress =
                    (File(it.path).length() / it.size * 100).toInt()
                DownloadState.SUCCESSFUL ->
                    binding.circleProgress.progress = 100
                DownloadState.NONE ->
                    binding.circleProgress.progress = 0

            }
        }
    }


    fun start() {
        info?.let {
            it.state = DownloadState.RUNNING
            download()
        }
    }

    private fun download() {

    }

    fun stop() {
        info?.let {
            it.state = DownloadState.STOP
        }
    }


}