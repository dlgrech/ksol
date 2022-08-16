# kSol - Kotlin library for Solana

## What is it?

kSol is a kotlin-based wrapper around the [Solana JSON-RPC](https://docs.solana.com/developing/clients/jsonrpc-api) API.

It also natively supports key management and everything needed to interact with a Solana Wallet, as well as send/sign transactions.

The goal is to implement all this functionality in Kotlin itself without relying on libraries from any other environment (like executing JS or native code), with an eye on using this for native Android app development.

The repository also includes a reference Android wallet - called Solar - that demonstrates how the library can be used to provide basic wallet functionality.

## Structure

The repository is structured into 4 high-level gradle modules:

- `lib`: Provides the core functionality of the library. The `SolanaApi` interface provides all the high-level functionality of the JSON RPC.
- `solpay`: High-level library for working with SolPay urls. The `SolPay` interface provides an entrypoint to all Solpay-related functionality.
- `cli`: A Kotlin command line app for using the functionality of the core library. This could be used as a (pointless) replacement for the standard [Solana CLI tools](https://docs.solana.com/cli)
- `android`: The Solar wallet, a non-custodial Android wallet app using the library to generate wallets/send transactions

## Android App - How to use

Assuming you have the Android SDK set up, you can build the app simply using `./gradlew :android:assembleDebug` from the root of the repository

## CLI - How to use

The `ksol.sh` script at the root of the repository is your starting point to using the library. 

It builds the CLI module and executes the given command:

```
~/ksol: ./ksol.sh --help
Usage: ksol [OPTIONS] COMMAND [ARGS]...

  Interact with the ksol Solana library

Options:
  -h, --help  Show this message and exit

Commands:
  rpc     Execute Solana JSON RPC methods
  keygen  Operations relating to Solana public/private keys
  send    Send a transaction
```

Use `--help` on child commands to get more information

## Common commands

### Generate a seed phrase:

```
~/ksol: ./ksol.sh keygen mnemonic

[notice, day, enhance, use, engage, disagree, above, caught, walnut, fury, awake, gesture, welcome, hurdle, critic, toward, cost, trick, forum, differ, develop, easy, hobby, project]
```

### Generate a public/private key pair from mnemonic

```
# Using seed phrase from above
~/ksol: ./ksol.sh keygen seed notice day enhance use engage disagree above caught walnut fury awake gesture welcome hurdle critic toward cost trick forum differ develop easy hobby project

KeyPair(publicKey=4eUAao5VfPbR7mcytqRuLeZNQNJFTo8Tmf56joZRt6sd, privateKey=5gBjQfeUyr16zuSx2DvFfwjosWK6pd6Y1TzCCaBZsDTLtphawQirbVPRMSSyWCzRAvXDfmrtV6gJHB6jrGfz4fk7)
```

### Generate account keypairs from a seed phrase:

```
# Using seed phrase from above
~/ksol: ./ksol.sh keygen accounts notice day enhance use engage disagree above caught walnut fury awake gesture welcome hurdle critic toward cost trick forum differ develop easy hobby project

account index #0: KeyPair(publicKey=9a3hDy7tsbMLUCM8ADfUvFYMGPzkMbJavrqF2mXRpMQZ, privateKey=4w6ayE5hLKt2hVKaJaE7fKGri75HBWdYxcX8kTwkpnX89M46NkzLXKd6yWCX6qkJLcABBJR52qDT9KzZ9x1Haf9o)
account index #1: KeyPair(publicKey=4s9GDoCWBveJGiNEf1GruCfAmiwJpBoAVXtp7eMZTjDp, privateKey=3KQ7ycafpPq54on2VQGe8yGK5rFEUtVZJd2Er1PcN2EewynWjpHHgMw3VgKEfaW43VQZMGiQTugiBrhH5EECwiiJ)
account index #2: KeyPair(publicKey=8f7pwijXKp5PZsqLhrQDFsMjQAQW1GoPdx7NCwY8VNii, privateKey=4KwSg2SWjQNfD7RGYUnhQQ1sLmVSpPcYzhEwREHFPqJEeJcrjY33w5QKKjnVdJx9ZdfHecWVt1HsuzQZr8FAxNVc)
account index #3: KeyPair(publicKey=kL61cPBRBUhyFtqXAADBQodLwuSVc87kw5AqEkhs1wE, privateKey=5g27nUg1Xec7tT2ybY6HcshB8Ezx4oFtrPYHC6RPqtG7vpy8Bbe13uoVV6FBhjFTkfWQVhfMQWYEgAJqk1NmW1kL)
account index #4: KeyPair(publicKey=E3XkzD5VtHGmEW3NHm4kyBUoQ1VUNq3URKMnt2ELTk5m, privateKey=67k3ax8XBPdapWmMpHa6YCy6po8pPy5FoWSrPks5gDgpY8XHf98v6Rv34d7prsgsnHTMuBoazK5DDWfGwDBETBtj)
account index #5: KeyPair(publicKey=FFAV9LLhsau4A8p8uJRGXjr8QVtKhDBq36iBjEzsvPhq, privateKey=5KMXuDriTWDrfVAa5vQfu4SZe4mWRWDeSDK3zYA25KvfspnEedRm3fYDrPpTqcgQgX2gyg1V8aWG9c9YPkVqFnbh)
account index #6: KeyPair(publicKey=GkQE68eYSLinsRnGS5Wa9RJRdnEcBAhRfi1RrPCVSrsA, privateKey=3yneBTiTh3jLS4UDF9Va9AbL5tN1ra6ahBC1t2DveMj46oEgR8MEeWH6gqqhDKGV9nPUFsXxx2CcifeS1mNZ4FKe)
account index #7: KeyPair(publicKey=32X91GQjPoonkA6s9PJsfGmL7jkefn7Roh6KzL8y3gmD, privateKey=8kjFgNnwWsoW62hQMuEZB69j6f6yG4e3k3rETd5UoGbzcXqmQC4SEs2usd5gf59FEjd3i7cBPLNk8DZzGpV4W5V)
account index #8: KeyPair(publicKey=BwbdUjtUhq3LW7FTJnuJNEnKP5hJbmDUQBKWvXH46JGk, privateKey=581E1AofqxyVU4ngprMXm6uy3wGUDFa9voukcpwiMEc4UxXarDJQNdqnWrgrtSH8gNdL8WNJer2TvCsbQqKafuXJ)
account index #9: KeyPair(publicKey=EXwSPoG27EDce8uSkxgT8paJLRgVnnE2Jv1y59fRqtei, privateKey=3BMruGBGxSYhAufPUNY5x84vR32dT1KHGpB4XvPQtXdhN5wGc6k7QZMFKmGjQzHFhf7KCFr9dyDDFwxNKSJtGW9k)
account index #10: KeyPair(publicKey=3Hmo9rFvtSvyMYHExNtZMYvjcPEqthCZ7m2HyfJaRTb8, privateKey=2b8wzpzKrCh2jfn9QjGhuxHxD1qFEXWukrf86NVewX7ZQq2SZGHNCZ1csm2KmMNfM3yjxjZdr6jbMiqHCtXfNGjC)
account index #11: KeyPair(publicKey=9ZXENPsW9xfyC1i9YJt9tFhS38dNmHMHAnGyUwSVjKdk, privateKey=2EorgFX4k2r87zGw1EpzdeBx5JDSAdV7wjRxPMb7r1t4Y7ZABaVoJS4gJ56GxbRMRmBxiZn4px8LphJ3bja4HuCn)
account index #12: KeyPair(publicKey=iTXqeAwgspVQvtYy4BqaRUbkPTmpukuRenAeTjTAMzC, privateKey=2PMvd74EvsQX4moZXJh5348hUuKbNDMhGrLmmdXskmqcdXnK8tmybQ1pHSXDdNx3DU5pQFSHvDecyhKfeBnjQHoA)
account index #13: KeyPair(publicKey=GqBN7r8NGrrcD1wraP6PmdFZRWjjZ3zvM3Ve6MYsWxKb, privateKey=26KM9nHo9ayBTQNRTPqd38rwse5u91Rrf2eNE7TPSFav8bYfE34J45F7tCGpiu9J3m55UNs5iy2fwMjSUDuNaDdj)
account index #14: KeyPair(publicKey=2PyAomUpCwCVRnv8cyGRY66HcMcSbMyHzRbc6hhN87Fe, privateKey=5c8JcFt5UbJw6X2ug4J3gfUn7uyjqN6bDE6Ji6ns6RueLK4hdmqfox1TyujVAmwyhvyYiu7yve2sEvxc4ounQ4Mv)
account index #15: KeyPair(publicKey=8UU1myRjs5ukwTfB6DWdKimnS1zbeZ6Wmxgv43ogdGZ5, privateKey=42WVPGod3UpcDrkH8Vs2koAqwf95Par5h4odAX6dxBGNxaWN4x6N6U1nMjjgdgnx1ZsFDy4RrHzMPF5iwsnW5yKm)
account index #16: KeyPair(publicKey=BNW4y8YfQnoCMrxRx9fnV9hFDfFZQGpA47ne9XfPE94a, privateKey=5uW2TJB3HZrZTa7MZZSVoQFjBoAs7gzgSRFryHsrL2TH4odQXRnHW2Mib7XoYGJWMmTsd9A3UZWzrQvP2e37VmH8)
account index #17: KeyPair(publicKey=5zpsv566DSaT6VmTECY9zjQxsyV61VHxYZCtgsRE3Gse, privateKey=4CYzwiGULtwBgyta9Mj12AEuwjRsQdMzi9sKU3SkdMNHsLAEZa6tu6ARJhwDY2FkFiJ51fVGpr959HfcHuBwGH3A)
account index #18: KeyPair(publicKey=7mBXnqsehR4yRJigYK7nCxVwFJP54AxKV5x9shwhXhhf, privateKey=5gzzMyxoAuaqhmpV15snY6tCP8rkTRBRGvnxtwEkDF2cEoSnKL5RqAzxLtrpqqNznWU6NahECCEqwXsCqQEjqRZf)
account index #19: KeyPair(publicKey=CD21S9vCA6mq3g3YnEe2USdNHENh5ghkcQynBwubVxEK, privateKey=4ivPnv7Bg3W9m4g2wQFJyJbHKEaRpTNDTket8CnJENm5MnFcaZhGKkJxYjnbnW6C49Wzd3wSEWYcC8bucU8Xixab)
account index #20: KeyPair(publicKey=2SqxrWGSeErDUxktJCYB4e2W4PkvYjbuKxMUWLqVfdt8, privateKey=2TcpbX7pqt3ZTnQ4o3AQQSYzGLfMYJ2mWqCqYM3JXx2BQ275xHJ9BDekpBxb79q3Lm3gvbjQMvj7jDbHsHcP4g2g)
```

### Request airdrop of 1000000000 lamport on devnet:

```
~/ksol: ./ksol.sh rpc --cluster devnet requestAirdrop 9a3hDy7tsbMLUCM8ADfUvFYMGPzkMbJavrqF2mXRpMQZ 1000000000

4v8Kg7AhkbsxACqgs9EjwD6h97DNNhrACopUYkXnzyNJWBomN2jc8A7gbUFCx8aHMw7zVwVLJhpbL6oKAk3Qiz8V
```

### Get transaction info:

```
~/ksol:  ./ksol.sh rpc --cluster devnet getTransaction 4v8Kg7AhkbsxACqgs9EjwD6h97DNNhrACopUYkXnzyNJWBomN2jc8A7gbUFCx8aHMw7zVwVLJhpbL6oKAk3Qiz8V

Transaction(signatures=[4v8Kg7AhkbsxACqgs9EjwD6h97DNNhrACopUYkXnzyNJWBomN2jc8A7gbUFCx8aHMw7zVwVLJhpbL6oKAk3Qiz8V], message=TransactionMessage(header=TransactionHeader(numRequiredSignatures=1, numReadonlySignedAccounts=0, numReadonlyUnsignedAccounts=1), accountKeys=[TransactionAccountMetadata(publicKey=4ETf86tK7b4W72f27kNLJLgRWi9UfJjgH4koHGUXMFtn, isSigner=true, isWritable=true), TransactionAccountMetadata(publicKey=9a3hDy7tsbMLUCM8ADfUvFYMGPzkMbJavrqF2mXRpMQZ, isSigner=false, isWritable=true), TransactionAccountMetadata(publicKey=11111111111111111111111111111111, isSigner=false, isWritable=false)], recentBlockhash=F2ijUhMJkCnMuG7TzE3Xg4C9xhxvXQbxVQq3rkJRF6G2, instructions=[TransactionInstruction(programAccount=11111111111111111111111111111111, inputData=[2, 0, 0, 0, -24, 3, 0, 0, 0, 0, 0, 0], inputAccounts=[4ETf86tK7b4W72f27kNLJLgRWi9UfJjgH4koHGUXMFtn, 9a3hDy7tsbMLUCM8ADfUvFYMGPzkMbJavrqF2mXRpMQZ])]))
```

### Get Balance

```
~/ksol: ./ksol.sh rpc --cluster devnet  getBalance 9a3hDy7tsbMLUCM8ADfUvFYMGPzkMbJavrqF2mXRpMQZ

1000000000
```

### Send transaction

```
~/ksol: ./ksol.sh send --cluster devnet  4w6ayE5hLKt2hVKaJaE7fKGri75HBWdYxcX8kTwkpnX89M46NkzLXKd6yWCX6qkJLcABBJR52qDT9KzZ9x1Haf9o  4s9GDoCWBveJGiNEf1GruCfAmiwJpBoAVXtp7eMZTjDp  10000000

Using recent blockhash: 6hmhiBhxzE5AN8QESiMRxFeUQjDZQkP5JXpRF9ioxVFa
Sending from: 9a3hDy7tsbMLUCM8ADfUvFYMGPzkMbJavrqF2mXRpMQZ

Transaction signature: 5Pz5JaadgwFkRXmZ61oVbzLTzvuMX731LWa2m3GfutrdJ7vhxyGRrX1btoYj15YQXiuyzQD9LLCgCbEgWnZojW4R

Waiting for confirmation..
Waiting for confirmation..
Got signature confirmation: CONFIRMED

Transaction(signatures=[5Pz5JaadgwFkRXmZ61oVbzLTzvuMX731LWa2m3GfutrdJ7vhxyGRrX1btoYj15YQXiuyzQD9LLCgCbEgWnZojW4R], message=TransactionMessage(header=TransactionHeader(numRequiredSignatures=1, numReadonlySignedAccounts=0, numReadonlyUnsignedAccounts=1), accountKeys=[TransactionAccountMetadata(publicKey=9a3hDy7tsbMLUCM8ADfUvFYMGPzkMbJavrqF2mXRpMQZ, isSigner=true, isWritable=true), TransactionAccountMetadata(publicKey=4s9GDoCWBveJGiNEf1GruCfAmiwJpBoAVXtp7eMZTjDp, isSigner=false, isWritable=true), TransactionAccountMetadata(publicKey=11111111111111111111111111111111, isSigner=false, isWritable=false)], recentBlockhash=6hmhiBhxzE5AN8QESiMRxFeUQjDZQkP5JXpRF9ioxVFa, instructions=[TransactionInstruction(programAccount=11111111111111111111111111111111, inputData=[2, 0, 0, 0, -128, -106, -104, 0, 0, 0, 0, 0], inputAccounts=[9a3hDy7tsbMLUCM8ADfUvFYMGPzkMbJavrqF2mXRpMQZ, 4s9GDoCWBveJGiNEf1GruCfAmiwJpBoAVXtp7eMZTjDp])]))
```
