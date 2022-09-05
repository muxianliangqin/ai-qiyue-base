package com.qiyue.jpa.wrapper;

import com.qiyue.infrastructure.enums.ExceptionEnum;
import com.qiyue.infrastructure.exceptions.LogicException;
import com.qiyue.infrastructure.utils.Asserts;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Slf4j
@Data
public class JpaWrapper<T, I, B, V> {

    public static final String NON_NULL_PARAM = "参数不能为空";
    public static final String NON_NULL_ID = "主键ID（primaryKey）不能为空";
    public static final String NON_NULL_BUSINESS_ID = "业务ID（businessId）不能为空";
    public static final String NON_NULL_UPDATE_VALUE = "逻辑删除时，更新字段值函数（updateValue）不能为空";
    public static final String NON_NULL_FIND_BY_BUSINESS_ID = "根据业务ID查询的方法（findByBusinessId）不能为空";
    /**
     * JpaRepository 数据操作
     */
    private JpaRepository<T, I> repository;
    private Function<T, I> idGetter;
    private BiConsumer<T, I> idSetter;
    /**
     * 通过唯一索引查询实例
     */
    private Function<B, Optional<T>> findByBusinessId;
    /**
     * 从实例中获取唯一索引对象
     */
    private Function<T, B> businessId;
    /**
     * 修改某个字段的值
     */
    private BiConsumer<T, V> updateValue;

    private JpaWrapper() {

    }

    private JpaWrapper(Builder<T, I, B, V> builder) {
        this.repository = builder.repository;
        this.idGetter = builder.idGetter;
        this.idSetter = builder.idSetter;
        this.findByBusinessId = builder.findByBusinessId;
        this.businessId = builder.businessId;
        this.updateValue = builder.updateValue;
    }

    public static <T, I, U, V> Builder<T, I, U, V> builder() {
        return new Builder<>();
    }

    public T add(T t) {
        Optional<T> fromDbOptional = findOne(t);
        if (fromDbOptional.isPresent()) {
            throw new LogicException(ExceptionEnum.QBG_DATABASE_RECORD_HAS_EXIST,
                    fromDbOptional.get());
        }
        return repository.save(t);
    }

    public void delete(T t) {
        Asserts.nonNull(t, NON_NULL_PARAM);
        I i = idGetter.apply(t);
        deleteById(i);
    }

    public void deleteById(I i) {
        Asserts.nonNull(i, NON_NULL_ID);
        repository.deleteById(i);
    }

    public T updateValue(T t, V v) {
        Asserts.nonNull(t, NON_NULL_PARAM);
        Asserts.nonNull(v, NON_NULL_PARAM);
        Asserts.nonNull(updateValue, NON_NULL_UPDATE_VALUE);
        updateValue.accept(t, v);
        return updateEntity(t);
    }

    public T updateById(I i, V v) {
        Asserts.nonNull(i, NON_NULL_PARAM);
        Asserts.nonNull(v, NON_NULL_PARAM);
        Optional<T> fromDbOptional = findById(i);
        T fromDb = fromDbOptional.orElseThrow(() -> {
            throw new LogicException(ExceptionEnum.QBG_DATABASE_RECORD_NOT_FOUND, i);
        });
        updateValue.accept(fromDb, v);
        return repository.save(fromDb);
    }

    public T updateByBusinessId(B b, V v) {
        Asserts.nonNull(b, NON_NULL_PARAM);
        Asserts.nonNull(v, NON_NULL_PARAM);
        Optional<T> fromDbOptional = findByBusinessId(b);
        T fromDb = fromDbOptional.orElseThrow(() -> {
            throw new LogicException(ExceptionEnum.QBG_DATABASE_RECORD_NOT_FOUND, b);
        });
        updateValue.accept(fromDb, v);
        return repository.save(fromDb);
    }

    public T updateEntity(T t) {
        Asserts.nonNull(t, NON_NULL_PARAM);
        Optional<T> fromDbOptional = findOne(t);
        T fromDb = fromDbOptional.orElseThrow(() -> {
            throw new LogicException(ExceptionEnum.QBG_DATABASE_RECORD_NOT_FOUND, t);
        });
        idSetter.accept(t, idGetter.apply(fromDb));
        return repository.save(t);
    }

    public Optional<T> findOne(T t) {
        if (Objects.nonNull(businessId)) {
            B B = businessId.apply(t);
            return findByBusinessId(B);
        }
        I i = idGetter.apply(t);
        return findById(i);
    }

    public Optional<T> findById(I i) {
        Asserts.nonNull(i, NON_NULL_ID);
        return repository.findById(i);
    }

    public Optional<T> findByBusinessId(B b) {
        Asserts.nonNull(b, NON_NULL_BUSINESS_ID);
        Asserts.nonNull(findByBusinessId, NON_NULL_FIND_BY_BUSINESS_ID);
        return findByBusinessId.apply(b);
    }

    public List<T> findAll() {
        return repository.findAll();
    }

    public static class Builder<T, I, B, V> {
        private JpaRepository<T, I> repository;
        private Function<T, I> idGetter;
        private BiConsumer<T, I> idSetter;
        private Function<T, B> businessId;
        private Function<B, Optional<T>> findByBusinessId;
        private BiConsumer<T, V> updateValue;

        public Builder<T, I, B, V> base(JpaRepository<T, I> repository, Function<T, I> idGetter) {
            this.repository = repository;
            this.idGetter = idGetter;
            return this;
        }

        public Builder<T, I, B, V> findByBusinessId(Function<B, Optional<T>> findByBusinessId, Function<T, B> businessId) {
            this.findByBusinessId = findByBusinessId;
            this.businessId = businessId;
            return this;
        }

        public Builder<T, I, B, V> updateValue(BiConsumer<T, V> updateValue) {
            this.updateValue = updateValue;
            return this;
        }

        public Builder<T, I, B, V> updateEntity(BiConsumer<T, I> idSetter) {
            this.idSetter = idSetter;
            return this;
        }

        public JpaWrapper<T, I, B, V> build() {
            return new JpaWrapper<>(this);
        }
    }
}
