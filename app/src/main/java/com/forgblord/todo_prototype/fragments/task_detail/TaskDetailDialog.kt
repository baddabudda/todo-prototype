package com.forgblord.todo_prototype.fragments.task_detail

import android.content.DialogInterface
import android.content.res.ColorStateList
import android.icu.text.DateFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.annotation.MenuRes
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColor
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.forgblord.todo_prototype.R
import com.forgblord.todo_prototype.data.models.TaskProject
import com.forgblord.todo_prototype.data.viewmodels.TaskDetailCRUD
import com.forgblord.todo_prototype.data.viewmodels.TaskDetailViewModelFactory
import com.forgblord.todo_prototype.databinding.FragmentTaskDetailsBinding
import com.forgblord.todo_prototype.fragments.datepicker.DatePickerFragment
import com.forgblord.todo_prototype.fragments.projectpicker.ProjectPickerDialog
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.launch
import java.util.Date

class TaskDetailDialog: BottomSheetDialogFragment() {
    private val args: TaskDetailDialogArgs by navArgs()

    private var _binding: FragmentTaskDetailsBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val taskDetailViewModel: TaskDetailCRUD by viewModels {
        TaskDetailViewModelFactory(args.taskId)
    }

    private var _task: TaskProject? = null

    override fun onStart() {
        super.onStart()

        val density = requireContext().resources.displayMetrics.density

        dialog?.let {
            val bottomSheet = it.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout

            val layoutParams = bottomSheet.layoutParams
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
            bottomSheet.layoutParams = layoutParams

            val behavior = BottomSheetBehavior.from(bottomSheet)
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED

            behavior.apply {
                peekHeight = (400 * density).toInt()

                addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                    override fun onStateChanged(bottomSheet: View, newState: Int) {

                    }

                    override fun onSlide(bottomSheet: View, slideOffset: Float) {

                    }
                })
            }
        }
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

        /*val popupMenu = PopupMenu(requireContext(), binding.btnSettingsTask)
        popupMenu.inflate(R.menu.menu_task)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_edit_task -> {
                    binding.etTask.boxStrokeWidth = 1
                    true
                }
                R.id.menu_delete_task -> {
                    Toast
                        .makeText(context, "DELETE", Toast.LENGTH_SHORT)
                        .show()
                    true
                }
                else -> false
            }
        }*/

        binding.apply {
            taskName.doOnTextChanged { text, _, _, _ ->
                taskDetailViewModel.updateTask { oldTask ->
//                    oldTask.copy(title=text.toString())
                    oldTask.copy(
                        task=oldTask.task.copy(title=text.toString())
                    )
                }
            }

            cbCompleted.setOnClickListener {
                taskDetailViewModel.updateTask { oldTask ->
//                    oldTask.copy(completed=!oldTask.completed)
                    oldTask.copy(
                        task=oldTask.task.copy(completed=!oldTask.task.completed)
                    )
                }

                dismiss()
            }

            chipDate.setOnClickListener {
                findNavController().navigate(TaskDetailDialogDirections.selectDate())
            }

            chipPriority.setOnClickListener {
                showMenu(it, R.menu.menu_priority)
            }

            btnDeleteTask.setOnClickListener {
                _task?.let { it1 -> taskDetailViewModel.deleteTask(it1.task) }
                findNavController().popBackStack()
            }

            /*btnSettingsTask.setOnClickListener {
                popupMenu.show()
            }*/

            projectName.setOnClickListener {
                findNavController().navigate(TaskDetailDialogDirections.actionTaskDetailToProjectPicker())
            }

            setFragmentResultListener(DatePickerFragment.REQUEST_KEY_DATE) { _, bundle ->
                val date = bundle.getSerializable(DatePickerFragment.BUNDLE_KEY_DATE) as Date
                taskDetailViewModel.updateTask { oldTask ->
//                    task.copy(date=date)
                    oldTask.copy(
                        task=oldTask.task.copy(date=date)
                    )
                }
            }

            setFragmentResultListener(ProjectPickerDialog.REQUEST_KEY_PROJECT) { _, bundle ->
                val projId = bundle.getInt(ProjectPickerDialog.BUNDLE_KEY_ID)
                val projectTitle = bundle.getString(ProjectPickerDialog.BUNDLE_KEY_TITLE)
                Log.d("DIALOG", projectTitle.toString())

                projectName.text = projectTitle.toString()
                taskDetailViewModel.updateTask { oldTask ->
//                    task.copy(proj_id=projId)
                    oldTask.copy(
                        task=oldTask.task.copy(proj_id=projId),
                        projectName=projectTitle
                    )
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                taskDetailViewModel.task.collect { flowTask ->
                    flowTask?.let { updateUI(it) }
                    _task = flowTask
                }
            }
        }
    }

    private fun updatePriority(priorityString: String): Int = when (priorityString) {
        getString(R.string.priority_1) -> 1
        getString(R.string.priority_2) -> 2
        getString(R.string.priority_3) -> 3
        else -> 4
    }

    private fun showMenu(view: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(requireContext(), view)
        popup.menuInflater.inflate(menuRes, popup.menu)
        popup.setForceShowIcon(true)

        popup.setOnMenuItemClickListener {
            binding.chipPriority.text = it.title.toString().substringBefore(" ")
            binding.chipPriority.chipIcon = it.icon
            binding.chipPriority.chipIconTint = it.iconTintList

            taskDetailViewModel.updateTask { oldTask ->
//                    oldTask.copy(title=text.toString())
                oldTask.copy(
                    task=oldTask.task.copy(priority = updatePriority(it.title.toString()))
                )
            }

            popup.dismiss()
            true
        }

        popup.show()
    }

    private fun updateUI(taskProject: TaskProject) {
        binding.apply {
            projectName.text = taskProject.projectName ?: "Inbox"

            if (taskName.text.toString() != taskProject.task.title) {
                taskName.setText(taskProject.task.title)
            }

            chipDate.text = if (taskProject.task.date == null) "Not set"
            else DateFormat.getPatternInstance(DateFormat.YEAR_ABBR_MONTH_WEEKDAY_DAY)
                .format(taskProject.task.date)

            cbCompleted.buttonTintList = when (taskProject.task.priority) {
                1 -> ColorStateList.valueOf(ContextCompat.getColor(root.context, R.color.red_500))
                2 -> ColorStateList.valueOf(ContextCompat.getColor(root.context, R.color.yellow_500))
                3 -> ColorStateList.valueOf(ContextCompat.getColor(root.context, R.color.blue_500))
                else -> ColorStateList.valueOf(ContextCompat.getColor(root.context, R.color.gray))
            }
            cbCompleted.isChecked = taskProject.task.completed
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)

    }
}