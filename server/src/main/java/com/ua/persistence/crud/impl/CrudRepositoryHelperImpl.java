package com.ua.persistence.crud.impl;

import org.apache.commons.collections4.MapUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.ua.exception.EntityNotFoundException;
import com.ua.persistence.crud.CrudRepositoryHelper;
import com.ua.persistence.entity.BaseEntity;
import com.ua.persistence.repository.AbstractRepository;
import com.ua.persistence.specification.AbstractSpecification;
import com.ua.persistence.specification.impl.AbstractSpecificationImpl;
import com.ua.util.PageAndSortUtil;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CrudRepositoryHelperImpl<
        E extends BaseEntity,
        R extends AbstractRepository<E>>
        implements CrudRepositoryHelper<E, R> {

    @Override
    public void create(R repository, E entity) {
        repository.save(entity);
    }

    @Override
    public void update(R repository, E entity) {
        checkExist(repository, entity.getId());
        repository.save(entity);
    }

    @Override
    public void delete(R repository, Long id) {
        checkExist(repository, id);
        repository.deleteById(id);
    }

    @Override
    public Optional<E> findById(R repository, Long id) {
        return repository.findById(id);
    }

    @Override
    public List<E> findAll(R repository, Map<String, String[]> parameterMap, Class<E> entityClass) {
        Specification<E> specification = null;
        if (MapUtils.isNotEmpty(parameterMap)) {
            AbstractSpecification<E> abstractSpecification = new AbstractSpecificationImpl<>();
            specification = abstractSpecification.generateCriteriaPredicate(parameterMap, entityClass);
        }

        Sort sort = PageAndSortUtil.generateSort(parameterMap);
        int page = PageAndSortUtil.generatePage(parameterMap);
        int size = PageAndSortUtil.generateSize(repository, parameterMap);
        if (size == 0) {
            size = 10;
        }
        PageRequest request = PageRequest.of(page, size, sort);

        Page<E> pageEntity;
        if (specification == null) {
            pageEntity = repository.findAll(request);
        } else {
            pageEntity = repository.findAll(specification, request);
        }

        return pageEntity.getContent();
    }

    @Override
    public long count(R repository, Map<String, String[]> parameterMap, Class<E> entityClass) {
        Specification<E> specification = null;
        if (MapUtils.isNotEmpty(parameterMap)) {
            AbstractSpecification<E> abstractSpecification = new AbstractSpecificationImpl<>();
            specification = abstractSpecification.generateCriteriaPredicate(parameterMap, entityClass);
        }
        if (specification == null) {
            return repository.count();
        } else {
            return repository.count(specification);
        }
    }

    private void checkExist(R repository, Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("entity not found with id " + id);
        }
    }
}
