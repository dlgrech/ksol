package com.dgsd.android.solar.onboarding.restoreaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dgsd.android.solar.R
import com.dgsd.android.solar.di.util.parentViewModel
import com.dgsd.android.solar.extensions.onEach
import com.dgsd.android.solar.onboarding.restoreaccount.model.CandidateAccount
import com.google.android.material.appbar.MaterialToolbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class RestoreAccountSelectAddressFragment :
  Fragment(R.layout.frag_onboarding_restore_account_select_address) {

  private val coordinator: RestoreAccountCoordinator by parentViewModel()
  private val viewModel: RestoreAccountSelectAddressViewModel by viewModel {
    parametersOf(
      checkNotNull(coordinator.seedPhrase),
      checkNotNull(coordinator.passPhrase)
    )
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
    val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
    val loadingIndicator = view.findViewById<ProgressBar>(R.id.loading_indicator)

    toolbar.setNavigationOnClickListener {
      requireActivity().onBackPressed()
    }

    val adapter = CandidateAccountAdapter()
    recyclerView.layoutManager = LinearLayoutManager(requireContext())
    recyclerView.adapter = adapter

    onEach(viewModel.accountData) {
      adapter.items = it
    }

    onEach(viewModel.isLoading) {
      loadingIndicator.isVisible = it
      recyclerView.isVisible = !it
    }

    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
      viewModel.onCreate()
    }
  }

  private class HeroAccountViewModel(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(candidateAccount: CandidateAccount) {
      itemView.findViewById<TextView>(R.id.text).text = candidateAccount.toString()
    }

    companion object {
      fun create(parent: ViewGroup): HeroAccountViewModel {
        val view = LayoutInflater.from(parent.context).inflate(
          R.layout.view_restore_account_candidate_address_hero,
          parent,
          false
        )

        return HeroAccountViewModel(view)
      }
    }
  }

  private class OtherAccountViewModel(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(candidateAccount: CandidateAccount) {
      itemView.findViewById<TextView>(R.id.text).text = candidateAccount.toString()
    }

    companion object {
      fun create(parent: ViewGroup): OtherAccountViewModel {
        val view = LayoutInflater.from(parent.context).inflate(
          R.layout.view_restore_account_candidate_address_other,
          parent,
          false
        )

        return OtherAccountViewModel(view)
      }
    }
  }

  private class CandidateAccountAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items: List<CandidateAccount> = emptyList()
      set(value) {
        field = value
        notifyDataSetChanged()
      }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
      return when (viewType) {
        VIEW_TYPE_HERO_ACCOUNT -> HeroAccountViewModel.create(parent)
        VIEW_TYPE_OTHER_ACCOUNT -> OtherAccountViewModel.create(parent)
        else -> error("Unknown view type: $viewType")
      }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
      when (holder) {
        is HeroAccountViewModel -> holder.bind(items[position])
        is OtherAccountViewModel -> holder.bind(items[position])
      }
    }

    override fun getItemCount(): Int {
      return items.size
    }

    override fun getItemViewType(position: Int): Int {
      return if (position == 0) {
        VIEW_TYPE_HERO_ACCOUNT
      } else {
        VIEW_TYPE_OTHER_ACCOUNT
      }
    }

    companion object {
      const val VIEW_TYPE_HERO_ACCOUNT = 0
      const val VIEW_TYPE_OTHER_ACCOUNT = 1
    }
  }
}