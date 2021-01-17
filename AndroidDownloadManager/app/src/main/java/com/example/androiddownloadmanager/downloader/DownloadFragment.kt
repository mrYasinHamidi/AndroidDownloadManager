package com.example.androiddownloadmanager.downloader

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.androiddownloadmanager.database.DownloadInfo
import com.example.androiddownloadmanager.DownloadState
import com.example.androiddownloadmanager.R
import com.example.androiddownloadmanager.database.DownloadDatabase
import com.example.androiddownloadmanager.database.getDatabase
import com.example.androiddownloadmanager.databinding.DownloadFragmentBinding
import com.example.androiddownloadmanager.utility.getStateFromDb
import com.example.androiddownloadmanager.utility.setStateToDb

class DownloadFragment : Fragment() {

    private lateinit var binding: DownloadFragmentBinding
    private lateinit var viewModel: DownloadViewModel
    private lateinit var adapter: DownloadAdapter
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

        //setup recycler view
        adapter = DownloadAdapter()
        binding.recyclerView.adapter = adapter

        //set an on click listener for add button
        binding.addFloatingButton.setOnClickListener {
            val action =
                DownloadFragmentDirections.actionDownloadFragmentToRequestFragment(viewModel.getNames())
            findNavController().navigate(action)
        }

        //get the list of download an show it in recycler view
        viewModel.downloadList.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        return binding.root
    }

    private fun createViewModel(): DownloadViewModel {
        val application = requireActivity().application
        val factory = DownloadViewModelFactory(getDatabase(application).dao,application)
        val vm = ViewModelProvider(this,factory).get(DownloadViewModel::class.java)
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
                        name = it["name"] ?: "",
                        url = it["url"] ?: "",
                        path = it["path"] ?: "",
                        size = (it["size"] ?: "").toLong(),
                        state = setStateToDb(DownloadState.NONE)
                    )
                    viewModel.addDownloadInfo(info)
                })
    }


}