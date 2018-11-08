/*
 *  Copyright (c) 2014-2018 Kumuluz and/or its affiliates
 *  and other contributors as indicated by the @author tags and
 *  the contributor list.
 *
 *  Licensed under the MIT License (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  https://opensource.org/licenses/MIT
 *
 *  The software is provided "AS IS", WITHOUT WARRANTY OF ANY KIND, express or
 *  implied, including but not limited to the warranties of merchantability,
 *  fitness for a particular purpose and noninfringement. in no event shall the
 *  authors or copyright holders be liable for any claim, damages or other
 *  liability, whether in an action of contract, tort or otherwise, arising from,
 *  out of or in connection with the software or the use or other dealings in the
 *  software. See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.kumuluz.ee.samples.ethereum;

import com.kumuluz.ee.ethereum.annotations.EventListen;
import com.kumuluz.ee.ethereum.annotations.Web3jUtil;
import com.kumuluz.ee.ethereum.utils.Web3jUtils;
import com.kumuluz.ee.samples.ethereum.entities.Customer;
import com.kumuluz.ee.samples.ethereum.services.CustomerService;
import contracts.SampleToken;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.tx.Transfer;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Domen Gašperlin
 * @since 1.0.0
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("execute")
@ApplicationScoped
public class EventsEndpoint {

    private Credentials credentials = Web3jUtils.getCredentials();

    private final String deployedContractAddress = "0x7f45B345fB76D47770af9C4eF36514eD7f713a33"; // Smart contract address of SampleToken

    private Logger log = Logger.getLogger(EventsEndpoint.class.getName());

    @Inject
    CustomerService customerService; // CDI Bean to access data from database

    @Inject
    @Web3jUtil
    private Web3j web3j;


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

    @GET
    @Path("contract/send/ether/{address}")
    public String sendEther(@PathParam("address") String receivingAddress) {
        try {
            TransactionReceipt transactionReceipt = Transfer.sendFunds(web3j, credentials, receivingAddress, BigDecimal.valueOf(0.01), Convert.Unit.ETHER).send();
            return "Transaction is at: " + transactionReceipt.getTransactionHash();
        } catch (Exception e) {
            String error = e.getMessage();
            log.severe(error);
            return error;
        }
    }

    @GET
    @Path("contract/deploy")
    public String deployContract() {
        try {
            ContractGasProvider contractGasProvider = new DefaultGasProvider();
            SampleToken sampleToken = SampleToken.deploy(web3j, credentials, contractGasProvider).send();
            String contractAddress = sampleToken.getContractAddress();
            return "Contract is deployed at address: " + contractAddress;
        } catch (Exception e) {
            String error = "Deploynment failure " + e.getMessage();
            log.severe(error);
            return error;
        }
    }

    @EventListen(eventName = "transfer", smartContractName = SampleToken.class, smartContractAddress = deployedContractAddress)
    public void reactToEvent(SampleToken.TransferEventResponse transferEventResponse) {
        if (transferEventResponse.tokens.compareTo(BigInteger.valueOf(20)) == 1) {
            log.info("Granting service access to user " + transferEventResponse.from + ". " +
                    transferEventResponse.tokens + " tokens received.");
        } else {
            log.info("Access denied. User " + transferEventResponse.from + " has send only " + transferEventResponse.tokens + " tokens.");
        }
    }

    @GET
    @Path("contract/get/owner")
    public String callContract() {
        ContractGasProvider contractGasProvider = new DefaultGasProvider();
        SampleToken sampleToken = SampleToken.load(deployedContractAddress, web3j, credentials, contractGasProvider);
        try {
            String address = sampleToken.owner().send();
            String owner = String.format("Owner is address %s\n", address);
            return owner;
        } catch (Exception e) {
            String error = "Error calling method " + e.getMessage();
            log.severe(error);
            return error;
        }
    }

    @GET
    @Path("contract/send/token/{to}/amount/{amount}")
    public String sendToken(@PathParam("to") String transaction, @PathParam("amount") String amount) {
        ContractGasProvider contractGasProvider = new DefaultGasProvider();
        SampleToken sampleToken = SampleToken.load(deployedContractAddress, web3j, credentials, contractGasProvider);
        try {
            String logs = "Transaction is at: " + sampleToken.transfer(transaction, BigInteger.valueOf(Integer.valueOf(amount))).send().getTransactionHash();
            return logs;
        } catch (Exception e) {
            String error = "Error calling method " + e.getMessage();
            log.severe(error);
            return error;
        }
    }

    @GET
    @Path("contract/send/tokens/customers")
    public String sendTokenToCustomers() {
        ContractGasProvider contractGasProvider = new DefaultGasProvider();
        SampleToken sampleToken = SampleToken.load(deployedContractAddress, web3j, credentials, contractGasProvider);
        try {
            List<Customer> customerList = customerService.getCustomers();
            String logs = "";
            for (Customer customer : customerList) {
                BigInteger amount = BigInteger.valueOf(100);
                logs += amount + " of SampleToken transfered in transation: " + sampleToken.transfer(customer.getWalletAddress(), amount).send().getTransactionHash() + "\n";
            }
            return logs;
        } catch (Exception e) {
            String error = "Error calling method " + e.getMessage();
            log.severe(error);
            return error;
        }
    }

    @GET
    @Path("contract/call/method/{name}")
    public String callContractMethod(@PathParam("name") String methodName) {
        ContractGasProvider contractGasProvider = new DefaultGasProvider();
        SampleToken sampleToken = SampleToken.load(deployedContractAddress, web3j, credentials, contractGasProvider);
        try {
            String logs = runMethod(sampleToken, methodName).send().toString();
            return logs;
        } catch (Exception e) {
            String error = "Error calling method " + e.getMessage();
            log.severe(error);
            return error;
        }
    }

    @GET
    @Path("contract/call/method/{name}/argument/{argument}")
    public String callContractMethod(@PathParam("name") String methodName, @PathParam("argument") String argo) {
        ContractGasProvider contractGasProvider = new DefaultGasProvider();
        SampleToken sampleToken = SampleToken.load(deployedContractAddress, web3j, credentials, contractGasProvider);
        try {
            String logs = runMethod(sampleToken, methodName, argo, argo.getClass()).send().toString();
            return methodName + "(" + argo + ") is: " + logs;
        } catch (Exception e) {
            String error = "Error calling method " + e.getMessage();
            log.severe(error);
            return error;
        }
    }

    private static RemoteCall runMethod(Object instance, String methodName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = instance.getClass().getMethod(methodName);
        return (RemoteCall) method.invoke(instance);
    }

    private static RemoteCall runMethod(Object instance, String methodName, String argo, Class<?> parameterType) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = instance.getClass().getMethod(methodName, parameterType);
        return (RemoteCall) method.invoke(instance, argo);
    }
}

