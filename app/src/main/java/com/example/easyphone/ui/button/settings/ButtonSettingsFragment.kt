package com.example.easyphone.ui.button.settings

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.easyphone.R
import com.example.easyphone.actions.utils.Actions
import com.example.easyphone.databinding.ButtonSettingsFragmentBinding
import com.example.easyphone.db.ButtonsDatabase
import com.example.easyphone.repository.ButtonsRepository


class ButtonSettingsFragment : Fragment() {

    companion object {
        fun newInstance() = ButtonSettingsFragment()
    }

    private val displayMetrics = DisplayMetrics()

    private lateinit var binding: ButtonSettingsFragmentBinding

    private lateinit var viewModel: ButtonSettingsViewModel

    private lateinit var validator: ButtonSettingsValidator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        validator = ButtonSettingsValidator(resources)
        binding = ButtonSettingsFragmentBinding.inflate(layoutInflater, container, false)
        binding.lifecycleOwner = this
        this.activity?.windowManager?.defaultDisplay?.getMetrics(displayMetrics)

        val application = requireNotNull(this.activity).application
        val dataSource = ButtonsRepository(ButtonsDatabase.getInstance(application))
        val viewModelFactory = ButtonSettingsViewModelFactory(dataSource, application, resources)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ButtonSettingsViewModel::class.java)
        binding.viewModel = viewModel

        binding.buttonNameField.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun afterTextChanged(s: Editable) {
                val value = s.toString()
                val field = "text"
                val error = validator.validateText(value)
                if (error != null) {
                    binding.buttonLengthField.error = error
                    viewModel.onErrorStatusChange(field, true)
                } else {
                    viewModel.onErrorStatusChange(field, false)
                    viewModel.onTextChange(value)
                }
            }
        })

        binding.buttonLengthField.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun afterTextChanged(s: Editable) {
                val value = if (s.toString() == "") 1 else s.toString().toInt()
                val field = "width"
                val error = validator.validateWidth(value)
                if (error != null) {
                    binding.buttonLengthField.error = error
                    viewModel.onErrorStatusChange(field, true)
                } else {
                    viewModel.onErrorStatusChange(field, false)
                    viewModel.onLengthChange(value)
                }
            }
        })

        binding.buttonHeightField.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }
            override fun afterTextChanged(s: Editable) {
                val value = if (s.toString() == "") 1 else s.toString().toInt()
                val field = "height"
                val error = validator.validateHeight(value)
                if (error != null) {
                    binding.buttonHeightField.error = error
                    viewModel.onErrorStatusChange(field, true)
                } else {
                    viewModel.onErrorStatusChange(field, false)
                    viewModel.onHeightChange(value)
                }
            }
        })

        viewModel.color.observe(viewLifecycleOwner, Observer {
            binding.buttonPreview.setBackgroundColor(it)
        })

        viewModel.buttonLengthInColumns.observe(viewLifecycleOwner, Observer<Int> {
            val length =
                it * ((displayMetrics.widthPixels).toInt() / resources.getInteger(R.integer.column_number)
                    .toInt() - 16)
            val layoutParams = binding.buttonPreview.layoutParams
            layoutParams.width = length
            binding.buttonPreview.layoutParams = layoutParams
        })

        viewModel.buttonHeightInRows.observe(viewLifecycleOwner, Observer<Int> {
            val height =
                it * ((displayMetrics.heightPixels).toInt() / resources.getInteger(R.integer.row_number)
                    .toInt() - 16)
            val layoutParams = binding.buttonPreview.layoutParams
            layoutParams.height = height
            binding.buttonPreview.layoutParams = layoutParams
        })

        viewModel.isExistingButton.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.deleteButton.visibility = Button.VISIBLE
            } else {
                binding.deleteButton.visibility = Button.GONE
            }
        })

        viewModel.moveBackEvent.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.confirmButton.findNavController().navigate(
                    ButtonSettingsFragmentDirections.actionButtonSettingsFragmentToEditorFragment()
                )
                viewModel.onMoveBackComplete()
            }
        })

        viewModel.haveErrorsEvent.observe(viewLifecycleOwner, Observer {
            if (it) {
                val toast = Toast(context)
                toast.setText(viewModel.getCurrentErrorMessage())
                toast.duration = Toast.LENGTH_SHORT
                toast.show()
                viewModel.onHaveErrorsEventComplete()
            }
        })

        viewModel.moveToActionEditEvent.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.confirmButton.findNavController().navigate(
                    ButtonSettingsFragmentDirections.actionButtonSettingsFragmentToActionEditFragment(viewModel.action.value ?: "", viewModel.currentButtonId)
                )
                viewModel.onActionChangeComplete()
            }
        })

        viewModel.createActionChooserEvent.observe(viewLifecycleOwner, Observer {
            if (it) {
                createActionsDialog(viewModel, context).show()
            }
        })

        binding.colorField.setOnClickListener {
            createColorsDialog(viewModel, context).show()
        }

        binding.deleteButton.setOnClickListener {
            viewModel.onDeleteCurrentButton()
        }

        binding.confirmButton.setOnClickListener {
            viewModel.onSubmit()
        }


        return binding.root
    }

    override fun onResume() {
        super.onResume()
        val args = ButtonSettingsFragmentArgs.fromBundle(requireArguments())
        viewModel.updateCurrentButton(args.buttonId)
        viewModel.updateButtons()
    }

    private fun createColorsDialog(viewModel: ButtonSettingsViewModel, context: Context?): AlertDialog.Builder {
        val colors = viewModel.colors.keys.toTypedArray()
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Pick a color")
        builder.setItems(colors, DialogInterface.OnClickListener { dialog, which ->
            viewModel.onColorChange(colors[which])
        })

        return builder
    }

    private fun createActionsDialog(viewModel: ButtonSettingsViewModel, context: Context?): AlertDialog.Builder {
        val actions = Actions.data.entries.map { it.value.name }.toTypedArray()
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setTitle("Pick action type")
        builder.setItems(actions, DialogInterface.OnClickListener { dialog, which ->
            viewModel.onActionChange(actions[which])
        })

        return builder
    }
}



