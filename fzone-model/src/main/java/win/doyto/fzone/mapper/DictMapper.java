package win.doyto.fzone.mapper;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import win.doyto.fzone.model.Dict;

@Mapper
@CacheNamespace(implementation = org.mybatis.caches.hazelcast.HazelcastCache.class)
public interface DictMapper {
    String Table = "Dict";
    String LIST = "SELECT * FROM " + Table;
    String HAS = "SELECT COUNT(*) > 0 FROM " + Table;
    String DELETE = "DELETE FROM " + Table;

    String _LIMIT = " LIMIT #{limit}";
    String _OFFSET = " OFFSET #{offset}";
    String _LIMIT_OFFSET = _LIMIT + _OFFSET;
    String _WHERE_ID = " WHERE id = #{id}";

    /* ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ */
    @Select(LIST + " WHERE `key` = #{key}")
    Dict getByKey(String key);
    /* ▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲ */

    @Select(LIST + _WHERE_ID)
    Dict get(Serializable id);

    @Delete(DELETE + _WHERE_ID)
    Integer delete(Serializable id);

    @Insert({
            "insert into",
            Table,
            "(`name`,`parentId`,`rank`,`key`,`value`,`memo`,`leaf`,`sequence`,`asDefault`,`createUserId`)",
            "values",
            "(#{name},#{parentId},#{rank},#{key},#{value},#{memo},#{leaf},#{sequence},#{asDefault},#{createUserId})"
    })
    @Options(useGeneratedKeys = true)
    int insert(Dict record);

    @UpdateProvider(type = DictSqlProvider.class, method = "update")
    int update(Dict record);

    /**
     * 检查某列是否存在某值
     *
     * @param column 列名
     * @param value  待检值
     * @return 如果值存在, 则返回true; 否则返回false
     */
    @Select(HAS + " WHERE ${column} = #{value}")
    @Options(useCache = false)
    Boolean hasValueOnColumn(@Param("column") String column, @Param("value") String value);

    @SelectProvider(type = DictSqlProvider.class, method = "query")
    List<Dict> query(Dict record);

    @SelectProvider(type = DictSqlProvider.class, method = "count")
    int count(Dict record);

    class DictSqlProvider {
        private String queryOrCount(Dict record, boolean query) {
            return new SQL() {{
                SELECT(query ? "*" : "COUNT(*)");
                FROM(Table);
                if (record.getName() != null) {
                    WHERE("name like CONCAT('%',#{name},'%')");
                }
                if (record.getRank() == null) {
                    throw new NullPointerException("查询时必须指定级别范围!");
                }
                WHERE("rank >= #{rank}");
            }}.toString() + (query && record.needPaging() ? _LIMIT_OFFSET : "");
        }

        public String query(Dict record) {
            return queryOrCount(record, true);
        }

        public String count(Dict record) {
            return queryOrCount(record, false);
        }

        public String update(final Dict record) {
            return new SQL() {{
                UPDATE(Table);
                if (record.getName() != null) {
                    SET("`name` = #{name,jdbcType=VARCHAR}");
                }
                if (record.getParentId() != null) {
                    SET("`parentId` = #{parentId,jdbcType=INTEGER}");
                }
                if (record.getRank() != null) {
                    SET("`rank` = #{rank,jdbcType=INTEGER}");
                }
                if (record.getKey() != null) {
                    SET("`key` = #{key,jdbcType=VARCHAR}");
                }
                if (record.getValue() != null) {
                    SET("`value` = #{value,jdbcType=VARCHAR}");
                }
                if (record.getMemo() != null) {
                    SET("`memo` = #{memo,jdbcType=VARCHAR}");
                }
                if (record.getLeaf() != null) {
                    SET("`leaf` = #{leaf,jdbcType=BIT}");
                }
                if (record.getSequence() != null) {
                    SET("`sequence` = #{sequence,jdbcType=INTEGER}");
                }
                if (record.getAsDefault() != null) {
                    SET("`asDefault` = #{asDefault,jdbcType=BIT}");
                }
                if (record.getUpdateTime() != null) {
                    SET("`updateTime` = #{updateTime,jdbcType=TIMESTAMP}");
                }
                if (record.getUpdateUserId() != null) {
                    SET("`updateUserId` = #{updateUserId,jdbcType=INTEGER}");
                }
                if (record.getValid() != null) {
                    SET("`valid` = #{valid,jdbcType=BIT}");
                }
                WHERE("id = #{id,jdbcType=INTEGER}");
            }}.toString();
        }
    }
}