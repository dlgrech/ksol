<h1 align="center">kSol - Kotlin library for Solana</h1>

kSol is a kotlin library for interacting with the Solana blockchain.

Features include:

- First-class Kotlin API for interacting with the [Solana JSON-RPC](https://docs.solana.com/developing/clients/jsonrpc-api) API.
- Key management, including generate new public/private keys, seed phrases etc
- Crafting and parsing Solana transactions
- Signing and sending transactions
- Subscribing to transaction/account updates 
- [SolPay](https://github.com/solana-labs/solana-pay) integration (including transfer & transaction requests)

The goal of the library is to provide everything that is needed to integrate with the Solana blockchain without relying on libraries from any other environment (like executing JS or native code). The library could be used in a native Android app, command line tool or as part of a JVM-backend service.

### Structure

The repository is structured into some high-level gradle modules:

- `ksol-core`: Provides common models and functionality. Depended on by other ksol modules
- `ksol-keygen`: Utilities for generating keypairs and dealing with seed phrases
- `ksol-rpc`: A coroutines based wrapper around the Solana JSON-RPC and subscriptions API
  - The [`SolanaApi`](https://github.com/dlgrech/ksol/blob/main/ksol-rpc/src/main/kotlin/com/dgsd/ksol/SolanaApi.kt) interface provides all the high-level functionality of the JSON RPC.
- `ksol-solpay`: High-level library for working with SolPay urls.
  - The [`SolPay`](https://github.com/dlgrech/ksol/blob/main/ksol-solpay/src/main/kotlin/com/dgsd/ksol/solpay/SolPay.kt) interface provides an entry point to all Solpay-related functionality.
- `ksol-cli`: A Kotlin command line app for using the functionality of the core library.
  - This could be used as a (pointless) replacement for the standard [Solana CLI tools](https://docs.solana.com/cli).
  - The [built in commands](https://github.com/dlgrech/ksol/tree/main/ksol-cli/src/main/kotlin/com/dgsd/ksol/cli) also offer a good look at how to use the library. The [SendCommand](https://github.com/dlgrech/ksol/blob/main/ksol-cli/src/main/kotlin/com/dgsd/ksol/cli/send/SendCommand.kt), for example, shows creating, signing and sending a transaction as well as listening for finalization.

### Dependencies

Different functionality is split into different dependencies.

You can find the latest version of each library [here](https://search.maven.org/search?q=io.github.dlgrech)

```
implementation "io.github.dlgrech:ksol-core:<LATEST-VERSION>"
implementation "io.github.dlgrech:ksol-solpay:<LATEST-VERSION>"
implementation "io.github.dlgrech:ksol-rpc:<LATEST-VERSION>"
implementation "io.github.dlgrech:ksol-keygen:<LATEST-VERSION>"
```