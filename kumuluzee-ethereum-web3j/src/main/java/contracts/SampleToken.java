package contracts;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.0.2.
 */
public final class SampleToken extends Contract {
    private static final String BINARY = "6060604052341561000f57600080fd5b60008054600160a060020a03191633600160a060020a031617905560408051908101604052600681527f53414d504c45000000000000000000000000000000000000000000000000000060208201526002908051610071929160200190610155565b5060408051908101604052600c81527f53414d504c4520546f6b656e0000000000000000000000000000000000000000602082015260039080516100b9929160200190610155565b506004805460ff191660121790556a52b7d2dcc80cd2e4000000600581905573ebe82f9ac91697e8a81f4f3a30fcaf499ef65993600081815260066020527f966864add5e37a4945ada8be0ed27fc6565af0ac8ccfdee895252bb349366d7b83905590917fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9060405190815260200160405180910390a36101f0565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061019657805160ff19168380011785556101c3565b828001600101855582156101c3579182015b828111156101c35782518255916020019190600101906101a8565b506101cf9291506101d3565b5090565b6101ed91905b808211156101cf57600081556001016101d9565b90565b610b68806101ff6000396000f3006060604052600436106100f85763ffffffff60e060020a60003504166306fdde0381146100fd578063095ea7b31461018757806318160ddd146101bd57806323b872dd146101e2578063313ce5671461020a5780633eaaf86b1461023357806370a082311461024657806379ba5097146102655780638da5cb5b1461027a57806395d89b41146102a9578063a293d1e8146102bc578063a9059cbb146102d5578063b5931f7c146102f7578063cae9ca5114610310578063d05c78da14610375578063d4ee1d901461038e578063dc39d06d146103a1578063dd62ed3e146103c3578063e6cb9013146103e8578063f2fde38b14610401575b600080fd5b341561010857600080fd5b610110610420565b60405160208082528190810183818151815260200191508051906020019080838360005b8381101561014c578082015183820152602001610134565b50505050905090810190601f1680156101795780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b341561019257600080fd5b6101a9600160a060020a03600435166024356104be565b604051901515815260200160405180910390f35b34156101c857600080fd5b6101d061052b565b60405190815260200160405180910390f35b34156101ed57600080fd5b6101a9600160a060020a036004358116906024351660443561055d565b341561021557600080fd5b61021d61065e565b60405160ff909116815260200160405180910390f35b341561023e57600080fd5b6101d0610667565b341561025157600080fd5b6101d0600160a060020a036004351661066d565b341561027057600080fd5b610278610688565b005b341561028557600080fd5b61028d610716565b604051600160a060020a03909116815260200160405180910390f35b34156102b457600080fd5b610110610725565b34156102c757600080fd5b6101d0600435602435610790565b34156102e057600080fd5b6101a9600160a060020a03600435166024356107a5565b341561030257600080fd5b6101d0600435602435610858565b341561031b57600080fd5b6101a960048035600160a060020a03169060248035919060649060443590810190830135806020601f8201819004810201604051908101604052818152929190602084018383808284375094965061087995505050505050565b341561038057600080fd5b6101d06004356024356109e0565b341561039957600080fd5b61028d610a05565b34156103ac57600080fd5b6101a9600160a060020a0360043516602435610a14565b34156103ce57600080fd5b6101d0600160a060020a0360043581169060243516610ab7565b34156103f357600080fd5b6101d0600435602435610ae2565b341561040c57600080fd5b610278600160a060020a0360043516610af2565b60038054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156104b65780601f1061048b576101008083540402835291602001916104b6565b820191906000526020600020905b81548152906001019060200180831161049957829003601f168201915b505050505081565b600160a060020a03338116600081815260076020908152604080832094871680845294909152808220859055909291907f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b9259085905190815260200160405180910390a35060015b92915050565b6000805260066020527f54cdd369e4e8a8515e52ca72ec816c2101831ad1f18bf44102ed171459c9b4f8546005540390565b600160a060020a0383166000908152600660205260408120546105809083610790565b600160a060020a03808616600090815260066020908152604080832094909455600781528382203390931682529190915220546105bd9083610790565b600160a060020a03808616600090815260076020908152604080832033851684528252808320949094559186168152600690915220546105fd9083610ae2565b600160a060020a03808516600081815260066020526040908190209390935591908616907fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9085905190815260200160405180910390a35060019392505050565b60045460ff1681565b60055481565b600160a060020a031660009081526006602052604090205490565b60015433600160a060020a039081169116146106a357600080fd5b600154600054600160a060020a0391821691167f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e060405160405180910390a3600180546000805473ffffffffffffffffffffffffffffffffffffffff19908116600160a060020a03841617909155169055565b600054600160a060020a031681565b60028054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156104b65780601f1061048b576101008083540402835291602001916104b6565b60008282111561079f57600080fd5b50900390565b600160a060020a0333166000908152600660205260408120546107c89083610790565b600160a060020a0333811660009081526006602052604080822093909355908516815220546107f79083610ae2565b600160a060020a0380851660008181526006602052604090819020939093559133909116907fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef9085905190815260200160405180910390a350600192915050565b600080821161086657600080fd5b818381151561087157fe5b049392505050565b600160a060020a03338116600081815260076020908152604080832094881680845294909152808220869055909291907f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b9259086905190815260200160405180910390a383600160a060020a0316638f4ffcb1338530866040518563ffffffff1660e060020a0281526004018085600160a060020a0316600160a060020a0316815260200184815260200183600160a060020a0316600160a060020a0316815260200180602001828103825283818151815260200191508051906020019080838360005b8381101561097457808201518382015260200161095c565b50505050905090810190601f1680156109a15780820380516001836020036101000a031916815260200191505b5095505050505050600060405180830381600087803b15156109c257600080fd5b6102c65a03f115156109d357600080fd5b5060019695505050505050565b8181028215806109fa57508183828115156109f757fe5b04145b151561052557600080fd5b600154600160a060020a031681565b6000805433600160a060020a03908116911614610a3057600080fd5b60008054600160a060020a038086169263a9059cbb929091169085906040516020015260405160e060020a63ffffffff8516028152600160a060020a0390921660048301526024820152604401602060405180830381600087803b1515610a9657600080fd5b6102c65a03f11515610aa757600080fd5b5050506040518051949350505050565b600160a060020a03918216600090815260076020908152604080832093909416825291909152205490565b8181018281101561052557600080fd5b60005433600160a060020a03908116911614610b0d57600080fd5b6001805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a03929092169190911790555600a165627a7a723058203686957ee5a9784cfe9adaff4646ce04ab553a90e13bfbe630ebd5360c8a4ab40029";

