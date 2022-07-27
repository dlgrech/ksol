package com.dgsd.android.solar.common.actionsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dgsd.android.solar.R
import com.dgsd.android.solar.common.actionsheet.model.ActionSheetItem
import com.dgsd.android.solar.common.bottomsheet.BaseBottomSheetFragment

class ActionSheetFragment : BaseBottomSheetFragment() {

  var sheetTitle: CharSequence? = null
    set(value) {
      field = value

      val titleView = view?.findViewById<TextView>(R.id.title)
      if (titleView != null) {
        titleView.text = value
        titleView.isVisible = !value.isNullOrEmpty()
      }
    }

  var actionSheetItems: List<ActionSheetItem> = emptyList()
    set(value) {
      field = value

      val adapter =
        view?.findViewById<RecyclerView>(R.id.recycler_view)?.adapter as? ActionSheetAdapter
      adapter?.notifyDataSetChanged()
    }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.frag_action_sheet, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    view.requireViewById<RecyclerView>(R.id.recycler_view).apply {
      adapter = ActionSheetAdapter()
      layoutManager = LinearLayoutManager(requireContext())
    }

    view.requireViewById<TextView>(R.id.title).apply {
      text = sheetTitle
      isVisible = !sheetTitle.isNullOrEmpty()
    }
  }

  private inner class ActionSheetAdapter : RecyclerView.Adapter<ActionSheetViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActionSheetViewHolder {
      val view = LayoutInflater.from(parent.context).inflate(
        R.layout.view_action_sheet_row,
        parent,
        false
      ) as TextView
      return ActionSheetViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActionSheetViewHolder, position: Int) {
      holder.bind(actionSheetItems[position])
    }

    override fun getItemCount(): Int {
      return actionSheetItems.size
    }
  }

  private inner class ActionSheetViewHolder(itemView: TextView) :
    RecyclerView.ViewHolder(itemView) {

    fun bind(actionSheetItem: ActionSheetItem) {
      (itemView as TextView).apply {
        text = actionSheetItem.title
        setCompoundDrawablesRelativeWithIntrinsicBounds(actionSheetItem.icon, null, null, null)
      }
      itemView.setOnClickListener {
        actionSheetItem.onClick.invoke()
        dismissAllowingStateLoss()
      }
    }
  }
}