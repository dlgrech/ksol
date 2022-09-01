
<h1 align="center">Common Commands</h1>

#### Generate a seed phrase:

```
~/ksol: ./ksol.sh keygen mnemonic

[notice, day, enhance, use, engage, disagree, above, caught, walnut, fury, awake, gesture, welcome, hurdle, critic, toward, cost, trick, forum, differ, develop, easy, hobby, project]
```

#### Generate a public/private key pair from mnemonic

```
~/ksol: ./ksol.sh keygen seed notice day enhance use engage disagree above caught walnut fury awake gesture welcome hurdle critic toward cost trick forum differ develop easy hobby project

KeyPair(
  publicKey=4eUAao5VfPbR7mcytqRuLeZNQNJFTo8Tmf56joZRt6sd, 
  privateKey=5gBjQfeUyr16zuSx2DvFfwjosWK6pd6Y1TzCCaBZsDTLtphawQirbVPRMSSyWCzRAvXDfmrtV6gJHB6jrGfz4fk7
)
```

#### Request airdrop:

```
~/ksol: ./ksol.sh rpc --cluster devnet requestAirdrop 9a3hDy7tsbMLUCM8ADfUvFYMGPzkMbJavrqF2mXRpMQZ 1000000000

4v8Kg7AhkbsxACqgs9EjwD6h97DNNhrACopUYkXnzyNJWBomN2jc8A7gbUFCx8aHMw7zVwVLJhpbL6oKAk3Qiz8V
```

#### Get Balance

```
~/ksol: ./ksol.sh rpc --cluster devnet  getBalance 9a3hDy7tsbMLUCM8ADfUvFYMGPzkMbJavrqF2mXRpMQZ

1000000000
```

#### Generate account keypairs from a seed phrase:

```
# Using seed phrase from above
~/ksol: ./ksol.sh keygen accounts notice day enhance use engage disagree above caught walnut fury awake gesture welcome hurdle critic toward cost trick forum differ develop easy hobby project

account index #0:
  KeyPair(
    publicKey=9a3hDy7tsbMLUCM8ADfUvFYMGPzkMbJavrqF2mXRpMQZ, 
    privateKey=4w6ayE5hLKt2hVKaJaE7fKGri75HBWdYxcX8kTwkpnX89M46NkzLXKd6yWCX6qkJLcABBJR52qDT9KzZ9x1Haf9o
  )
account index #1: 
  KeyPair(
    publicKey=4s9GDoCWBveJGiNEf1GruCfAmiwJpBoAVXtp7eMZTjDp, 
    privateKey=3KQ7ycafpPq54on2VQGe8yGK5rFEUtVZJd2Er1PcN2EewynWjpHHgMw3VgKEfaW43VQZMGiQTugiBrhH5EECwiiJ
  )
account index #2: 
  KeyPair(
    publicKey=8f7pwijXKp5PZsqLhrQDFsMjQAQW1GoPdx7NCwY8VNii, 
    privateKey=4KwSg2SWjQNfD7RGYUnhQQ1sLmVSpPcYzhEwREHFPqJEeJcrjY33w5QKKjnVdJx9ZdfHecWVt1HsuzQZr8FAxNVc
  )
account index #3: 
  KeyPair(
    publicKey=kL61cPBRBUhyFtqXAADBQodLwuSVc87kw5AqEkhs1wE, 
    privateKey=5g27nUg1Xec7tT2ybY6HcshB8Ezx4oFtrPYHC6RPqtG7vpy8Bbe13uoVV6FBhjFTkfWQVhfMQWYEgAJqk1NmW1kL
  )
account index #4: 
  KeyPair(
    publicKey=E3XkzD5VtHGmEW3NHm4kyBUoQ1VUNq3URKMnt2ELTk5m, 
    privateKey=67k3ax8XBPdapWmMpHa6YCy6po8pPy5FoWSrPks5gDgpY8XHf98v6Rv34d7prsgsnHTMuBoazK5DDWfGwDBETBtj
  )
account index #5: 
  KeyPair(
    publicKey=FFAV9LLhsau4A8p8uJRGXjr8QVtKhDBq36iBjEzsvPhq, 
    privateKey=5KMXuDriTWDrfVAa5vQfu4SZe4mWRWDeSDK3zYA25KvfspnEedRm3fYDrPpTqcgQgX2gyg1V8aWG9c9YPkVqFnbh
  )
```

#### Get transaction info:

