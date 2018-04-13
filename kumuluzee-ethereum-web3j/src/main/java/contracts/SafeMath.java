package contracts;

import java.math.BigInteger;
import java.util.Arrays;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.0.2.
 */
public final class SafeMath extends Contract {
    private static final String BINARY = "6060604052341561000f57600080fd5b6101798061001e6000396000f3006060604052600436106100615763ffffffff7c0100000000000000000000000000000000000000000000000000000000600035041663a293d1e88114610066578063b5931f7c14610091578063d05c78da146100aa578063e6cb9013146100c3575b600080fd5b341561007157600080fd5b61007f6004356024356100dc565b60405190815260200160405180910390f35b341561009c57600080fd5b61007f6004356024356100f1565b34156100b557600080fd5b61007f600435602435610112565b34156100ce57600080fd5b61007f60043560243561013d565b6000828211156100eb57600080fd5b50900390565b60008082116100ff57600080fd5b818381151561010a57fe5b049392505050565b81810282158061012c575081838281151561012957fe5b04145b151561013757600080fd5b92915050565b8181018281101561013757600080fd00a165627a7a723058201502e62a06282dfe3aac1952c0b9ae761a0ef9039ae8d112ec6d6d2e71a832060029";

    private SafeMath(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    private SafeMath(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<BigInteger> safeSub(BigInteger a, BigInteger b) {
        Function function = new Function("safeSub", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(a), 
                new org.web3j.abi.datatypes.generated.Uint256(b)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> safeDiv(BigInteger a, BigInteger b) {
        Function function = new Function("safeDiv", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(a), 
                new org.web3j.abi.datatypes.generated.Uint256(b)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> safeMul(BigInteger a, BigInteger b) {
        Function function = new Function("safeMul", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(a), 
                new org.web3j.abi.datatypes.generated.Uint256(b)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> safeAdd(BigInteger a, BigInteger b) {
        Function function = new Function("safeAdd", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(a), 
                new org.web3j.abi.datatypes.generated.Uint256(b)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public static RemoteCall<SafeMath> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(SafeMath.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<SafeMath> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(SafeMath.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static SafeMath load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new SafeMath(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static SafeMath load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new SafeMath(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }
}
