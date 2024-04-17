package com.forgblord.todo_prototype.fragments.browse

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.forgblord.todo_prototype.data.models.Project
import com.forgblord.todo_prototype.data.viewmodels.ProjectViewModel
import com.forgblord.todo_prototype.databinding.FragmentAddProjectSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.random.Random


class AddProjectFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentAddProjectSheetBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val projectViewModel: ProjectViewModel by viewModels()

    private val colors: List<Int> = listOf(
        Color.parseColor("#BA68C8"),
        Color.parseColor("#4DD0E1"),
        Color.parseColor("#FFD54F")
    )

    private fun randomColorPicker(): Int { return Random.nextInt(0, 3) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddProjectSheetBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnAdd.setOnClickListener {
                val title = this.projectName.text.toString()
                val newProject = Project(
                    project_id=0,
                    title=title,
                    colorCode=colors[randomColorPicker()]
                )

                projectViewModel.addProject(newProject)
                dismiss()
            }

            btnCancel.setOnClickListener {
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}