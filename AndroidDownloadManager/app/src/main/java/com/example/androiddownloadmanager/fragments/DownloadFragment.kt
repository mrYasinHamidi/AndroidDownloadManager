package com.example.androiddownloadmanager.fragments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.androiddownloadmanager.*
import com.example.androiddownloadmanager.adapters.DownloadAdapter
import com.example.androiddownloadmanager.adapters.InfoUpdate
import com.example.androiddownloadmanager.database.DownloadInfo
import com.example.androiddownloadmanager.database.getDatabase
import com.example.androiddownloadmanager.databinding.DownloadFragmentBinding
import com.example.androiddownloadmanager.customViews.DownloadView
import com.example.androiddownloadmanager.factories.DownloadViewModelFactory
import com.example.androiddownloadmanager.viewmodels.DownloadViewModel
import com.tonyodev.fetch2.Fetch
import com.tonyodev.fetch2.FetchConfiguration

class DownloadFragment : Fragment() {

    private lateinit var binding: DownloadFragmentBinding
    private lateinit var viewModel: DownloadViewModel
    private lateinit var adapter : DownloadAdapter


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

        //create a Downloader
        val fetchConfig = FetchConfiguration.Builder(requireContext())
            .setDownloadConcurrentLimit(3)
            .build()
        val fetch = Fetch.Impl.getInstance(fetchConfig)
        val downloader = Downloader(fetch)

        //initialize RecyclerView
        adapter = DownloadAdapter(downloader, InfoUpdate {
            viewModel.update(it)
        })
        binding.recyclerview.adapter = adapter

        //do observe , observable object of view model
        viewModel.infos.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
            Log.i("aaa","items Submited")
        })


        //set an on click listener for add button
        binding.addFloatingButton.setOnClickListener {
            val action =
                DownloadFragmentDirections.actionDownloadFragmentToRequestFragment(viewModel.getNames())
            findNavController().navigate(action)
        }


        return binding.root
    }


    private fun createViewModel(): DownloadViewModel {
        val application = requireActivity().application
        val factory = DownloadViewModelFactory(getDatabase(application).dao, context, application)
        val vm = ViewModelProvider(this, factory).get(DownloadViewModel::class.java)
        binding.lifecycleOwner = this
        return vm
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        /*every time that user insert a download url in RequestFragment.kt
        * we will be noticed in this section
        * then we create a DownloadInfo with that information and pass it to view model */

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
                        state = DownloadState.INIT
                    )
                    viewModel.insert(info) //pass DownloadInformation to view model
                })
    }

}