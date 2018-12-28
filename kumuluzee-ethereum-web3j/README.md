# KumuluzEE Ethereum with Web3j

> Develop and publish smart contract, send funds and interact with ethereum blochain from any client.

The object of of this sample is to demonstrate, how to get up and running with development on ethereum.


## Requirements

In order to run this example you will need the following:

1. Java 8 (or newer), you can use any implementation:
    * If you have installed Java, you can check the version by typing the following in a command line:
        
        ```
        java -version
        ```

2. Maven 3.2.1 (or newer):
    * If you have installed Maven, you can check the version by typing the following in a command line:
        
        ```
        mvn -version
        ```
3. Git:
    * If you have installed Git, you can check the version by typing the following in a command line:
    
        ```
        git --version
        ```
    

## Prerequisites

Sample can be run without additional setup. Ethereum client in the sample is connecting to rinkeby testnet and is using sample wallet. To customize settings edit config.yaml appropriately. Smart contracts written in solidity that you wish to use should be placed in resources folder.

## Usage

The example uses maven to build and run the microservices.

1. Build the sample using maven:

    ```bash
    $ cd kumuluzee-ethereum-web3j
    $ mvn clean package
    ```

2. Run the sample:
* Uber-jar:

    ```bash
    $ java -jar target/${project.build.finalName}.jar
    ```
    
    in Windows environemnt use the command
    ```batch
    java -jar target/${project.build.finalName}.jar
    ```

* Exploded:

    ```bash
    $ java -cp target/classes:target/dependency/* com.kumuluz.ee.EeApplication
    ```
    
    in Windows environment use the command
    ```batch
    java -cp target/classes;target/dependency/* com.kumuluz.ee.EeApplication
    ```
    
    
To test if everything is working correctly try following endpoints:
* To display the version of the ethereum client - http://localhost:8080/v1/execute/client/version
* To get balance on the smart contract - http://localhost:8080/v1/execute/contract/balance/{id}
* To send ether from your wallet to  http://localhost:8080/v1/execute/contract/send/ether/{address}
* To deploy smart contract http://localhost:8080/v1/execute/contract/deploy
* To get owner of contract http://localhost:8080/v1/execute/contract/get/owner
* To send your token to another address if your contract is erc20 token http://localhost:8080/v1/execute/contract/send/token/{to}
* To call method on smart contract with any name without parameters http://localhost:8080/v1/execute/contract/call/method/{name}
* To call method on smart contract with one parameter http://localhost:8080/v1/execute/contract/call/method/{name}/argument/{argument}

## Tutorial
This tutorial will guide you through the steps required to create a simple token on top of the ethereum blockchain.

We will follow these steps:
* Create a wallet
* Add Maven dependencies
* Get access to ethereum client
* Interacting with smart contracts
* Build the microservice
* Run it

