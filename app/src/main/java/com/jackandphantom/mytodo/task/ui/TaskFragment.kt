package com.jackandphantom.mytodo.task.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.jackandphantom.mytodo.EvenObserver
import com.jackandphantom.mytodo.MyToDoApplication
import com.jackandphantom.mytodo.R
import com.jackandphantom.mytodo.databinding.TaskFragBinding
import com.jackandphantom.mytodo.task.TaskViewModel
import com.jackandphantom.mytodo.task.adapter.DataAdapter
import com.jackandphantom.mytodo.utils.setupRefreshLayout
import javax.inject.Inject

class TaskFragment : Fragment() {

    // @
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<TaskViewModel> { viewModelFactory }

    private lateinit var viewDataBinding: TaskFragBinding

    private lateinit var listAdapter: DataAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as MyToDoApplication).appComponent
            .taskComponent().create().inject(this)
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        viewDataBinding = TaskFragBinding.inflate(inflater, container, false).apply {
            viewmodel = viewModel
        }
        return viewDataBinding.root
    }


    private fun setupNavigation() {
        viewModel.openTaskEvent.observe(viewLifecycleOwner, EvenObserver {
            val action = HomeFragmentDirections.actionNavHomeToTaskEditFragment(it)
            findNavController().navigate(action)
        })
        viewModel.newTaskEvent.observe(viewLifecycleOwner, EvenObserver {
           // navigateToAddNewTask()
            val action = HomeFragmentDirections.actionNavHomeToTaskEditFragment(null)
            findNavController().navigate(action)


        })
    }

    private fun setupFab() {
        activity?.findViewById<FloatingActionButton>(R.id.add_task_fab)?.let {
            it.setOnClickListener {
               // navigateToAddNewTask()
                val action = HomeFragmentDirections.actionNavHomeToTaskEditFragment(null)
                findNavController().navigate(action)
            }
        }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setUpListAdapter()
        setupRefreshLayout(viewDataBinding.refreshLayout, viewDataBinding.tasksList)
        setupNavigation()
        setupFab()

        viewModel.load(true)
    }

    private fun setUpListAdapter() {

        val viewModel = viewDataBinding.viewmodel

        if (viewModel != null) {
            listAdapter = DataAdapter(this.viewModel)
            viewDataBinding.tasksList.adapter = listAdapter

        } else {
            Log.e("MY TAG", "View model is not initalize before inititalizing the adapter")
        }

    }

}