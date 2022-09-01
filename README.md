<p align="center">
  <img src="./android/src/main/ic_launcher-playstore.png">
</p>

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

The repository also includes a reference Android wallet - called Solar - that demonstrates how the library can be used to provide basic wallet functionality.

### Structure

The repository is structured into 4 high-level gradle modules:

- `lib`: Provides the core functionality of the library. 
  - The [`SolanaApi`](https://github.com/dlgrech/ksol/blob/main/lib/src/main/kotlin/com/dgsd/ksol/SolanaApi.kt) interface provides all the high-level functionality of the JSON RPC.
- `solpay`: High-level library for working with SolPay urls. 
  - The [`SolPay`](https://github.com/dlgrech/ksol/blob/main/solpay/src/main/kotlin/com/dgsd/ksol/solpay/SolPay.kt) interface provides an entry point to all Solpay-related functionality.
- `cli`: A Kotlin command line app for using the functionality of the core library. 
  - This could be used as a (pointless) replacement for the standard [Solana CLI tools](https://docs.solana.com/cli). 
  - The [built in commands](https://github.com/dlgrech/ksol/tree/main/cli/src/main/kotlin/com/dgsd/ksol/cli) also offer a good look at how to use the library. The [SendCommand](https://github.com/dlgrech/ksol/blob/main/cli/src/main/kotlin/com/dgsd/ksol/cli/send/SendCommand.kt), for example, shows creating, signing and sending a transaction as well as listening for finalization.
- `android`: The Solar wallet, a non-custodial Android wallet app
  - Uses the ksol library to generate wallets/send transactions
  - Integrates [SolPay](https://github.com/solana-labs/solana-pay)
  - Integrates Solana [mobile-wallet-adapter](https://github.com/solana-mobile/mobile-wallet-adapter) to provide wallet functionality to other apps

