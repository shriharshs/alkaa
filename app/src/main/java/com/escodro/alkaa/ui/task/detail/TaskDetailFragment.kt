package com.escodro.alkaa.ui.task.detail

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.escodro.alkaa.R
import com.escodro.alkaa.data.local.model.Task
import com.escodro.alkaa.databinding.FragmentTaskDetailBinding
import com.escodro.alkaa.ui.task.list.TaskListFragment
import org.koin.android.architecture.ext.viewModel

/**
 * [Fragment] responsible to show the [Task] details.
 *
 * Created by Igor Escodro on 31/5/18.
 */
class TaskDetailFragment : Fragment() {

    private val viewModel: TaskDetailViewModel by viewModel()

    private var binding: FragmentTaskDetailBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(
                inflater, R.layout.fragment_task_detail,
                container, false
            )
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initComponents()
    }

    private fun initComponents() {
        binding?.setLifecycleOwner(this)
        binding?.viewModel = viewModel

        // TODO Update to safe args when Google supports Parcelable
        val task = arguments?.getParcelable<Task>(TaskListFragment.EXTRA_TASK)
        viewModel.task.value = task
        (activity as? AppCompatActivity)?.supportActionBar?.title = task?.description
    }
}