```
~/ksol:  ./ksol.sh rpc --cluster devnet getTransaction 4v8Kg7AhkbsxACqgs9EjwD6h97DNNhrACopUYkXnzyNJWBomN2jc8A7gbUFCx8aHMw7zVwVLJhpbL6oKAk3Qiz8V

Transaction(
  signatures=[4v8Kg7AhkbsxACqgs9EjwD6h97DNNhrACopUYkXnzyNJWBomN2jc8A7gbUFCx8aHMw7zVwVLJhpbL6oKAk3Qiz8V],   
  message=TransactionMessage(
    header=TransactionHeader(numRequiredSignatures=1, numReadonlySignedAccounts=0, numReadonlyUnsignedAccounts=1), 
    recentBlockhash=F2ijUhMJkCnMuG7TzE3Xg4C9xhxvXQbxVQq3rkJRF6G2,
    accountKeys=[
      TransactionAccountMetadata(publicKey=4ETf86tK7b4W72f27kNLJLgRWi9UfJjgH4koHGUXMFtn, isSigner=true, isWritable=true), 
      TransactionAccountMetadata(publicKey=9a3hDy7tsbMLUCM8ADfUvFYMGPzkMbJavrqF2mXRpMQZ, isSigner=false, isWritable=true), 
      TransactionAccountMetadata(publicKey=11111111111111111111111111111111, isSigner=false, isWritable=false)
    ],  
    instructions=[
      TransactionInstruction(
        programAccount=11111111111111111111111111111111, 
        inputData=[2, 0, 0, 0, -24, 3, 0, 0, 0, 0, 0, 0], 
        inputAccounts=[4ETf86tK7b4W72f27kNLJLgRWi9UfJjgH4koHGUXMFtn, 9a3hDy7tsbMLUCM8ADfUvFYMGPzkMbJavrqF2mXRpMQZ]
      )
    ]
  )
)
```

#### Send transaction

```
~/ksol: ./ksol.sh send --cluster devnet  4w6ayE5hLKt2hVKaJaE7fKGri75HBWdYxcX8kTwkpnX89M46NkzLXKd6yWCX6qkJLcABBJR52qDT9KzZ9x1Haf9o  4s9GDoCWBveJGiNEf1GruCfAmiwJpBoAVXtp7eMZTjDp  10000000

Using recent blockhash: 6hmhiBhxzE5AN8QESiMRxFeUQjDZQkP5JXpRF9ioxVFa
Sending from: 9a3hDy7tsbMLUCM8ADfUvFYMGPzkMbJavrqF2mXRpMQZ

Transaction signature: 5Pz5JaadgwFkRXmZ61oVbzLTzvuMX731LWa2m3GfutrdJ7vhxyGRrX1btoYj15YQXiuyzQD9LLCgCbEgWnZojW4R

Waiting for confirmation..
Waiting for confirmation..

Got signature confirmation: CONFIRMED

Transaction(
  signatures=[5Pz5JaadgwFkRXmZ61oVbzLTzvuMX731LWa2m3GfutrdJ7vhxyGRrX1btoYj15YQXiuyzQD9LLCgCbEgWnZojW4R], 
  message=TransactionMessage(
    header=TransactionHeader(numRequiredSignatures=1, numReadonlySignedAccounts=0, numReadonlyUnsignedAccounts=1), 
    recentBlockhash=6hmhiBhxzE5AN8QESiMRxFeUQjDZQkP5JXpRF9ioxVFa,
    accountKeys=[
      TransactionAccountMetadata(publicKey=9a3hDy7tsbMLUCM8ADfUvFYMGPzkMbJavrqF2mXRpMQZ, isSigner=true, isWritable=true), 
      TransactionAccountMetadata(publicKey=4s9GDoCWBveJGiNEf1GruCfAmiwJpBoAVXtp7eMZTjDp, isSigner=false, isWritable=true), 
      TransactionAccountMetadata(publicKey=11111111111111111111111111111111, isSigner=false, isWritable=false)
    ], 
    instructions=[
      TransactionInstruction(
        programAccount=11111111111111111111111111111111, 
        inputData=[2, 0, 0, 0, -128, -106, -104, 0, 0, 0, 0, 0], 
        inputAccounts=[9a3hDy7tsbMLUCM8ADfUvFYMGPzkMbJavrqF2mXRpMQZ, 4s9GDoCWBveJGiNEf1GruCfAmiwJpBoAVXtp7eMZTjDp]
      )
    ]
  )
)
```
