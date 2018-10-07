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

import com.kumuluz.ee.ethereum.annotations.Web3jUtil;
import com.kumuluz.ee.ethereum.utils.Web3jUtils;
import com.kumuluz.ee.samples.ethereum.entities.Customer;
import com.kumuluz.ee.samples.ethereum.services.CustomerService;
import contracts.SampleToken;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.tx.Contract;
import org.web3j.tx.Transfer;
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

    private String deployedContractAddress = "0x7f45B345fB76D47770af9C4eF36514eD7f713a33"; // Smart contract address of SampleToken

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

    @GET
    @Path("contract/send/token/{to}")
    public String sendToken (@PathParam("to") String transaction) {
        SampleToken sampleToken = SampleToken.load(deployedContractAddress, web3j, credentials, Contract.GAS_PRICE, Contract.GAS_LIMIT);
        try {
            String logs = "Transaction is at: " + sampleToken.transfer(transaction, BigInteger.valueOf(100)).send().getTransactionHash();
            return logs;
        } catch (Exception e) {
            String error = "Error calling method";;
            log.severe(error);
            return error;
        }
    }

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

    @GET
    @Path("contract/call/method/{name}")
    public String callContractMethod (@PathParam("name") String methodName) {
        SampleToken sampleToken = SampleToken.load(deployedContractAddress, web3j, credentials, Contract.GAS_PRICE, Contract.GAS_LIMIT);
        try {
            String logs = runMethod(sampleToken, methodName).send().toString();
            return logs;
        } catch (Exception e) {
            String error = "Error calling method";;
            log.severe(error);
            return error;
        }
    }

    @GET
    @Path("contract/call/method/{name}/argument/{argument}")
    public String callContractMethod (@PathParam("name") String methodName, @PathParam("argument") String argo) {
        SampleToken sampleToken = SampleToken.load(deployedContractAddress, web3j, credentials, Contract.GAS_PRICE, Contract.GAS_LIMIT);
        try {
            String logs = runMethod(sampleToken, methodName, argo, argo.getClass()).send().toString();
            return methodName + "(" + argo + ") is: " + logs;
        } catch (Exception e) {
            String error = "Error calling method";;
            log.severe(error);
            return error;
        }
    }

    private static RemoteCall runMethod(Object instance, String methodName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = instance.getClass().getMethod(methodName);
        return (RemoteCall)method.invoke(instance);
    }

    private static RemoteCall runMethod(Object instance, String methodName, String argo, Class<?> parameterType) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = instance.getClass().getMethod(methodName, parameterType);
        return (RemoteCall)method.invoke(instance, argo);
    }
}

