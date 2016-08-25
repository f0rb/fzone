package win.doyto.fzone.mapper;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import win.doyto.fzone.model.Nile;

@Mapper
@CacheNamespace(implementation = org.mybatis.caches.hazelcast.HazelcastCache.class)
public interface NileMapper {
    String Table = "Nile";
    String LIST = "SELECT * FROM " + Table;
    String HAS = "SELECT COUNT(*) > 0 FROM " + Table;
    String DELETE = "DELETE FROM " + Table;

    String _LIMIT = " LIMIT #{limit}";
    String _OFFSET = " OFFSET #{offset}";
    String _LIMIT_OFFSET = _LIMIT + _OFFSET;
    String _WHERE_ID = " WHERE id = #{id}";

    @Select(LIST + _WHERE_ID)
    Nile get(Serializable id);

    @Delete(DELETE + _WHERE_ID)
    Integer delete(Serializable id);

    @Insert({
            "insert into",
            Table,
            "(`id`,`parentId`,`name`,`type`,`size`,`ownerId`,`md5`,`sha1`,`mime`)",
            "values",
            "(#{id},#{parentId},#{name},#{type},#{size},#{ownerId},#{md5},#{sha1},#{mime})"
    })
    int insert(Nile record);

    @UpdateProvider(type = NileSqlProvider.class, method = "update")
    int update(Nile record);

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

    @SelectProvider(type = NileSqlProvider.class, method = "query")
    List<Nile> query(Nile record);

    @SelectProvider(type = NileSqlProvider.class, method = "count")
    int count(Nile record);


    Integer delete(Nile record);

    Integer deleteDirectory(Nile record);

    Nile get(String id);

    Nile getParentAndName(String id);

    List<Nile> list(Nile record);

    List<Nile> listDirectoriesByUserId(Integer userId);

    void listDirectories(Nile record);

    List<Nile> listChildren(String parentId);

    void insertAll(List<Nile> records);

    Long count();

    String queryPath(Nile record);

    List<Integer> getSubrecordIds(Nile record);

    Long countNameUnderParent(String parentId, String filename);

    List<String> listNameUnderParent(String parentId);

    Long countByParentId(String parentId, Integer userId);


    class NileSqlProvider {
        private String queryOrCount(Nile record, boolean select) {
            return new SQL() {
                {
                    SELECT(select ? "*" : "COUNT(*)");
                    FROM(Table);
                    //if (record.getName() != null) {
                    //    WHERE("name like CONCAT('%',#{name},'%')");
                    //}
                }
            }.toString() + (select && record.needPaging() ? _LIMIT_OFFSET : "");
        }

        public String query(Nile record) {
            return queryOrCount(record, true);
        }

        public String count(Nile record) {
            return queryOrCount(record, false);
        }

        public String update(final Nile record) {
            return new SQL() {
                {
                    UPDATE(Table);
                    if (record.getParentId() != null) {
                        SET("`parentId` = #{parentId,jdbcType=VARCHAR}");
                    }
                    if (record.getName() != null) {
                        SET("`name` = #{name,jdbcType=VARCHAR}");
                    }
                    if (record.getType() != null) {
                        SET("`type` = #{type,jdbcType=INTEGER}");
                    }
                    if (record.getSize() != null) {
                        SET("`size` = #{size,jdbcType=INTEGER}");
                    }
                    if (record.getOwnerId() != null) {
                        SET("`ownerId` = #{ownerId,jdbcType=INTEGER}");
                    }
                    if (record.getMd5() != null) {
                        SET("`md5` = #{md5,jdbcType=VARCHAR}");
                    }
                    if (record.getSha1() != null) {
                        SET("`sha1` = #{sha1,jdbcType=VARCHAR}");
                    }
                    if (record.getMime() != null) {
                        SET("`mime` = #{mime,jdbcType=VARCHAR}");
                    }
                    if (record.getUpdateTime() != null) {
                        SET("`updateTime` = #{updateTime,jdbcType=TIMESTAMP}");
                    }
                    WHERE("id = #{id,jdbcType=INTEGER}");
                }
            }.toString();
        }
    }
}