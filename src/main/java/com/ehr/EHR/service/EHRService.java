package com.ehr.EHR.service;

import com.ehr.EHR.model.EHR;
import com.ehr.EHR.repository.EHRRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EHRService {
    @Autowired
    EHRRepository ehrRepository;

    public List<EHR> getAllEHR(){
        return ehrRepository.findAll();
    }

    public void addEHR(EHR ehr){
        ehrRepository.save(ehr);
    }
}
