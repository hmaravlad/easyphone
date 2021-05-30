package com.example.easyphone.ui.account

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.easyphone.MainActivity
import com.example.easyphone.R
import com.example.easyphone.databinding.AccountFragmentBinding
import com.example.easyphone.db.ButtonsDatabase
import com.example.easyphone.repository.ButtonsRepository
import com.example.easyphone.repository.DataSyncRepository
import com.example.easyphone.utils.InternetChecker
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task


class AccountFragment : Fragment() {

    companion object {
        fun newInstance() = AccountFragment()
    }

    private lateinit var binding: AccountFragmentBinding

    private lateinit var viewModel: AccountViewModel

    private lateinit var mGoogleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AccountFragmentBinding.inflate(layoutInflater, container, false)

        binding.lifecycleOwner = this

        val application = requireNotNull(this.activity).application
        val buttonsRepository = ButtonsRepository(ButtonsDatabase.getInstance(application))
        val dataSyncRepository = DataSyncRepository()
        val internetChecker = InternetChecker(requireContext())
        val viewModelFactory = AccountViewModelFactory(
            buttonsRepository,
            dataSyncRepository,
            application,
            internetChecker
        )
        viewModel = ViewModelProvider(this, viewModelFactory).get(AccountViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.haveInternet.observe(viewLifecycleOwner, Observer {
            if (!it) {
                binding.retryButton.visibility = Button.VISIBLE
                binding.importButton.visibility = Button.GONE
                binding.exportButton.visibility = Button.GONE
                binding.signInButton.visibility = Button.GONE
            } else {
                binding.retryButton.visibility = Button.GONE
                if (viewModel.loggedInGoogle.value ?: false) {
                    binding.importButton.visibility = Button.VISIBLE
                    binding.exportButton.visibility = Button.VISIBLE
                    binding.signInButton.visibility = Button.GONE
                } else {
                    binding.importButton.visibility = Button.GONE
                    binding.exportButton.visibility = Button.GONE
                    binding.signInButton.visibility = Button.VISIBLE
                }

            }
        })

        viewModel.loggedInGoogle.observe(viewLifecycleOwner, Observer {
            if (viewModel.haveInternet.value ?: false) {
                if (!it) {
                    binding.importButton.visibility = Button.GONE
                    binding.exportButton.visibility = Button.GONE
                    binding.signInButton.visibility = Button.VISIBLE
                } else {
                    binding.importButton.visibility = Button.VISIBLE
                    binding.exportButton.visibility = Button.VISIBLE
                    binding.signInButton.visibility = Button.GONE
                }
            }
        })


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(resources.getString(R.string.google_key))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso);

        binding.signInButton.setOnClickListener {
            signIn()
        };

        viewModel.apiErrorEvent.observe(viewLifecycleOwner, Observer {
            if (it) {
                val toast = Toast(requireContext())
                toast.setText(viewModel.errorText)
                toast.show()

                Log.d("My_DEBUG", "need to relog: ${viewModel.needToRelog}")

                if (viewModel.needToRelog) {
                    mGoogleSignInClient.signOut()
                    viewModel.onGetLastSignedAccount(null)
                }
                viewModel.onApiErrorEventEnded()
            }
        })

        viewModel.messageEvent.observe(viewLifecycleOwner, Observer {
            if (it) {
                val toast = Toast(requireContext())
                toast.setText(viewModel.messageText)
                toast.show()
                viewModel.onApiErrorEventEnded()
            }
        })

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 1) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, 1)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            viewModel.onGetLastSignedAccount(account)
        } catch (e: ApiException) {
            viewModel.onGetLastSignedAccount(null)
        }
    }

    override fun onStart() {
        super.onStart()
        val account = GoogleSignIn.getLastSignedInAccount(requireContext())
        viewModel.onGetLastSignedAccount(account)
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setActionBarTitle(resources.getString(R.string.account))
        viewModel.checkInternet()
        viewModel.onLaunch()
    }
}
