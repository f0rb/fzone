package win.doyto.fzone.mapper;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import win.doyto.fzone.model.Post;

@Mapper
@CacheNamespace(implementation = org.mybatis.caches.hazelcast.HazelcastCache.class)
public interface PostMapper {
    String Table = "Post";
    String LIST = "SELECT * FROM " + Table;
    String HAS = "SELECT COUNT(*) > 0 FROM " + Table;
    String DELETE = "DELETE FROM " + Table;

    String _LIMIT = " LIMIT #{limit}";
    String _OFFSET = " OFFSET #{offset}";
    String _LIMIT_OFFSET = _LIMIT + _OFFSET;
    String _WHERE_ID = " WHERE id = #{id}";

    @Select(LIST + _WHERE_ID)
    Post get(Serializable id);

    @Delete(DELETE + _WHERE_ID)
    Integer delete(Serializable id);

    @Insert({
            "insert into",
            Table,
            "(`id`,`content`,`locked`,`preview`,`title`,`userId`,`categoryId`,`zoneId`,`nileId`,`state`)",
            "values",
            "(#{id},#{content},#{locked},#{preview},#{title},#{userId},#{categoryId},#{zoneId},#{nileId},#{state})"
    })
    @Options
    int insert(Post record);

    @UpdateProvider(type = PostSqlProvider.class, method = "update")
    int update(Post record);

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

    @SelectProvider(type = PostSqlProvider.class, method = "query")
    List<Post> query(Post record);

    @SelectProvider(type = PostSqlProvider.class, method = "count")
    int count(Post record);

    class PostSqlProvider {
        private String queryOrCount(Post record, boolean select) {
            return new SQL() {
                {
                    SELECT(select ? "*" : "COUNT(*)");
                    FROM("PostView");
                    if (record.getTitle() != null) {
                        WHERE("title like CONCAT('%',#{title},'%')");
                    }
                    if (record.getUserId() != null) {
                        WHERE("userId = #{userId}");
                    }
                    if (record.getState() != null) {
                        WHERE("state = #{state}");
                    } else {
                        WHERE("state != 'NEW'");//不查状态为NEW的记录
                    }
                }
            }.toString() + (select && record.needPaging() ? _LIMIT_OFFSET : "");
        }

        public String query(Post record) {
            return queryOrCount(record, true);
        }

        public String count(Post record) {
            return queryOrCount(record, false);
        }

        public String update(final Post record) {
            return new SQL() {
                {
                    UPDATE(Table);
                    if (record.getContent() != null) {
                        SET("`content` = #{content,jdbcType=VARCHAR}");
                    }
                    if (record.getIview() != null) {
                        SET("`iview` = #{iview,jdbcType=INTEGER}");
                    }
                    if (record.getLocked() != null) {
                        SET("`locked` = #{locked,jdbcType=VARCHAR}");
                    }
                    if (record.getPreview() != null) {
                        SET("`preview` = #{preview,jdbcType=VARCHAR}");
                    }
                    if (record.getTitle() != null) {
                        SET("`title` = #{title,jdbcType=VARCHAR}");
                    }
                    if (record.getCategoryId() != null) {
                        SET("`categoryId` = #{categoryId,jdbcType=INTEGER}");
                    }
                    if (record.getZoneId() != null) {
                        SET("`zoneId` = #{zoneId,jdbcType=INTEGER}");
                    }
                    if (record.getState() != null) {
                        SET("`state` = #{state,jdbcType=VARCHAR}");
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