package com.paystream.user.service.impl;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.util.DateTime;
import com.paystream.user.document.UserDocument;
import com.paystream.user.dto.UserProfileResponse;
import com.paystream.user.repository.UserElasticsearchRepository;
import com.paystream.user.service.UserSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserSearchServiceImpl implements UserSearchService {

    private final UserElasticsearchRepository userEsRepository;
    private final ElasticsearchOperations elasticsearchOperations;


    @Override
    public UserDocument createUser(UserDocument user) {
        return userEsRepository.save(user);
    }

    @Override
    public UserDocument getUserProfile(String userId) {
        return userEsRepository.findById(userId).orElse(null);
    }

    @Override
    public void deleteUser(String userId) {
        userEsRepository.deleteById(userId);
    }

    @Override
    public void removeUser(String userId) {

    }

    @Override
    public List<UserDocument> getAllUsers() {
        return List.of();
    }

    @Override
    public List<UserDocument> searchUsers(String keyword, String status, int page, int size) {
        DateTime date = DateTime.of("2023-01-01T00:00:00Z");
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q.match(b -> b.field("fullName").query(keyword)))
                .withPageable(PageRequest.of(page, size))
                .build();

        NativeQuery query1 = NativeQuery.builder()
                .withQuery(q -> q.multiMatch(b -> b.query(keyword)
                        .fields("fullName", "email", "phone")))
                .withPageable(PageRequest.of(page, size))
                .build();

        NativeQuery query2 = NativeQuery.builder()
                .withQuery(q -> q.multiMatch(b -> b.query(keyword)
                        .fields("fullName^3", "email^1.5", "phone")))
                .withPageable(PageRequest.of(page, size))
                .build();


        NativeQuery query3 = NativeQuery.builder()
                .withQuery(q -> q.match(m -> m.query(keyword).field("fullName").fuzziness("AUTO")))
                .withPageable(PageRequest.of(page, size))
                .build();

        NativeQuery query4 = NativeQuery.builder()
                .withQuery(q -> q.term(t -> t.field("status").value(status)))
                .build();
        NativeQuery query5 = NativeQuery.builder()
                .withQuery(q -> q.terms(
                        t -> t.field("status").terms(
                        ts -> ts.value(List.of(FieldValue.of(status))))))
                .build();
        NativeQuery query6 = NativeQuery.builder()
                .withQuery(t -> t.range(r -> r.field("createdAt")
                        .from(date.toString()).to("2024-01-01T00:00:00Z")))
                .build();

        NativeQuery query7 = NativeQuery.builder()
                .withQuery(t -> t.range(r -> r.field("status")
                        .gte(JsonData.of(10)).lt(JsonData.of(20))))
                .build();


        SearchHits<UserDocument> searchHits = elasticsearchOperations.search(query, UserDocument.class);

        return searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

}
