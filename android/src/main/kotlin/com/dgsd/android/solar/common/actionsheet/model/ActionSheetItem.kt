package com.dgsd.android.solar.common.actionsheet.model

data class ActionSheetItem(
    val title: CharSequence,
    val onClick: () -> Unit,
)