package org.dsahu.langchain.learning.resume.repository;

import lombok.RequiredArgsConstructor;
import org.dsahu.langchain.learning.resume.dto.ResumeSearchRequest;
import org.dsahu.langchain.learning.resume.entity.ResumeDocument;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ResumeSearchRepositoryImpl implements ResumeSearchRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<ResumeDocument> search(ResumeSearchRequest request) {

        Query query = new Query();

        List<Criteria> criteriaList = new ArrayList<>();

        if (request.profileType() != null && !request.profileType().isBlank()) {
            criteriaList.add(
                    Criteria.where("profile.profileType")
                            .is(request.profileType())
            );
        }

        if (request.country() != null && !request.country().isBlank()) {
            criteriaList.add(
                    Criteria.where("profile.country")
                            .is(request.country())
            );
        }

        if (request.location() != null && !request.location().isBlank()) {
            criteriaList.add(
                    Criteria.where("profile.location")
                            .is(request.location())
            );
        }

        if (request.minExperience() != null) {
            criteriaList.add(
                    Criteria.where("profile.yearsOfExperience")
                            .gte(request.minExperience())
            );
        }

        if (!criteriaList.isEmpty()) {
            query.addCriteria(
                    new Criteria().andOperator(
                            criteriaList.toArray(new Criteria[0])
                    )
            );
        }
        if (request.skills() != null && !request.skills().isEmpty()) {
            criteriaList.add(
                    Criteria.where("profile.skills")
                            .in(request.skills())
            );
        }
        if (request.domains() != null && !request.domains().isEmpty()) {
            criteriaList.add(
                    Criteria.where("profile.domains")
                            .in(request.domains())
            );
        }

        return mongoTemplate.find(query, ResumeDocument.class);
    }
}