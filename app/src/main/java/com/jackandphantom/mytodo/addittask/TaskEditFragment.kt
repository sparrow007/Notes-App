package com.jackandphantom.mytodo.addittask

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.jackandphantom.mytodo.EvenObserver
import com.jackandphantom.mytodo.MyToDoApplication
import com.jackandphantom.mytodo.R
import com.jackandphantom.mytodo.databinding.TaskEditBinding
import soup.neumorphism.NeumorphImageButton
import javax.inject.Inject

class TaskEditFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory : ViewModelProvider.Factory

    private val taskViewModel by viewModels<TaskEditViewModel>{viewModelFactory}

    private val args: TaskEditFragmentArgs by navArgs()

    private lateinit var taskViewBinding : TaskEditBinding

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().applicationContext as MyToDoApplication).appComponent.taskEditComponent()
            .create().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.task_edit, container, false)
       taskViewBinding = TaskEditBinding.bind(root).apply {
            viewModel = taskViewModel
        }
        taskViewBinding.lifecycleOwner = this.viewLifecycleOwner
        return taskViewBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        navigateUp()
        setUpDelete()
        taskViewModel.start(args.taskId)
    }

    private fun setUpDelete() {
        if (args.taskId != null) taskViewBinding.buttonDelete.visibility = View.VISIBLE

    }

    private fun navigateUp() {
        taskViewModel.taskUpdateEvent.observe(viewLifecycleOwner, EvenObserver {
            findNavController().navigateUp()

        })

        taskViewBinding.root.findViewById<NeumorphImageButton>(R.id.nib_back).setOnClickListener {
            findNavController().navigateUp()
        }
    }
}