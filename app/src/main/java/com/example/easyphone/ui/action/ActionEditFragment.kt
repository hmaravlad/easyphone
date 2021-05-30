package com.example.easyphone.ui.action

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.easyphone.MainActivity
import com.example.easyphone.R
import com.example.easyphone.actions.utils.ActionArgData
import com.example.easyphone.databinding.ActionEditFragmentBinding
import com.example.easyphone.db.ButtonsDatabase
import com.example.easyphone.repository.ButtonsRepository


class ActionEditFragment : Fragment() {

    companion object {
        fun newInstance() = ActionEditFragment()
        private const val MY_PERMISSIONS_REQUEST_LOCATION = 99

    }

    private lateinit var binding: ActionEditFragmentBinding

    private lateinit var viewModel: ActionEditViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ActionEditFragmentBinding.inflate(layoutInflater, container, false)

        binding.lifecycleOwner = this

        val application = requireNotNull(this.activity).application
        val dataSource = ButtonsRepository(ButtonsDatabase.getInstance(application))
        val args = ActionEditFragmentArgs.fromBundle(requireArguments())
        val viewModelFactory = ActionEditViewModelFactory(
            dataSource,
            application,
            args.buttonId,
            args.myAction
        )
        viewModel = ViewModelProvider(this, viewModelFactory).get(ActionEditViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.isActionChooseApp.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.confirmButton.visibility = Button.GONE
                binding.setAppButton.visibility = Button.VISIBLE
            } else {
                binding.confirmButton.visibility = Button.VISIBLE
                binding.setAppButton.visibility = Button.GONE
            }
        })

        viewModel.getActionData().args.forEach {
            val editText = createTextInput(it, context, resources)
            editText.addTextChangedListener(getArgTextWatcher(viewModel, it.name))
            binding.argsLayout.addView(editText)
        }

        viewModel.toEditorEvent.observe(viewLifecycleOwner, Observer {
            if (it) {
                binding.confirmButton.findNavController().navigate(
                    ActionEditFragmentDirections.actionActionEditFragmentToEditorFragment()
                )
                viewModel.onToEditorCompleted()
            }
        })

        viewModel.setAppEvent.observe(viewLifecycleOwner, Observer {
            if (it) {
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_LAUNCHER)


                val intentPick = Intent()
                intentPick.action = Intent.ACTION_PICK_ACTIVITY
                intentPick.putExtra(Intent.EXTRA_TITLE, "Launch using")
                intentPick.putExtra(Intent.EXTRA_INTENT, intent)
                this.startActivityForResult(intentPick, 1)

                viewModel.onSetAppComleted()
            }
        })

        viewModel.needPermissionEvent.observe(viewLifecycleOwner, Observer {
            if (it) {
                if (checkLocationPermissionAbsence()) {
                    requestLocationPermission()
                }
                viewModel.needPermissionEventCompleted()
            }
        })

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (activity is MainActivity) (activity as MainActivity).setActionBarTitle(resources.getString(R.string.edit_action))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (data != null && data.component != null) {
                val appName = data.component!!.packageName
                viewModel.onAppArgChange(appName)
                viewModel.onSubmit()
            }
        }
    }

    private fun checkLocationPermissionAbsence(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED)
    }

    private fun requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                activity as Activity,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        } else {
            ActivityCompat.requestPermissions(
                activity as Activity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(context, "permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

    private fun createTextInput(it: ActionArgData, context: Context?, resources: Resources): EditText {
        val editText = EditText(context, null, R.attr.myEditTextStyle)
        editText.inputType = it.inputType
        if (it.inputType == InputType.TYPE_TEXT_FLAG_MULTI_LINE) {
            editText.isSingleLine = false
            editText.inputType = (InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
            editText.imeOptions = EditorInfo.IME_FLAG_NO_ENTER_ACTION
            editText.setLines(1);
            editText.maxLines = 15;
        }
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        val margin = resources.getDimension(R.dimen.default_margin)

        layoutParams.setMargins(margin.toInt())
        editText.layoutParams = layoutParams
        editText.hint = it.text
        editText.invalidate()

        return editText
    }

    private fun getArgTextWatcher(viewModel: ActionEditViewModel, argName: String): TextWatcher {
        return object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                viewModel.onArgChange(argName, s.toString())
            }
        }
    }
}

