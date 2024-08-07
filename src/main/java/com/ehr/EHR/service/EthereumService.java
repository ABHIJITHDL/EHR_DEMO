package com.ehr.EHR.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthChainId;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ChainIdLong;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;


@Service
public class EthereumService {

    private final Web3j web3j;
    private final Credentials credentials;
    String toAddress;

    public EthereumService(@Value("${ethereum.node.url}") String nodeUrl,
                           @Value("${ethereum.private.key}") String privateKey,@Value("ethereum.receiver.public.key") String toAddress) {
        this.web3j = Web3j.build(new HttpService(nodeUrl));
        this.credentials = Credentials.create(privateKey);
        this.toAddress=toAddress;
    }

    public BigInteger getChainId() throws Exception {
        EthChainId ethChainId = web3j.ethChainId().send();
        return ethChainId.getChainId();
    }

    public String storeHash(String hash) throws Exception {
        // Replace with your actual gas price and gas limit values
        BigInteger gasPrice = BigInteger.valueOf(20000000000L); // Adjust gas price based on network conditions
        BigInteger gasLimit = BigInteger.valueOf(6721975); // Set gas limit for the transaction

        // Get the nonce value
        BigInteger nonce = web3j.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.LATEST)
                .send().getTransactionCount();

        RawTransaction rawTransaction = RawTransaction.createTransaction(
                nonce,
                gasPrice,
                gasLimit,
                toAddress,
                BigInteger.ZERO, // value to be sent
                hash
        );

        // Fetch the chain ID
        BigInteger chainId = getChainId();

        // Sign the transaction
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, chainId.longValue(), credentials);
        String hexValue = Numeric.toHexString(signedMessage);

        // Send the transaction
        EthSendTransaction transactionResponse = web3j.ethSendRawTransaction(hexValue).sendAsync().get();

        // Get the transaction hash
        String transactionHash = transactionResponse.getTransactionHash();
        if (transactionHash == null) {
            throw new RuntimeException("Transaction failed: " + transactionResponse.getError().getMessage());
        }

        return transactionHash;
    }
    }


    /*public String storeHash(String hash) throws Exception {

        return Transfer.sendFunds(
                web3j, credentials, contractAddress,
                BigDecimal.ZERO, Convert.Unit.ETHER
        ).sendAsync().get().getTransactionHash().;
    }*/

    /*public CompletableFuture<String> storeHash(String hash) {
        HashStorage contract = HashStorage.load(
                contractAddress, web3j, credentials, Convert.toWei("1", Convert.Unit.ETHER).toBigInteger(), new BigDecimal(30000)
        );

        return contract.storeHash(new Utf8String(hash)).sendAsync().thenApply(transactionReceipt -> transactionReceipt.getTransactionHash());
    }*/
