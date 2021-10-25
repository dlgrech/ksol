package com.dgsd.ksol.jsonrpc.networking

import java.io.IOException

class RpcIOException(message: String, cause: Throwable?) : IOException(message, cause)