    private SampleToken(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    private SampleToken(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public List<OwnershipTransferredEventResponse> getOwnershipTransferredEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("OwnershipTransferred", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList());
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<OwnershipTransferredEventResponse> responses = new ArrayList<OwnershipTransferredEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
            typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse._to = (String) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<OwnershipTransferredEventResponse> ownershipTransferredEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("OwnershipTransferred", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList());
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, OwnershipTransferredEventResponse>() {
            @Override
            public OwnershipTransferredEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                OwnershipTransferredEventResponse typedResponse = new OwnershipTransferredEventResponse();
                typedResponse._from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse._to = (String) eventValues.getIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public List<TransferEventResponse> getTransferEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Transfer", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<TransferEventResponse> responses = new ArrayList<TransferEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            TransferEventResponse typedResponse = new TransferEventResponse();
            typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.tokens = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<TransferEventResponse> transferEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Transfer", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, TransferEventResponse>() {
            @Override
            public TransferEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                TransferEventResponse typedResponse = new TransferEventResponse();
                typedResponse.from = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.to = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.tokens = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public List<ApprovalEventResponse> getApprovalEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("Approval", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<ApprovalEventResponse> responses = new ArrayList<ApprovalEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            ApprovalEventResponse typedResponse = new ApprovalEventResponse();
            typedResponse.tokenOwner = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.tokens = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<ApprovalEventResponse> approvalEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("Approval", 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, ApprovalEventResponse>() {
            @Override
            public ApprovalEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                ApprovalEventResponse typedResponse = new ApprovalEventResponse();
                typedResponse.tokenOwner = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.spender = (String) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.tokens = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public RemoteCall<String> name() {
        Function function = new Function("name", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> approve(String spender, BigInteger tokens) {
        Function function = new Function(
                "approve", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(spender), 
                new org.web3j.abi.datatypes.generated.Uint256(tokens)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> totalSupply() {
        Function function = new Function("totalSupply", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> transferFrom(String from, String to, BigInteger tokens) {
        Function function = new Function(
                "transferFrom", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(from), 
                new org.web3j.abi.datatypes.Address(to), 
                new org.web3j.abi.datatypes.generated.Uint256(tokens)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> decimals() {
        Function function = new Function("decimals", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint8>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> _totalSupply() {
        Function function = new Function("_totalSupply", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<BigInteger> balanceOf(String tokenOwner) {
        Function function = new Function("balanceOf", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(tokenOwner)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> acceptOwnership() {
        Function function = new Function(
                "acceptOwnership", 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<String> owner() {
        Function function = new Function("owner", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<String> symbol() {
        Function function = new Function("symbol", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<BigInteger> safeSub(BigInteger a, BigInteger b) {
        Function function = new Function("safeSub", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(a), 
                new org.web3j.abi.datatypes.generated.Uint256(b)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> transfer(String to, BigInteger tokens) {
        Function function = new Function(
                "transfer", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(to), 
                new org.web3j.abi.datatypes.generated.Uint256(tokens)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> safeDiv(BigInteger a, BigInteger b) {
        Function function = new Function("safeDiv", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(a), 
                new org.web3j.abi.datatypes.generated.Uint256(b)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> approveAndCall(String spender, BigInteger tokens, byte[] data) {
        Function function = new Function(
                "approveAndCall", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(spender), 
                new org.web3j.abi.datatypes.generated.Uint256(tokens), 
                new org.web3j.abi.datatypes.DynamicBytes(data)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> safeMul(BigInteger a, BigInteger b) {
        Function function = new Function("safeMul", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(a), 
                new org.web3j.abi.datatypes.generated.Uint256(b)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<String> newOwner() {
        Function function = new Function("newOwner", 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteCall<TransactionReceipt> transferAnyERC20Token(String tokenAddress, BigInteger tokens) {
        Function function = new Function(
                "transferAnyERC20Token", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(tokenAddress), 
                new org.web3j.abi.datatypes.generated.Uint256(tokens)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> allowance(String tokenOwner, String spender) {
        Function function = new Function("allowance", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(tokenOwner), 
                new org.web3j.abi.datatypes.Address(spender)), 
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

    public RemoteCall<TransactionReceipt> transferOwnership(String _newOwner) {
        Function function = new Function(
                "transferOwnership", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_newOwner)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<SampleToken> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(SampleToken.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<SampleToken> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(SampleToken.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static SampleToken load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new SampleToken(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static SampleToken load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new SampleToken(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class OwnershipTransferredEventResponse {
        public String _from;

        public String _to;
    }

    public static class TransferEventResponse {
        public String from;

        public String to;

        public BigInteger tokens;
    }

    public static class ApprovalEventResponse {
        public String tokenOwner;

        public String spender;

        public BigInteger tokens;
    }
}
