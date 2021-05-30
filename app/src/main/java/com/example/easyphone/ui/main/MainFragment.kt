package com.example.easyphone.ui.main

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.easyphone.MainActivity
import com.example.easyphone.R
import com.example.easyphone.actions.utils.ActionFactory
import com.example.easyphone.databinding.MainFragmentBinding
import com.example.easyphone.db.ButtonsDatabase
import com.example.easyphone.db.entities.ProgrammedButton
import com.example.easyphone.repository.ButtonsRepository
import com.example.easyphone.utils.ButtonsDisplayer


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var binding: MainFragmentBinding

    private lateinit var viewModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = MainFragmentBinding.inflate(layoutInflater, container, false)

        binding.lifecycleOwner = this
        setHasOptionsMenu(true);

        return binding.root
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.overflow_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(
            item,
            requireView().findNavController()
        )
                || super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setActionBarTitle(resources.getString(R.string.app_name))
        viewModel.updateButtons()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val application = requireNotNull(this.activity).application
        val dataSource = ButtonsRepository(ButtonsDatabase.getInstance(application))
        val viewModelFactory = MainViewModelFactory(dataSource, application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.buttons.observe(viewLifecycleOwner, Observer {
            val actionButtonDataList = it
            val buttonsData = ButtonsDisplayer().display(
                resources,
                it.map { it.button },
                binding.buttonTable,
                context,
                { view: View, button: ProgrammedButton ->
                    Log.d("MY_DEBUG", "Action Should be called");
                })

            buttonsData.buttons.forEach {
                val buttonData = it.first
                val button = it.second
                val data =
                    actionButtonDataList.find { it.button.id == buttonData.id } ?: throw Error(
                        "Unexpected error"
                    )
                val action = ActionFactory.create(data.action.type, data.args)
                button.setOnClickListener {
                    action.performAction(requireContext())
                }
            }
        })
    }

}