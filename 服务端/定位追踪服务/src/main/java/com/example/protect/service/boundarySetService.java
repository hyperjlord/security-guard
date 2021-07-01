package com.example.protect.service;

import com.example.protect.entities.boundarySet;

import java.util.List;

public interface boundarySetService {
     public boolean storageBoundary(boundarySet boundarySetEntity);
     public boolean storageEdge(boundarySet boundarySet);
     public List<boundarySet> getBoundaryById(int account_id);
     public List<String> getEdgeById(int account_id);
}
