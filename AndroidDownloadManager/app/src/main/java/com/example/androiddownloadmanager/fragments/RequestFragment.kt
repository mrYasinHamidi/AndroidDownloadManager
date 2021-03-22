package com.example.androiddownloadmanager.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.androiddownloadmanager.R
import com.example.androiddownloadmanager.databinding.RequestFragmentBinding
import com.example.androiddownloadmanager.viewmodels.RequestViewModel
import com.example.androiddownloadmanager.RequestState
import com.example.androiddownloadmanager.TextPaste
import com.example.androiddownloadmanager.getClipBoardText

class RequestFragment : DialogFragment() {

    private lateinit var binding: RequestFragmentBinding
    private lateinit var viewModel: RequestViewModel
    val args : RequestFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.request_fragment, container, false
        )

        //initialize view model
        viewModel = createViewModel()


        //set a listener for every time that user paste a url to input_url
        binding.inputUrl.addOnPasteTextListener(object : TextPaste {
            override fun onUpdate(text: String) {
                checkUrl(text)
            }
        })
        for (i in args.names)
            Toast.makeText(context, i, Toast.LENGTH_SHORT).show()
        //set a onClick for paste url to input url
        binding.txtInputLayout.setStartIconOnClickListener {
            val pastedText = getClipBoardText(requireContext())
            binding.inputUrl.setText(pastedText)
            checkUrl(pastedText)
        }

        binding.txtInputLayout.setErrorIconOnClickListener {
            viewModel.clareStatus()
        }
        //when user on clare button on right site of edite text
        binding.txtInputLayout.setEndIconOnClickListener {
            viewModel.clareStatus()
            binding.inputUrl.clearFocus()
            binding.inputUrl.text?.clear()
        }

        //when user change add or remove a latter from link
//        binding.inputUrl.addOnChangeTextListener(object:ChangeText{
//            override fun onUpdate() {
//                Toast.makeText(context, "textChange", Toast.LENGTH_SHORT).show()
//                viewModel.clareStatus()
//            }
//        })


        //set onClick for button start and cancel
        binding.btnStart.setOnClickListener {
            /* first of all we check that status of request
            * and according to it decide what happened*/
            when (viewModel.status.value) {
                RequestState.SUCCESSFUL -> {
                    submit()
                }
                RequestState.LOADING -> {
                    Toast.makeText(
                        context,
                        "please be patient , we are still getting information",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                RequestState.NONE -> {
                    checkUrl(binding.inputUrl.text.toString())
                }
                RequestState.ERROR -> {
                    viewModel.clareStatus()
                    checkUrl(binding.inputUrl.text.toString())
                }

            }
        }
        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp() //finish fragment and pop up it from backstack
        }
        return binding.root
    }

    private fun checkUrl(text: String) {
        //this method check the text of inputUrl , if it is not empty then call viewModel GetStatus function
        if (text.isEmpty())
            Toast.makeText(context, "please enter your download link", Toast.LENGTH_SHORT).show()
        else
            viewModel.getStatus(text)
        Log.i("aaa", "checkUrl")
    }


    private fun createViewModel(): RequestViewModel {
        val vm = ViewModelProvider(this).get(RequestViewModel::class.java)
        binding.viewModel = vm
        binding.lifecycleOwner = this
        return vm
    }

    private fun submit() {
        val name =
            if (binding.inputName.text.isEmpty()) "${viewModel.name.value}" else "${binding.inputName.text.toString()
                .trim()}.${binding.inputUrl.text.toString().split(".").last()}"
        if (args.names.contains(name)) {
            Toast.makeText(context, "this name is already used", Toast.LENGTH_SHORT).show()
        } else {
            findNavController().previousBackStackEntry?.savedStateHandle?.set(
                "request",
                mapOf<String, String>(
                    Pair(
                        "name", name
                    ),
                    Pair("url", binding.inputUrl.text.toString()),
                    Pair("size", "${viewModel.size.value}"),
                    Pair("path", "${viewModel.path.value}")
                )
            )
            findNavController().navigateUp()
        }
    }

}