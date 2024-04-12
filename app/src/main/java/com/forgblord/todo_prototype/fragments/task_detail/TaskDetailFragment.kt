package com.forgblord.todo_prototype.fragments.task_detail

import android.icu.text.DateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.forgblord.todo_prototype.data.models.Task
import com.forgblord.todo_prototype.data.viewmodels.TaskDetailViewModel
import com.forgblord.todo_prototype.data.viewmodels.TaskDetailViewModelFactory
import com.forgblord.todo_prototype.databinding.FragmentTaskDetailsBinding
import kotlinx.coroutines.launch

class TaskDetailFragment: Fragment() {
    private val args: TaskDetailFragmentArgs by navArgs()

    private var _binding: FragmentTaskDetailsBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val taskDetailViewModel: TaskDetailViewModel by viewModels {
        TaskDetailViewModelFactory(args.taskId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTaskDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*binding.apply {
            taskDetailProject.text = "Default Project"
            taskDetailTitle.text = task
            taskDetailDate.text = if (task.date == null) "Not set"
            else DateFormat.getPatternInstance(DateFormat.YEAR_ABBR_MONTH_WEEKDAY_DAY)
                .format(task.date)

            taskDetailCheckbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    onChecked(task.id)
                }
            }
        }*/

        binding.apply {
            taskDetailTitle.doOnTextChanged { text, _, _, _ ->
                taskDetailViewModel.updateTask { oldTask ->
                    oldTask.copy(title=text.toString())
                }
            }

            taskDetailCheckbox.setOnCheckedChangeListener { _, isChecked ->
                taskDetailViewModel.updateTask { oldTask ->
                    oldTask.copy(completed=isChecked)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                taskDetailViewModel.task.collect() { task ->
                    task?.let { updateUI(it) }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateUI(task: Task) {
        binding.apply {
            taskDetailProject.text = "Default Project"

            if (taskDetailTitle.text.toString() != task.title) {
                taskDetailTitle.setText(task.title)
            }

            taskDetailDate.text = if (task.date == null) "Not set"
            else DateFormat.getPatternInstance(DateFormat.YEAR_ABBR_MONTH_WEEKDAY_DAY)
                .format(task.date)

            taskDetailCheckbox.isChecked = task.completed
        }
    }

    /*override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val task: Task = taskListViewModel.getTaskById(args.taskId)
        binding.apply {
            taskDetailProject.text = "Default Project"
            taskDetailTitle.text = task.title
            taskDetailDate.text = if (task.date == null) "Not set"
            else DateFormat.getPatternInstance(DateFormat.YEAR_ABBR_MONTH_WEEKDAY_DAY)
                .format(task.date)

            taskDetailCheckbox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    onChecked(task.id)
                }
            }
        }
    }*/

    /*private fun onChecked(id: UUID) {
        taskListViewModel.removeTaskById(id)
    }*/
}