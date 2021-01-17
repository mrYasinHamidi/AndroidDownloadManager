package com.example.androiddownloadmanager.customViews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.androiddownloadmanager.database.DownloadInfo
import com.example.androiddownloadmanager.DownloadState
import com.example.androiddownloadmanager.R
import com.example.androiddownloadmanager.databinding.DownloadViewBinding
import com.example.androiddownloadmanager.getSize
import com.example.androiddownloadmanager.OnStateChange
import com.example.androiddownloadmanager.getStateFromDb
import com.example.androiddownloadmanager.setStateToDb
import java.io.File

class DownloadView : LinearLayout {

    private val onChangeListeners = ArrayList<OnStateChange>()

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
            when (getStateFromDb(it.state)) {
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
            it.state = setStateToDb(DownloadState.RUNNING)
            download()
        }
    }

    private fun download() {
        Toast.makeText(context, "${info?.name} is downloading ...", Toast.LENGTH_SHORT).show()
    }

    fun stop() {
        info?.let {
            updateState(DownloadState.STOP)
        }
    }

    private fun updateState(state: DownloadState) {
        info?.let {
            it.state = setStateToDb(state)
            for (i in onChangeListeners)
                i.onUpdate(state)
        }
    }


    fun setOnStateChangeListener(onChange: OnStateChange){
        onChangeListeners.add(onChange)
    }

}