### Create a wallet
Most popular options include:
* [MyEtherWallet (Online)](https://www.myetherwallet.com)
* [MetaMask](https://metamask.io)
* [Mist (Desktop)](https://github.com/ethereum/mist/releases)
* [Parity (Desktop)](https://ethcore.io/parity.html)

Path to the wallet should be supplied in config.yaml. 
### Add Maven dependencies

Add the `kumuluzee-ethereum-web3j` dependency, if you haven't used the sample:
```xml
 <dependency>
    <groupId>com.kumuluz.ee.ethereum</groupId>
    <artifactId>kumuluzee-ethereum-web3j</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

`web3j-maven-plugin` is used for generating smart contract wrappers and should already be added to your project from sample. Otherwise you can include it by adding following section to your pom.xml.
```xml
    <plugin>
        <groupId>org.web3j</groupId>
        <artifactId>web3j-maven-plugin</artifactId>
        <version>0.1.4</version>
        <configuration>
            <soliditySourceFiles/>
            <packageName>contracts</packageName>
        </configuration>
        <executions>
            <execution>
                <id>package</id>
                <goals>
                    <goal>generate-sources</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
```
### Get access to ethereum client

To connect to ethereum network you need to use the client. You can client yourself (geth, parity) or use one provided for you in the cloud such as [Infura](https://infura.io/signup).
For testing purposes we recommend client to connect to testnet such as rinkeby (geth client only). To get free ether use [rinkeby faucet](https://faucet.rinkeby.io/)

### Generating smart contract wrappers
Put your solidity files in resources folder in maven project. Then run following lifecycle:
```bash
$ mvn web3j:generate-sources
```
and java wrapper classes should be generated in /src/main/java/contracts folder. Then you can interact with autogenerated classes that represent smart contract methods in your project.
### Interacting with smart contracts
We are going to build [Rest](https://github.com/kumuluz/kumuluzee-samples/tree/master/jax-rs) interface using JAX-RS to demonstrate operations on smart contracts. 

To get version of the client used to connect to ethereum network we can use following endpoint.
```java
@GET
    @Path("client/version")
    public String getClientVerstion() {
        try {
            Web3ClientVersion web3ClientVersion = web3j.web3ClientVersion().send();
            String clientVersion = web3ClientVersion.getWeb3ClientVersion();
            return clientVersion;
        } catch (Exception e) {
            String error = e.getMessage();
            log.severe(error);
            return error;
        }
    }
```
To get balance of the account with account address (id) we can use web3j.ethGetBalance method supplying address of the receiving account.
```java
@GET
    @Path("contract/balance/{id}")
    public String getBalance(@PathParam("id") String accountAddress) {
        try {
            BigInteger wei = web3j.ethGetBalance(accountAddress, DefaultBlockParameterName.LATEST).sendAsync().get().getBalance();
            return "You have " + wei + " wei.";
        } catch (Exception e) {
            String error = e.getMessage();
            log.severe(error);
            return error;
        }
    }
```

To send ether (currency on ethereum network) from your account somewhere, we can use folowing endpoint supplying web3j instance, credentials object containing data to access wallet, receiving address and amount we wish to transfer. 
>Due to mechanism with which ethereum processes transactions your speed might vary.
```java
@GET
    @Path("contract/send/ether/{address}")
    public String sendEther(@PathParam("address") String receivingAddress) {
        try {
            TransactionReceipt transactionReceipt = Transfer.sendFunds(web3j, credentials, receivingAddress, BigDecimal.valueOf(0.01), Convert.Unit.ETHER).send();
            return transactionReceipt.getTransactionHash().toString();
        } catch (Exception e) {
            String error = e.getMessage();
            log.severe(error);
            return error;
        }
    }
```

In order to deploy smart contract written call deploy() method on autogenerated smart contract wrapper class, supplying web3j instance, credentials object and GAS_PRICE which is the cost per instruction and GAS_LIMIT which specifies maximum amount of gas transaction can consume.
```java
@GET
    @Path("contract/deploy")
    public String deployContract () {
        try {
            SampleToken sampleToken = SampleToken.deploy(web3j, credentials, Contract.GAS_PRICE, Contract.GAS_LIMIT).send();
            String contractAddress = sampleToken.getContractAddress();
            return "Contract is deployed at address: " + contractAddress;
        } catch (Exception e) {
            String error = "Deploynment failure";;
            log.severe(error);
            return error;
        }
    }
```

In order to call methods smart contract offers, we need to specify where they are located on the network. The example demostrates calling the method owner() to get address of smart contract owner.  
```java
@GET
    @Path("contract/get/owner")
    public String callContract () {
        SampleToken sampleToken = SampleToken.load(deployedContractAddress, web3j, credentials, Contract.GAS_PRICE, Contract.GAS_LIMIT);
        try {
            String address = sampleToken.owner().send();
            String owner = String.format("Owner is address %s\n", address);
            return owner;
        } catch (Exception e) {
            String error = "Error calling method";;
            log.severe(error);
            return error;
        }
    }
```

If your smart contract is a ERC20 token, you can send it using transfer method.
```java
@GET
    @Path("contract/send/token/{to}")
    public String callContract (@PathParam("to") String transaction) {
        SampleToken sampleToken = SampleToken.load(deployedContractAddress, web3j, credentials, Contract.GAS_PRICE, Contract.GAS_LIMIT);
        try {
            String logs = sampleToken.transfer(transaction, BigInteger.valueOf(100)).send().getLogsBloom();
            return logs;
        } catch (Exception e) {
            String error = "Error calling method";;
            log.severe(error);
            return error;
        }
    }
```  

To send token to all your customers stored in database use. To check if transfer was successful transaction hash can be checked using [etherscan](https://rinkeby.etherscan.io/).
```java
@GET
    @Path("contract/send/tokens/customers")
    public String sendTokenToCustomers () {
        SampleToken sampleToken = SampleToken.load(deployedContractAddress, web3j, credentials, Contract.GAS_PRICE, Contract.GAS_LIMIT);
        try {
            List<Customer> customerList = customerService.getCustomers();
            String logs = "";
            for (Customer customer : customerList) {
                BigInteger amount = BigInteger.valueOf(100);
                logs += amount + " of SampleToken transfered in transation: " + sampleToken.transfer(customer.getWalletAddress(), amount).send().getTransactionHash() + "\n";
            }
            return logs;
        } catch (Exception e) {
            String error = "Error calling method";;
            log.severe(error);
            return error;
        }
    }
```

If you run your own Ethereum node (Infura is not supported) you can listen to smart contract events and react to them programatically inside methods annotated with EventListen annotation. To get data about the event pass appropriate EventResponse object.
```java
@EventListen(eventName="transfer", smartContractName = SampleToken.class, smartContractAddress = deployedContractAddress)
    public void reactToEvent (SampleToken.TransferEventResponse transferEventResponse) {
        if (transferEventResponse.tokens.compareTo(BigInteger.valueOf(20)) == 1) {
            log.info("Granting service access to user " + transferEventResponse.from + ". " +
                    transferEventResponse.tokens + " tokens received.");
        } else {
            log.info("Access denied. User " + transferEventResponse.from + " has send only " + transferEventResponse.tokens + " tokens.");
        }
    }
```

In order to get your wallet credentials use Web3jUtils
```java
private Credentials credentials = Web3jUtils.getCredentials();
```
To load smart contract with autogenerated wrapper class named SampleToken
```java
SampleToken sampleToken = SampleToken.load(deployedContractAddress, web3j, credentials, Contract.GAS_PRICE, Contract.GAS_LIMIT);
```
Then you call call methods on the instance of that object.
### Build the microservice and run it

To build the microservice and run the example, use the commands as described in previous sections.
