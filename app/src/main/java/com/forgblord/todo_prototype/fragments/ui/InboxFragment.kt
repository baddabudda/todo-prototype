package com.forgblord.todo_prototype.fragments.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.forgblord.todo_prototype.Task
import com.forgblord.todo_prototype.TaskListViewModel
import com.forgblord.todo_prototype.databinding.FragmentInboxBinding
import com.forgblord.todo_prototype.fragments.base.TaskListFragment
import java.util.UUID

class InboxFragment: TaskListFragment<FragmentInboxBinding>()  {
    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentInboxBinding
        get() = FragmentInboxBinding::inflate

    override val getRecyclerView: (FragmentInboxBinding) -> RecyclerView
        get() = FragmentInboxBinding::rvInboxList
    override val getOnClickNavigation: (id: UUID) -> Unit
        get() = { id ->
            findNavController().navigate(
                InboxFragmentDirections.openTask(id)
            )
        }

    override val data: MutableList<Task>
        get() = taskListViewModel.getAllTasks()
}