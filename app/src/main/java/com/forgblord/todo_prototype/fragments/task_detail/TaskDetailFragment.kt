package com.forgblord.todo_prototype.fragments.task_detail

import android.icu.text.DateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.navArgs
import com.forgblord.todo_prototype.data.models.Task
import com.forgblord.todo_prototype.data.viewmodels.TaskListViewModel
import com.forgblord.todo_prototype.databinding.FragmentTaskDetailsBinding
import com.forgblord.todo_prototype.fragments.datepicker.DatePickerFragment
import java.util.Date
import java.util.UUID

class TaskDetailFragment: Fragment() {
    private val args: TaskDetailFragmentArgs by navArgs()
    private var _binding: FragmentTaskDetailsBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val taskListViewModel: TaskListViewModel by activityViewModels()

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun onChecked(id: UUID) {
        taskListViewModel.removeTaskById(id)
    }
}