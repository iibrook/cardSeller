/*
 * Copyright 2002-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.card.seller.dao.hibernate;

import com.card.seller.dao.hibernate.annotation.StateDelete;
import com.card.seller.domain.ConvertUtils;
import com.card.seller.domain.ReflectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.*;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.spi.NamedQueryDefinition;
import org.hibernate.engine.spi.NamedSQLQueryDefinition;
import org.hibernate.internal.AbstractQueryImpl;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.jdbc.Work;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.transform.Transformers;
import org.hibernate.type.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Hibernate基础类,包含对Hibernate的CURD和其他Hibernate操作
 *
 * @param <T>  ROM对象
 * @param <PK> ORM主键ID类型
 * @author vincent
 */
@SuppressWarnings("unchecked")
public class BasicHibernateDao<T, PK extends Serializable> {

    protected SessionFactory sessionFactory;

    protected Class<T> entityClass;

    protected final String DEFAULT_ALIAS = "X";

    private static Logger logger = LoggerFactory.getLogger(BasicHibernateDao.class);

    /**
     * 构造方法
     */
    public BasicHibernateDao() {
        entityClass = ReflectionUtils.getSuperClassGenricType(getClass());
    }

    /**
     * 构造方法
     *
     * @param entityClass orm实体类型class
     */
    public BasicHibernateDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * 设置Hibernate sessionFactory
     *
     * @param sessionFactory
     */
    @Autowired(required = false)
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * 获取Hibernate SessionFactory
     *
     * @return {@link org.hibernate.SessionFactory}
     */
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * 取得当前Session.
     *
     * @return {@link org.hibernate.Session}
     */
    public Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * 新增对象.
     *
     * @param entity orm实体
     */
    public void insert(T entity) {
        getSession().save(entity);
    }

    /**
     * 批量新增对象
     *
     * @param list orm实体集合
     */
    public void insertAll(List<T> list) {

        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        for (Iterator<T> it = list.iterator(); it.hasNext(); ) {
            insert(it.next());
        }

    }

    /**
     * 更新对象
     *
     * @param entity orm实体
     */
    public void update(T entity) {
        getSession().update(entity);
    }

    /**
     * 批量更新对象
     *
     * @param list orm实体集合
     */
    public void updateAll(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        for (Iterator<T> it = list.iterator(); it.hasNext(); ) {
            update(it.next());
        }
    }

    /**
     * 新增或修改对象
     *
     * @param entity orm实体
     */
    public void saveOrUpdate(T entity) {
        getSession().saveOrUpdate(entity);
    }

    /**
     * 新增对象
     *
     * @param entity orm实体
     */
    public void save(T entity) {
        getSession().save(entity);
    }

    /**
     * 保存或更新全部对象
     *
     * @param list orm实体集合
     */
    public void saveAll(List<T> list) {

        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        for (Iterator<T> it = list.iterator(); it.hasNext(); ) {
            saveOrUpdate(it.next());
        }
    }

    /**
     * 删除对象.
     *
     * @param entity 对象必须是session中的对象或含PK属性的transient对象.
     */
    public void delete(T entity) {

        if (entity == null) {
            logger.warn("要删除的对象为:null");
            return;
        }
        Class<?> entityClass = ReflectionUtils.getTargetClass(entity);
        StateDelete stateDelete = ReflectionUtils.getAnnotation(entityClass, StateDelete.class);
        if (stateDelete != null) {
            Object value = ConvertUtils.convertToObject(stateDelete.value(), stateDelete.type().getValue());
            ReflectionUtils.invokeSetterMethod(entity, stateDelete.propertyName(), value);
            update(entity);
        } else {
            getSession().delete(entity);
        }

    }

    /**
     * 按PK删除对象.
     *
     * @param id 主键ID
     */
    public void delete(PK id) {
        delete(get(id));
    }

