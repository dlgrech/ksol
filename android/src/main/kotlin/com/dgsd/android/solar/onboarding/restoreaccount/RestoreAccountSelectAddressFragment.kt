package com.dgsd.android.solar.onboarding.restoreaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.ui.SolTokenFormatter
import com.dgsd.android.solar.common.util.getTextColorForLamports
import com.dgsd.android.solar.di.util.parentViewModel
import com.dgsd.android.solar.extensions.getColorAttr
import com.dgsd.android.solar.extensions.onEach
import com.dgsd.android.solar.onboarding.restoreaccount.model.CandidateAccount
import com.google.android.material.appbar.MaterialToolbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class RestoreAccountSelectAddressFragment :
  Fragment(R.layout.frag_onboarding_restore_account_select_address) {

  private val coordinator: RestoreAccountCoordinator by parentViewModel()
  private val viewModel: RestoreAccountSelectAddressViewModel by viewModel {
    parametersOf(checkNotNull(coordinator.seedInfo))
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val toolbar = view.findViewById<MaterialToolbar>(R.id.toolbar)
    val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)

    toolbar.setNavigationOnClickListener {
      requireActivity().onBackPressed()
    }

    val adapter = CandidateAccountAdapter(
      onClickListener = { candidateAccount ->
        viewModel.onCandidateAccountClicked(candidateAccount)
      }
    )
    recyclerView.layoutManager = LinearLayoutManager(requireContext())
    recyclerView.adapter = adapter

    onEach(viewModel.accountData) {
      adapter.items = it
    }

    onEach(viewModel.continueWithResult) { keyPair ->
      coordinator.onWalletSelected(keyPair)
    }

    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
      viewModel.onCreate()
    }
  }

  private class HeroAccountViewHolder(
    view: View,
    private val onClickListener: (CandidateAccount) -> Unit,
  ) : RecyclerView.ViewHolder(view) {

    private val shimmerSuggestedWalletTitle = view.findViewById<View>(R.id.shimmer_suggested_wallet)
    private val shimmerAccountKey = view.findViewById<View>(R.id.shimmer_account_key)
    private val shimmerAmount = view.findViewById<View>(R.id.shimmer_amount)
    private val shimmerUseThisAccountButton = view.findViewById<View>(R.id.shimmer_use_this_account)

    private val suggestedWalletTitle = view.findViewById<View>(R.id.suggested_wallet)
    private val accountKey = view.findViewById<TextView>(R.id.account_key)
    private val amount = view.findViewById<TextView>(R.id.amount)
    private val useThisAccountButton = view.findViewById<View>(R.id.use_this_account)


    fun bind(candidateAccount: CandidateAccount) {
      shimmerSuggestedWalletTitle.isInvisible = candidateAccount !is CandidateAccount.Empty
      shimmerAccountKey.isInvisible = candidateAccount !is CandidateAccount.Empty
      shimmerUseThisAccountButton.isInvisible = candidateAccount !is CandidateAccount.Empty
      shimmerAmount.isInvisible =
        !(candidateAccount is CandidateAccount.Empty || candidateAccount is CandidateAccount.Loading)

      accountKey.text = candidateAccount.keyPairOrNull()?.publicKey?.toBase58String()
      amount.text = if (candidateAccount is CandidateAccount.AccountWithBalance) {
        itemView.context.getString(
          R.string.lamport_amount_with_sol_suffix,
          SolTokenFormatter.format(candidateAccount.lamports)
        )
      } else {
        null
      }

      if (candidateAccount is CandidateAccount.AccountWithBalance) {
        amount.setTextColor(getTextColorForLamports(itemView.context, candidateAccount.lamports))
      } else if (candidateAccount is CandidateAccount.Error) {
        amount.setTextColor(itemView.context.getColorAttr(R.attr.colorError))
        amount.setText(R.string.sol_amount_error_loading)
      }

      suggestedWalletTitle.isInvisible = shimmerSuggestedWalletTitle.isVisible
      accountKey.isInvisible = shimmerAccountKey.isVisible
      amount.isInvisible = shimmerAmount.isVisible
      useThisAccountButton.isInvisible = shimmerUseThisAccountButton.isVisible

      useThisAccountButton.setOnClickListener {
        onClickListener.invoke(candidateAccount)
      }
    }

    companion object {
      fun create(
        parent: ViewGroup,
        onClickListener: (CandidateAccount) -> Unit,
      ): HeroAccountViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
          R.layout.view_restore_account_candidate_address_hero,
          parent,
          false
        )

        return HeroAccountViewHolder(view, onClickListener)
      }
    }
  }

  private class OtherAccountViewHolder(
    view: View,
    private val onClickListener: (CandidateAccount) -> Unit,
  ) : RecyclerView.ViewHolder(view) {

    private val shimmerAccountKey = view.findViewById<View>(R.id.shimmer_account_key)
    private val shimmerAmount = view.findViewById<View>(R.id.shimmer_amount)

    private val accountKey = view.findViewById<TextView>(R.id.account_key)
    private val amount = view.findViewById<TextView>(R.id.amount)

    fun bind(candidateAccount: CandidateAccount) {
      shimmerAccountKey.isInvisible = candidateAccount !is CandidateAccount.Empty
      shimmerAmount.isInvisible =
        !(candidateAccount is CandidateAccount.Empty || candidateAccount is CandidateAccount.Loading)

      accountKey.text = candidateAccount.keyPairOrNull()?.publicKey?.toBase58String()
      amount.text = if (candidateAccount is CandidateAccount.AccountWithBalance) {
        SolTokenFormatter.format(candidateAccount.lamports)
      } else {
        null
      }

      if (candidateAccount is CandidateAccount.AccountWithBalance) {
        amount.setTextColor(getTextColorForLamports(itemView.context, candidateAccount.lamports))
      } else if (candidateAccount is CandidateAccount.Error) {
        amount.setTextColor(itemView.context.getColorAttr(R.attr.colorError))
        amount.setText(R.string.sol_amount_error_loading)
      }

      accountKey.isVisible = !shimmerAccountKey.isVisible
      amount.isVisible = !shimmerAmount.isVisible

      itemView.setOnClickListener {
        onClickListener.invoke(candidateAccount)
      }
    }

    companion object {
      fun create(
        parent: ViewGroup,
        onClickListener: (CandidateAccount) -> Unit,
      ): OtherAccountViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
          R.layout.view_restore_account_candidate_address_other,
          parent,
          false
        )

        return OtherAccountViewHolder(view, onClickListener)
      }
    }
  }

  private class AlternateWalletsHeader(view: View) : RecyclerView.ViewHolder(view) {

    private val title = view.findViewById<View>(R.id.title)
    private val shimmerTitle = view.findViewById<View>(R.id.shimmer_title)

    fun bind(showLoadingState: Boolean) {
      title.isInvisible = showLoadingState
      shimmerTitle.isInvisible = !showLoadingState
    }

    companion object {

      fun create(
        parent: ViewGroup,
      ): AlternateWalletsHeader {
        val view = LayoutInflater.from(parent.context).inflate(
          R.layout.view_restore_account_alternate_wallet_header,
          parent,
          false
        )

        return AlternateWalletsHeader(view)
      }
    }
  }

  private class CandidateAccountAdapter(
    private val onClickListener: (CandidateAccount) -> Unit,
  ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items: List<CandidateAccount> = emptyList()
      set(value) {
        field = value
        notifyDataSetChanged()
      }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
      return when (viewType) {
        VIEW_TYPE_HERO_ACCOUNT -> HeroAccountViewHolder.create(parent, onClickListener)
        VIEW_TYPE_OTHER_ACCOUNT -> OtherAccountViewHolder.create(parent, onClickListener)
        VIEW_TYPE_ALTERNATE_WALLETS_HEADER -> AlternateWalletsHeader.create(parent)
        else -> error("Unknown view type: $viewType")
      }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
      val candidateAccount = if (position == 0) items.first() else items[position - 1]

      when (holder) {
        is HeroAccountViewHolder -> holder.bind(candidateAccount)
        is OtherAccountViewHolder -> holder.bind(candidateAccount)
        is AlternateWalletsHeader -> holder.bind(
          items.any { it is CandidateAccount.Empty }
        )
      }

      holder.itemView.isEnabled = when (candidateAccount) {
        is CandidateAccount.AccountWithBalance -> true
        is CandidateAccount.Empty -> false
        is CandidateAccount.Error -> true
        is CandidateAccount.Loading -> true
      }
    }

    override fun getItemCount(): Int {
      return items.size + 1 // +1 for "alternative addresses" header
    }

    override fun getItemViewType(position: Int): Int {
      return when (position) {
        0 -> VIEW_TYPE_HERO_ACCOUNT
        1 -> VIEW_TYPE_ALTERNATE_WALLETS_HEADER
        else -> VIEW_TYPE_OTHER_ACCOUNT
      }
    }

    companion object {
      const val VIEW_TYPE_HERO_ACCOUNT = 0
      const val VIEW_TYPE_ALTERNATE_WALLETS_HEADER = 1
      const val VIEW_TYPE_OTHER_ACCOUNT = 2
    }
  }
}