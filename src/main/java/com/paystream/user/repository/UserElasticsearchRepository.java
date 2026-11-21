package com.paystream.user.repository;

import com.paystream.user.document.UserDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserElasticsearchRepository extends ElasticsearchRepository<UserDocument, String> {
    List<UserDocument> findByFullNameContaining(String keyword);
    List<UserDocument> findByStatus(String status);
}
