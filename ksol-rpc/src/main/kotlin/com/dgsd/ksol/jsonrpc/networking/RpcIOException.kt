package com.dgsd.ksol.jsonrpc.networking

import java.io.IOException

internal class RpcIOException(message: String, cause: Throwable?) : IOException(message, cause)