    /**
     * 按PK批量删除对象
     *
     * @param ids 主键ID集合
     */
    public void deleteAll(List<PK> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return;
        }
        for (Iterator<PK> it = ids.iterator(); it.hasNext(); ) {
            delete(it.next());
        }

    }

    /**
     * 按orm实体集合删除对象
     *
     * @param list
     */
    public void deleteAllByEntities(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }

        for (Iterator<T> it = list.iterator(); it.hasNext(); ) {
            delete(it.next());
        }
    }


    /**
     * 按PK获取对象实体.如果找不到对象或者id为null值时，返回null,参考{@link org.hibernate.Session#get(Class, java.io.Serializable)}
     *
     * @param id 主键ID
     * @see org.hibernate.Session#get(Class, java.io.Serializable)
     */
    public T get(PK id) {

        if (id == null) {
            return null;
        }

        return (T) getSession().get(entityClass, id);
    }

    /**
     * 按PK获取对象代理.如果id为null，返回null。参考{@link org.hibernate.Session#load(Class, java.io.Serializable)}
     *
     * @param id 主键ID
     * @see org.hibernate.Session#load(Class, java.io.Serializable)
     */
    public T load(PK id) {
        if (id == null) {
            return null;
        }

        return (T) getSession().load(entityClass, id);
    }

    /**
     * 按PK列表获取对象列表.
     *
     * @param ids 主键ID集合
     * @return List
     */
    public List<T> get(Collection<PK> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return createCriteria(Restrictions.in(getIdName(), ids)).list();
    }

    /**
     * 按PK列表获取对象列表.
     *
     * @param ids 主键ID数据
     * @return List
     */
    public List<T> get(PK[] ids) {
        return createCriteria(Restrictions.in(getIdName(), ids)).list();
    }

    /**
     * 取得对象的主键名.
     *
     * @return String
     */
    public String getIdName() {
        ClassMetadata meta = sessionFactory.getClassMetadata(entityClass);
        return meta.getIdentifierPropertyName();
    }

    /**
     * 获取实体名称
     *
     * @return String
     */
    public String getEntityName() {
        ClassMetadata meta = sessionFactory.getClassMetadata(entityClass);
        return meta.getEntityName();
    }

    /**
     * 获取全部对象.
     *
     * @return List
     */
    public List<T> getAll() {
        return createCriteria().list();
    }

    /**
     * 通过HQL查询全部
     *
     * @param queryOrNamedQuery hql 或者Hibernate的NamedQuery
     * @param values            与属性名方式的hql值
     * @return List
     */
    public <X> List<X> findByQuery(String queryOrNamedQuery, Map<String, Object> values) {
        return createQuery(queryOrNamedQuery, values).list();
    }

    /**
     * 通过HQL查询全部
     *
     * @param queryOrNamedQuery hql 或者Hibernate的NamedQuery
     * @param values            与属性名方式的hql值
     * @return 分页
     */
    public <X> List<X> findByQuery(String queryOrNamedQuery, Map<String, Object> values, int offset, int fetchSize) {
        Query query = createQuery(queryOrNamedQuery);
        if (values != null) {
            query.setProperties(values);
        }
        query.setFirstResult(offset);
        query.setMaxResults(fetchSize);
        return query.list();
    }

    public <X> List<X> findBySQLQuery(String queryOrNamedQuery, Map<String, Class> aliasMap, Class transferClass, Map<String, Object> values) {
        SQLQuery query = createSQLQuery(queryOrNamedQuery, aliasMap, values);
        setQueryTransform(transferClass, query);
        return query.list();
    }


    public <X> List<X> findBySQLQuery(String queryOrNamedQuery, Map<String, Class> aliasMap, Class transferClass, Map<String, Object> values, int offset, int fetchSize) {
        SQLQuery query = createSQLQuery(queryOrNamedQuery, aliasMap, values);
        setQueryTransform(transferClass, query);
        query.setFirstResult(offset);
        query.setMaxResults(fetchSize);
        return query.list();
    }

    public Long countSqlResult(String queryString, Map<String, Object> values) {
        String countHql = prepareCountHql(queryString);
        try {
            Object result = createSQLQuery(countHql, null, values).uniqueResult();
            return Long.valueOf(result.toString());
        } catch (Exception e) {
            throw new RuntimeException("hql不能自动计算总数:" + countHql, e);
        }
    }

    private void setQueryTransform(Class transferClass, SQLQuery query) {
        if (transferClass != null) {
            List<Field> fieldList = new ArrayList<Field>();
            Field[] fields = transferClass.getDeclaredFields();
            for (Field field : fields) {
                fieldList.add(field);
            }
            //暂不使用递归获取父类fields方法 (getSuperclassFields)
            Class superclass = transferClass.getSuperclass();
            if(superclass != null) {
                Field[] superFields = superclass.getDeclaredFields();
                for (Field superField : superFields) {
                    fieldList.add(superField);
                }
            }

            fields = fieldList.toArray(new Field[fieldList.size()]);
            for (int i = 0; i < fields.length; i++) {
                if (fields[i].getType().getName().equals(Long.class.getName())) {
                    query.addScalar(fields[i].getName(), LongType.INSTANCE);
                } else if (fields[i].getType().getName().equals(Date.class.getName())) {
                    query.addScalar(fields[i].getName(), TimestampType.INSTANCE);
                } else if (fields[i].getType().getName().equals(BigDecimal.class.getName())) {
                    query.addScalar(fields[i].getName(), BigDecimalType.INSTANCE);
                } else if (fields[i].getType().getName().equals(Double.class.getName())) {
                    query.addScalar(fields[i].getName(), DoubleType.INSTANCE);
                } else if (fields[i].getType().getName().equals(Integer.class.getName()) || fields[i].getType().getName().equalsIgnoreCase("int")) {
                    query.addScalar(fields[i].getName(), IntegerType.INSTANCE);
                } else if (fields[i].getType().getName().equals(Boolean.class.getName())) {
                    query.addScalar(fields[i].getName(), BooleanType.INSTANCE);
                } else {
                    query.addScalar(fields[i].getName(), StringType.INSTANCE);
                }
                query.setResultTransformer(Transformers.aliasToBean(transferClass));
            }
        }
    }

    /**
     * 递归获取父类 fields
     * @param transferClass
     * @param fieldList
     */
    private void getSuperclassFields(Class transferClass, List<Field> fieldList) {
        Class superclass = transferClass.getSuperclass();
        if(superclass != null) {
            Field[] superFields = superclass.getDeclaredFields();
            for (Field superField : superFields) {
                fieldList.add(superField);
            }

            if (superclass.getSuperclass() != null) {
                getSuperclassFields(superclass, fieldList);
            }
        }
    }

    public <X> List<X> findBySQLQuery(String queryOrNamedQuery, Map<String, Class> aliasMap, Map<String, Object> values, int offset, int fetchSize) {
        SQLQuery query = createSQLQuery(queryOrNamedQuery, aliasMap, values);
        query.setFirstResult(offset);
        query.setMaxResults(fetchSize);
        return (List<X>) query.list();
    }

    public <X> List<X> findBySQLQuery(String queryOrNamedQuery, Map<String, Class> aliasMap, Map<String, Object> values) {
        SQLQuery query = createSQLQuery(queryOrNamedQuery, aliasMap, values);
        query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return (List<X>) query.list();
    }

    public <X> X findBySQLQuery(String queryOrNamedQuery, Map<String, Object> values) {
        SQLQuery query = createSQLQuery(queryOrNamedQuery, null, values);
        query.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
        return (X) query.uniqueResult();
    }

    /**
     * 通过HQL查询全部
     *
     * @param queryOrNamedQuery hql 或者Hibernate的NamedQuery
     * @param values            可变长度的hql值
     * @return List
     */
    public <X> List<X> findByQuery(String queryOrNamedQuery, Object... values) {
        return createQuery(queryOrNamedQuery, values).list();
    }

    /**
     * 通过hql查询单个orm实体
     *
     * @param queryOrNamedQuery hql 或者Hibernate的NamedQuery
     * @param values            以属性名的hql值
     * @return Object
     */
    public <X> X findUniqueByQuery(String queryOrNamedQuery, Map<String, Object> values) {
        return (X) createQuery(queryOrNamedQuery, values).uniqueResult();
    }

    /**
     * 通过hql查询单个orm实体
     *
     * @param queryOrNamedQuery hql 或者Hibernate的NamedQuery
     * @param values            可变长度的hql值
     * @return Object
     */
    public <X> X findUniqueByQuery(String queryOrNamedQuery, Object... values) {
        return (X) createQuery(queryOrNamedQuery, values).uniqueResult();
    }

    public <X> X findUniqueBySQLQuery(String queryOrNamedQuery, Map<String, Object> values) {
        return (X) createSQLQuery(queryOrNamedQuery, null, values).uniqueResult();
    }

    /**
     * 获取全部对象
     *
     * @param orders 排序对象，不需要排序，可以不传
     * @return List
     */
    public List<T> getAll(Order... orders) {
        Criteria c = createCriteria();
        setOrderToCriteria(c, orders);
        return c.list();
    }

    /**
     * 获取实体的总记录数
     *
     * @return int
     */
    public int entityCount() {
        return countHqlResult("from " + getEntityName() + " " + DEFAULT_ALIAS).intValue();
    }

    /**
     * 根据Criterion可变数组创建Criteria对象
     *
     * @param criterions 可变长度的Criterion数组
     * @return @return {@link org.hibernate.Criteria}
     */
    protected Criteria createCriteria(Criterion... criterions) {

        Criteria criteria = getSession().createCriteria(this.entityClass);

        for (Criterion criterion : criterions) {

            criteria.add(criterion);
        }
        return criteria;
    }

    /**
     * 根据查询HQL与参数列表创建Query对象
     *
     * @param queryOrNamedQuery hql 或者Hibernate的NamedQuery
     * @param values            命名参数,按名称绑定.
     * @return {@link org.hibernate.Query}
     */
    protected Query createQuery(String queryOrNamedQuery, Map<String, ?> values) {
        Query query = createQuery(queryOrNamedQuery);
        if (values != null) {
            query.setProperties(values);
        }
        return query;
    }

    /**
     * 根据hql创建Hibernate Query对象
     *
     * @param queryOrNamedQuery hql 或者Hibernate的NamedQuery
     * @param values            数量可变的参数,按顺序绑定.
     * @return {@link org.hibernate.Query}
     */
    protected Query createQuery(String queryOrNamedQuery, Object... values) {
        Assert.hasText(queryOrNamedQuery, "queryOrNamedQuery不能为空");

        SessionFactoryImpl factory = (SessionFactoryImpl) sessionFactory;
        NamedQueryDefinition nqd = factory.getNamedQuery(queryOrNamedQuery);
        Query query = null;

        if (nqd != null) {
            query = getSession().getNamedQuery(queryOrNamedQuery);
        } else {
            query = getSession().createQuery(queryOrNamedQuery);
        }

        setQueryValues(query, values);
        return query;
    }

    /**
     * 根据查询SQL与参数列表创建Query对象
     *
     * @param queryOrSqlQuery query 或者 NamedSQLQuery
     * @param values          命名参数,按名称绑定.
     * @return {@link org.hibernate.Query}
     */
    protected SQLQuery createSQLQuery(String queryOrSqlQuery, Map<String, Class> aliasMap, Map<String, ?> values) {
        SQLQuery query = createSQLQuery(queryOrSqlQuery, aliasMap);
        if (values != null) {
            query.setProperties(values);
        }
        return query;
    }

    /**
     * 根据查询SQL与参数列表创建SQLQuery对象
     *
     * @param queryOrNamedSQLQuery query 或者 NamedSQLQuery
     * @param values               数量可变的参数,按顺序绑定.
     * @return {@link org.hibernate.SQLQuery}
     */
    protected SQLQuery createSQLQuery(String queryOrNamedSQLQuery, Map<String, Class> aliasMap, Object... values) {
        Assert.hasText(queryOrNamedSQLQuery, "queryOrNamedSQLQuery不能为空");
        SessionFactoryImpl factory = (SessionFactoryImpl) sessionFactory;
        NamedSQLQueryDefinition nsqlqd = factory.getNamedSQLQuery(queryOrNamedSQLQuery);
        Query query = null;
        if (nsqlqd != null) {
            query = getSession().getNamedQuery(queryOrNamedSQLQuery);
        } else {
            query = getSession().createSQLQuery(queryOrNamedSQLQuery);
        }

        setQueryValues(query, values);
        SQLQuery sqlQuery = (SQLQuery) query;
        if (aliasMap != null && aliasMap.size() > 0) {
            for (Iterator<Map.Entry<String, Class>> it = aliasMap.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, Class> entry = it.next();
                sqlQuery.addEntity(entry.getKey(), entry.getValue());
            }
        } else if (aliasMap != null) {
            sqlQuery.addEntity(entityClass);
        }
        return sqlQuery;
    }

    /**
     * 设置参数值到query的hql中
     *
     * @param query  Hibernate Query
     * @param values 参数值可变数组
     */
    protected void setQueryValues(Query query, Object... values) {
        if (ArrayUtils.isEmpty(values)) {
            return;
        }
        AbstractQueryImpl impl = (AbstractQueryImpl) query;
        String[] params = impl.getNamedParameters();

        int methodParameterPosition = params.length - 1;

        if (impl.hasNamedParameters()) {
            for (String p : params) {
                Object o = values[methodParameterPosition--];
                query.setParameter(p, o);
            }
        } else {
            for (Integer i = 0; i < values.length; i++) {
                query.setParameter(i, values[i]);
            }
        }
    }

    /**
     * 通过排序表达式向Criteria设置排序方式,
     *
     * @param criteria Criteria
     * @param orders   排序表达式，规则为:属性名称_排序规则,如:property_asc或property_desc,可以支持多个属性排序，用逗号分割,如:"property1_asc,proerty2_desc",也可以"property"不加排序规则时默认是desc
     */
    protected void setOrderToCriteria(Criteria criteria, Order... orders) {
        if (ArrayUtils.isEmpty(orders)) {
            return;
        }
        for (Order o : orders) {
            criteria.addOrder(o);
        }
    }

    /**
     * 执行count查询获得本次Hql查询所能获得的对象总数.使用命名方式参数
     * <p/>
     * <pre>
     * 	from object o where o.property = :proprty and o.property = :proprty
     * </pre>
     * <p/>
     * 本函数只能自动处理简单的hql语句,复杂的hql查询请另行编写count语句查询.
     *
     * @param queryString hql
     * @param values      值
     * @return long
     */
    protected Long countHqlResult(String queryString, Map<String, ?> values) {
        String countHql = prepareCountHql(queryString);

        try {
            return (Long) createQuery(countHql, values).uniqueResult();
        } catch (Exception e) {
            throw new RuntimeException("hql不能自动计算总数:" + countHql, e);
        }
    }

    /**
     * 执行count查询获得本次Hql查询所能获得的对象总数.(使用jdbc方式参数)
     * <p/>
     * <pre>
     * 	from object o where o.property = ? and o.property = ?
     * </pre>
     * <p/>
     * 本函数只能自动处理简单的hql语句,复杂的hql查询请另行编写count语句查询.
     *
     * @param queryString hql
     * @param values      值
     * @return long
     */
    protected Long countHqlResult(String queryString, Object... values) {
        String countHql = prepareCountHql(queryString);

        try {
            Object result = createQuery(countHql, values).uniqueResult();
            return (Long) result;
        } catch (Exception e) {
            throw new RuntimeException("hql不能自动计算总数:" + countHql, e);
        }
    }

    /**
     * 绑定计算总数HQL语句,返回绑定后的hql字符串
     *
     * @param orgHql hql
     * @return String
     */
    private String prepareCountHql(String orgHql) {
        String countField = StringUtils.substringBetween(orgHql, "select", "from");
        String countHql = MessageFormat.format("select count ({0}) {1} ",
                StringUtils.isEmpty(countField) ? "*" : countField,
                removeSelect(removeOrders(orgHql)));
        return countHql;
    }

    /**
     * 移除from前面的select 字段 返回移除后的hql字符串
     *
     * @param hql
     * @return String
     */
    private String removeSelect(String hql) {
        int beginPos = hql.toLowerCase().indexOf("from");
        return hql.substring(beginPos);
    }

    /**
     * 删除hql中的 order by的字段,返回删除后的新字符串
     *
     * @param hql
     * @return String
     */
    private String removeOrders(String hql) {
        Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(hql);
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, "");
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * 为Query添加distinct transformer,讲查询出来的重复数据进行distinct处理
     *
     * @param queryOrNamedQuery hql 或者Hibernate的NamedQuery
     * @param values            值
     * @return List
     */
    public <X> List<X> distinct(String queryOrNamedQuery, Object... values) {
        Query query = createQuery(queryOrNamedQuery, values);
        query.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

        return query.list();
    }

    /**
     * 为Query添加distinct transformer,讲查询出来的重复数据进行distinct处理
     *
     * @param queryOrNamedQuery hql 或者Hibernate的NamedQuery
     * @param values            值
     * @return List
     */
    public <X> List<X> distinct(String queryOrNamedQuery, Map<String, Object> values) {
        Query query = createQuery(queryOrNamedQuery, values);
        query.setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

        return query.list();
    }

    /**
     * Flush当前Session.
     */
    public void flush() {
        getSession().flush();
    }

    /**
     * 如果session中存在相同持久化识别的实例，用给出的对象的状态覆盖持久化实例
     *
     * @param entity 持久化实例
     */
    public void merge(T entity) {
        getSession().merge(entity);
    }

    /**
     * 如果session中存在相同持久化识别的实例，用给出的对象的状态覆盖持久化实例
     *
     * @param entity     持久化实例
     * @param entityName 持久化对象名称
     */
    public void merge(String entityName, T entity) {
        getSession().merge(entityName, entity);
    }

    /**
     * 刷新操作对象
     *
     * @param entity 操作对象
     */
    public void refresh(T entity) {
        getSession().refresh(entity);
    }

    /**
     * 刷新操作对象
     *
     * @param entity      操作对象
     * @param lockOptions Hibernate LockOptions
     */
    public void refresh(T entity, LockOptions lockOptions) {

        if (lockOptions == null) {
            refresh(entity);
        } else {
            getSession().refresh(entity, lockOptions);
        }
    }

    /**
     * 把操作对象在缓存区中直接清除
     *
     * @param entity 操作对象
     */
    public void evict(T entity) {
        getSession().evict(entity);
    }

    /**
     * 把session所有缓存区的对象全部清除，但不包括正在操作中的对象
     */
    public void clear() {
        getSession().clear();
    }

    /**
     * 对于已经手动给ID主键的操作对象进行insert操作
     *
     * @param entity          操作对象
     * @param replicationMode 创建策略
     */
    public void replicate(T entity, ReplicationMode replicationMode) {
        getSession().replicate(entity, replicationMode);
    }

    /**
     * 对于已经手动给ID主键的操作对象进行insert操作
     *
     * @param entityName      操作对象名称
     * @param entity          操作对象
     * @param replicationMode 创建策略
     */
    public void replicate(String entityName, T entity, ReplicationMode replicationMode) {
        getSession().replicate(entityName, entity, replicationMode);
    }

    /**
     * 把一个瞬态的实例持久化，但很有可能不能立即持久化实例，可能会在flush的时候才会持久化
     * 当它在一个transaction外部被调用的时候并不会触发insert。
     *
     * @param entity 瞬态的实例
     */
    public void persist(T entity) {
        getSession().persist(entity);
    }

    /**
     * 把一个瞬态的实例持久化，但很有可能不能立即持久化实例，可能会在flush的时候才会持久化
     * 当它在一个transaction外部被调用的时候并不会触发insert。
     *
     * @param entity     瞬态的实例
     * @param entityName 瞬态的实例名称
     */
    public void persist(String entityName, T entity) {
        getSession().persist(entityName, entity);
    }

    /**
     * 从当前Session中获取一个能够操作JDBC的Connection并执行想要操作的JDBC语句
     *
     * @param work Hibernate Work
     */
    public void doWork(Work work) {
        getSession().doWork(work);
    }

    /**
     * 判断entity实例是否已经与session缓存关联,是返回true,否则返回false
     *
     * @param entity 实例
     * @return boolean
     */
    public boolean contains(Object entity) {
        return getSession().contains(entity);
    }

    /**
     * 执行HQL进行批量修改/删除操作.成功后返回更新记录数
     *
     * @param queryOrNamedQuery hql 或者Hibernate的NamedQuery
     * @param values            命名参数,按名称绑定.
     * @return int
     */
    public int executeUpdate(String queryOrNamedQuery, Map<String, ?> values) {
        return createQuery(queryOrNamedQuery, values).executeUpdate();
    }

    /**
     * 执行HQL进行批量修改/删除操作.成功后更新记录数
     *
     * @param queryOrNamedQuery hql 或者Hibernate的NamedQuery
     * @param values            参数值
     * @return int
     */
    public int executeUpdate(String queryOrNamedQuery, Object... values) {
        return createQuery(queryOrNamedQuery, values).executeUpdate();
    }


    /**
     * 执行SQL进行批量修改/删除操作.成功后更新记录数
     *
     * @param queryOrNamedQuery sql 或者Hibernate的NamedQuery
     * @param values            参数值
     * @return int
     */
    public int executeSQLUpdate(String queryOrNamedQuery, Map<String, ?> values) {
        return createSQLQuery(queryOrNamedQuery, null, values).executeUpdate();
    }
}
