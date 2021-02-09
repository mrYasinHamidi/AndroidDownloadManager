package com.example.androiddownloadmanager.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.androiddownloadmanager.DownloadState
import com.example.androiddownloadmanager.database.DownloadInfo
import com.example.androiddownloadmanager.databinding.DownloadListItemBinding
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import io.reactivex.rxjava3.disposables.Disposable

class DownloadAdapter(
    private val downloadObservable: Observable<String>,
    private val listCallBack: ListCallBack
) :
    ListAdapter<DownloadInfo, DownloadViewHolder>(DownloadUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DownloadViewHolder {
        return DownloadViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: DownloadViewHolder, position: Int) {
        holder.bind(getItem(position), downloadObservable, listCallBack)
    }
}

class DownloadViewHolder(private val binding: DownloadListItemBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(info: DownloadInfo, downloadObservable: Observable<String>, listener: ListCallBack) {
        binding.executePendingBindings()

        binding.circleProgress.setOnClickListener {
            listener.clickInfo(info)
        }

        downloadObservable.subscribe(object : Observer<String> {
            override fun onSubscribe(d: Disposable?) {
            }

            override fun onNext(t: String?) {
                Log.i("Observable","On_Next : $t")
                val objects: List<String> = t?.split(',') ?: listOf("")
                if (objects[1].toInt() == info.dId)
                    when (objects[0]) {
                        "onStarted" -> {
                            info.state = DownloadState.RUNNING
                            listener.updateInfo(info)
                        }
                        "onResumed" -> {
                            info.state = DownloadState.RUNNING
                            listener.updateInfo(info)
                        }
                        "onProgress" -> {
                            binding.circleProgress.progress = objects[2].toInt()
                        }
                        "onPaused" -> {
                            info.state = DownloadState.STOP
                            listener.updateInfo(info)
                        }
                        "onError" -> {
                            info.state = DownloadState.ERROR
                            listener.updateInfo(info)
                        }
                        "onCompleted" -> {
                            info.state = DownloadState.SUCCESSFUL
                            listener.updateInfo(info)
                        }
                    }
            }

            override fun onError(e: Throwable?) {
            }

            override fun onComplete() {
                Log.i("Observable","OnComplete : ${info.dId}")
            }
        })

    }


    companion object {
        fun from(parent: ViewGroup): DownloadViewHolder =
            DownloadViewHolder(
                DownloadListItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )


    }

}

class DownloadUtil : DiffUtil.ItemCallback<DownloadInfo>() {
    override fun areItemsTheSame(oldItem: DownloadInfo, newItem: DownloadInfo): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: DownloadInfo, newItem: DownloadInfo): Boolean {
        return oldItem == newItem
    }
}

class ListCallBack(private val onUpdate: (DownloadInfo) -> Unit, private val onClick: (DownloadInfo) -> Unit) {
    fun updateInfo(info: DownloadInfo) = onUpdate(info)
    fun clickInfo(info: DownloadInfo) = onClick(info)
}

//https://project.yasinhamidi.ir/MafiaApp/mafia.apk