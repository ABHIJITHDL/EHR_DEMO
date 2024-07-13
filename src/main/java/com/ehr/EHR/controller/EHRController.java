package com.ehr.EHR.controller;

import com.ehr.EHR.model.EHR;
import com.ehr.EHR.repository.EHRRepository;
import com.ehr.EHR.service.BlockchainService;
import com.ehr.EHR.service.EHRService;
import com.ehr.EHR.service.EthereumService;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import org.web3j.protocol.core.methods.response.EthBlock;


import java.io.IOException;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/api")
public class EHRController {

    @Autowired
    private EHRRepository ehrRepository;

    @Autowired
    private EthereumService ethereumService;

    @Autowired
    private EHRService ehrService;

    @Autowired
    private BlockchainService blockchainService;


    @GetMapping("/home")
    public String home(){
        return "home";
    }

    @GetMapping("/blocks")
    public ResponseEntity<List<EthBlock.Block>> getBlocks(){
        try{
            return new ResponseEntity<>(blockchainService.getLatestBlocks(5),HttpStatus.OK
            );
        }catch (IOException e){
            System.out.println(e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }
    @GetMapping("/ehr")
    public ResponseEntity<String> getEHR(){
        String response = ehrService.getAllEHR().toString();
        if(response==null)
            return new ResponseEntity<>("No data", HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/ehr")
    @ResponseBody
    public EHR createEHR(@RequestParam("file")MultipartFile file,
                         @RequestParam("patientId") String patientId) throws Exception {
        if(!file.getContentType().equals("application/pdf")){
            throw new IllegalArgumentException("File must be a pdf");
        }

        EHR ehr = new EHR();
        ehr.setId(null);
        ehr.setPatientId(patientId);
        ehr.setFileName(file.getOriginalFilename());
        ehr.setPdfData(file.getBytes());

        EHR savedEHR = ehrRepository.save(ehr);

        String pdfText = extractTextFromPDF(file.getBytes());
        String hash = calculateHash(pdfText);

        savedEHR.setPdfHash(hash);
        String txHash = ethereumService.storeHash(hash);
        savedEHR.setBlockchainHash(txHash);
        return ehrRepository.save(savedEHR);
    }

    private String calculateHash(String pdfText) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(pdfText.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(encodedHash);
    }

    private String extractTextFromPDF(byte[] pdfData) throws Exception {
        try (PDDocument document = PDDocument.load(pdfData)){
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}