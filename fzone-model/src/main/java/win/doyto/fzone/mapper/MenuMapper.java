package win.doyto.fzone.mapper;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import win.doyto.fzone.model.Menu;

@Mapper
@CacheNamespace(implementation = org.mybatis.caches.hazelcast.HazelcastCache.class)
public interface MenuMapper {
    String Table = "Menu";
    String LIST = "SELECT * FROM " + Table;
    String HAS = "SELECT COUNT(*) > 0 FROM " + Table;
    String DELETE = "DELETE FROM " + Table;

    String _LIMIT = " LIMIT #{limit}";
    String _OFFSET = " OFFSET #{offset}";
    String _LIMIT_OFFSET = _LIMIT + _OFFSET;
    String _WHERE_ID = " WHERE id = #{id}";

    /* ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼ */
    @Select("SELECT id, name from menu where id in (SELECT DISTINCT (parentId) FROM Menu)")
    List<Menu> parentList();

    @Select({
            "SELECT DISTINCT mr.menuId FROM MenuRole mr, UserRole ur",
            "WHERE ur.userId = #{0,jdbcType=INTEGER}",
            "  AND mr.roleId = ur.roleId"
    })
    @Options(useCache = false)
    List<Integer> getMenuIdsByUserId(Serializable userId);
    /* ▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲ */

    @Select(LIST + _WHERE_ID)
    Menu get(Serializable id);

    @Delete(DELETE + _WHERE_ID)
    Integer delete(Serializable id);

    @Insert({
            "insert into",
            Table,
            "(`name`,`url`,`html`,`sequence`,`parentId`,`label`,`scope`,`rank`,`createUserId`)",
            "values",
            "(#{name},#{url},#{html},#{sequence},#{parentId},#{label},#{scope},#{rank},#{createUserId})"
    })
    @Options(useGeneratedKeys = true)
    int insert(Menu record);

    @UpdateProvider(type = MenuSqlProvider.class, method = "update")
    int update(Menu record);

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

    @SelectProvider(type = MenuSqlProvider.class, method = "query")
    List<Menu> query(Menu record);

    @SelectProvider(type = MenuSqlProvider.class, method = "count")
    int count(Menu record);

    class MenuSqlProvider {
        private String queryOrCount(Menu record, boolean query) {
            return new SQL() {{
                SELECT(query ? "*" : "COUNT(*)");
                FROM("MenuView");
                if (record.getName() != null) {
                    WHERE("name like CONCAT('%',#{name},'%')");
                }
                if (record.getParentId() != null) {
                    WHERE("parentId = #{parentId}");
                }
                if (record.getOnlyLeaf() != null) {
                    if (record.getOnlyLeaf()) {
                        WHERE("id not in (select distinct(parentId) from MenuView where parentId is not null)");
                    }
                }
                if (record.getValid() != null) {
                    WHERE("valid = #{valid}");
                }
                if (query) {
                    ORDER_BY("parentId, sequence");
                }
            }}.toString() + (query && record.needPaging() ? _LIMIT_OFFSET : "");
        }

        public String query(Menu record) {
            return queryOrCount(record, true);
        }

        public String count(Menu record) {
            return queryOrCount(record, false);
        }

        public String update(final Menu record) {
            return new SQL() {{
                UPDATE(Table);
                if (record.getName() != null) {
                    SET("`name` = #{name,jdbcType=VARCHAR}");
                }
                if (record.getUrl() != null) {
                    SET("`url` = #{url,jdbcType=VARCHAR}");
                }
                if (record.getHtml() != null) {
                    SET("`html` = #{html,jdbcType=VARCHAR}");
                }
                if (record.getSequence() != null) {
                    SET("`sequence` = #{sequence,jdbcType=INTEGER}");
                }
                if (record.getLabel() != null) {
                    SET("`label` = #{label,jdbcType=VARCHAR}");
                }
                if (record.getRank() != null) {
                    SET("`rank` = #{rank,jdbcType=INTEGER}");
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