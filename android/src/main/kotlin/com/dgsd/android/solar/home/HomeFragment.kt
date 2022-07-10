package com.dgsd.android.solar.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.dgsd.android.solar.common.util.collectAsStateLifecycleAware
import com.dgsd.android.solar.extensions.setContent
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = setContent {

        val isLoadingBalance: Boolean by viewModel.isLoadingBalance.collectAsStateLifecycleAware(initial = false)
        val balanceText by viewModel.balanceText.collectAsStateLifecycleAware(initial = null)

        val isLoadingTransactions: Boolean by viewModel.isLoadingTransactionSignatures.collectAsStateLifecycleAware(initial = false)
        val transactions by viewModel.transactions.collectAsStateLifecycleAware(initial = null)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .wrapContentSize(Alignment.Center)
        ) {
            if (isLoadingBalance) {
                Text(text = "Loading Balance")
            } else {
                Text(text = "Balance: $balanceText")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoadingTransactions) {
                Text(text = "Loading Transactions")
            } else {
                Column {
                    Text(text = "Signatures:")
                    if (transactions.isNullOrEmpty()) {
                        Text(text = "None")
                    } else {
                        for (signature in transactions.orEmpty()) {
                            Text(text = signature.toString())
                        }
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.onCreate()
        }
    }

    companion object {

        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}