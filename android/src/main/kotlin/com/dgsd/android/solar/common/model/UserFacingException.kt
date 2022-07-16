package com.dgsd.android.solar.common.model


/**
 * [RuntimeException] subclass whos message can be presented to the user
 */
class UserFacingException(
  val userVisibleMessage: String
) : RuntimeException(userVisibleMessage)