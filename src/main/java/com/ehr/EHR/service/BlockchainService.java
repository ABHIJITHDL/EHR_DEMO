package com.ehr.EHR.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
public class BlockchainService {
    @Autowired
    private Web3j web3j;


    public List<EthBlock.Block> getLatestBlocks(int count) throws IOException{
        List<EthBlock.Block> blocks=new ArrayList<>();
        EthBlock.Block latestBlock=web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST,false).send().getBlock();
        blocks.add(latestBlock);
        System.out.println(latestBlock.getTransactions().getFirst().get());
        System.out.println(latestBlock.getTransactions());
        int latest = latestBlock.getNumber().intValue();
        if ( latest < count) {
            count = latest;
        }

        // Retrieve previous blocks
        for (int i = 1; i < count; i++) {
            BigInteger blockNumber = BigInteger.valueOf(latest-i);
            EthBlock.Block block = web3j.ethGetBlockByNumber(
                    DefaultBlockParameter.valueOf(blockNumber),
                    false
            ).send().getBlock();
            blocks.add(block);
        }

        return blocks;
    }
}
