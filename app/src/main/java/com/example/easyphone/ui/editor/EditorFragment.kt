package com.example.easyphone.ui.editor

//import com.example.easyphone.databinding.EditorFragmentBinding
import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.example.easyphone.MainActivity
import com.example.easyphone.R
import com.example.easyphone.databinding.EditorFragmentBinding
import com.example.easyphone.db.ButtonsDatabase
import com.example.easyphone.db.entities.ProgrammedButton
import com.example.easyphone.repository.ButtonsRepository
import com.example.easyphone.utils.ButtonsDisplayer

class EditorFragment : Fragment() {

    companion object {
        fun newInstance() = EditorFragment()
    }

    private val buttonClickAnim = AlphaAnimation(1f, 0.7f)

    private lateinit var binding: EditorFragmentBinding

    private lateinit var viewModel: EditorViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EditorFragmentBinding.inflate(layoutInflater, container, false)

        binding.lifecycleOwner = this

        binding.createButtonButton.setOnClickListener({
            it.findNavController().navigate(
                EditorFragmentDirections.actionEditorFragmentToButtonSettingsFragment(-1)
            )
        })

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        (activity as MainActivity).setActionBarTitle(resources.getString(R.string.editor))
        viewModel.onDeleteButtonsWithoutActions()
        viewModel.updateButtons()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val application = requireNotNull(this.activity).application
        val dataSource = ButtonsRepository(ButtonsDatabase.getInstance(application))
        val viewModelFactory = EditorViewModelFactory(dataSource, application, resources)
        viewModel = ViewModelProvider(this, viewModelFactory).get(EditorViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.buttons.observe(viewLifecycleOwner, Observer {
            val colorAvailable = resources.getColor(R.color.light_green)
            val colorNonAvailable = resources.getColor(R.color.light_red)
            val colorCurrent = resources.getColor(R.color.yellow)

            val buttonsData = ButtonsDisplayer().display(
                resources,
                it,
                binding.buttonTable,
                context,
                { view: View, button: ProgrammedButton ->
                    view.findNavController().navigate(
                        EditorFragmentDirections.actionEditorFragmentToButtonSettingsFragment(button.id)
                    )
                })
            buttonsData.buttons.forEach {
                val buttonData = it.first
                val TAG = "icon bitmap"
                val button = it.second
                button.tag = TAG

                button.setOnLongClickListener {
                    val item = ClipData.Item(it.getTag() as CharSequence)

                    val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
                    val data = ClipData(it.getTag().toString(), mimeTypes, item)

                    viewModel.setCurrentDraggedButton(buttonData)

                    val dragshadow = View.DragShadowBuilder(it)

                    it.startDragAndDrop(
                        data // data to be dragged
                        , dragshadow // drag shadow
                        , it // local data about the drag and drop operation
                        , 0 // flags set to 0 because not using currently
                    )
                }
                button.invalidate()
            }

            buttonsData.textViews.forEach {
                val point = it.first
                val textView = it.second

                textView.setOnDragListener { v, event ->
                    val canPlaceButton = viewModel.canPlaceButton(point)
                    when (event.action) {
                        DragEvent.ACTION_DRAG_STARTED -> {
                            if (event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                                if (canPlaceButton) {
                                    (v as? TextView)?.setBackgroundColor(colorAvailable)
                                } else {
                                    (v as? TextView)?.setBackgroundColor(colorNonAvailable)
                                }
                                v.invalidate()
                                true
                            } else {
                                false
                            }
                        }
                        DragEvent.ACTION_DRAG_ENTERED -> {
                            if (canPlaceButton) {
                                (v as? TextView)?.setBackgroundColor(colorCurrent)
                            }
                            v.invalidate()
                            true
                        }

                        DragEvent.ACTION_DRAG_LOCATION ->
                            // Ignore the event
                            true
                        DragEvent.ACTION_DRAG_EXITED -> {
                            if (canPlaceButton) {
                                (v as? TextView)?.setBackgroundColor(colorAvailable)
                            }
                            v.invalidate()
                            true
                        }
                        DragEvent.ACTION_DROP -> {
                            val item: ClipData.Item = event.clipData.getItemAt(0)

                            val dragData = item.text

                            Log.d("MY_DEBUG", "data: \"${dragData}\"");
                            if (canPlaceButton) {
                                viewModel.saveButtonsNewPosition(point)
                            }
                            (v as? TextView)?.setBackgroundColor(Color.WHITE)
                            v.invalidate()
                            canPlaceButton
                        }

                        DragEvent.ACTION_DRAG_ENDED -> {
                            (v as? TextView)?.setBackgroundColor(Color.WHITE)
                            v.invalidate()
                            true
                        }
                        else -> {
                            Log.e(
                                "DragDrop Example",
                                "Unknown action type received by OnDragListener."
                            )
                            false
                        }
                    }
                }
            }
        })
    }
}