package com.ehr.EHR.mapper;

import com.ehr.EHR.dto.BlockDTO;
import com.ehr.EHR.dto.TransactionDTO;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Transaction;

import java.util.ArrayList;
import java.util.List;

public class BlockMapper {
    public static BlockDTO mappTOBlockDTO(EthBlock.Block block) {
        BlockDTO blockDTO = new BlockDTO();
        blockDTO.setHash(block.getHash());
        blockDTO.setNumber(block.getNumber().intValue());
        blockDTO.setTimestamp(block.getTimestampRaw());

        List<TransactionDTO> transactionDTOS = new ArrayList<>();
        for (EthBlock.TransactionResult transactionResult : block.getTransactions()) {
            Object transactionObject = transactionResult.get();
            TransactionDTO transactionDTO = new TransactionDTO();

            if (transactionObject instanceof EthBlock.TransactionObject) {
                EthBlock.TransactionObject transaction = (EthBlock.TransactionObject) transactionObject;
                transactionDTO.setFrom(transaction.getFrom());
                transactionDTO.setTo(transaction.getTo());
                transactionDTO.setHash(transaction.getHash());
                transactionDTO.setInput(transaction.getInput());
            } else if (transactionObject instanceof String) {
                // Handle the case where the transaction is a String (probably a transaction hash)
                String transactionHash = (String) transactionObject;
                transactionDTO.setHash(transactionHash);
                // Note: Other fields will be null or default values
            } else {
                System.out.println("Unexpected transaction type: " + transactionObject.getClass().getName());
                continue; // Skip this transaction
            }

            transactionDTOS.add(transactionDTO);
        }
        blockDTO.setTransactions(transactionDTOS);
        return blockDTO;
    }
}
