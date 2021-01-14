package com.example.androiddownloadmanager.downloader

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.androiddownloadmanager.DownloadInfo
import com.example.androiddownloadmanager.DownloadState
import com.example.androiddownloadmanager.R
import com.example.androiddownloadmanager.databinding.DownloadFragmentBinding

class DownloadFragment : Fragment() {

    private lateinit var binding: DownloadFragmentBinding
    private lateinit var viewModel: DownloadViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.download_fragment, container, false
        )

        //initialize view model
        viewModel = createViewModel()

        //set an on click listener for add button
        binding.addFloatingButton.setOnClickListener {
            findNavController().navigate(R.id.action_downloadFragment_to_requestFragment)
        }

        return binding.root
    }

    private fun createViewModel(): DownloadViewModel {
        val vm = ViewModelProvider(this).get(DownloadViewModel::class.java)
        binding.viewModel = vm
        binding.lifecycleOwner = this
        return vm
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Map<String, String>>(
            "request"
        )
            ?.observe(viewLifecycleOwner,
                Observer {
                    val view = DownloadView(context)
                    val info = DownloadInfo(
                        it.get("name") ?: "",
                        it.get("url") ?: "",
                        it.get("path") ?: "",
                        (it.get("size") ?: "").toLong(),
                        DownloadState.NONE
                    )
                    binding.downloadList.addView(view)
                    view.setDownloadInformation(info)

                })
    }


}