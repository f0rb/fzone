package win.doyto.fzone.mapper;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import win.doyto.fzone.model.Perm;

@Mapper
public interface PermMapper {
    String Table = "Perm";
    String LIST = "SELECT * FROM " + Table;
    String HAS = "SELECT COUNT(*) > 0 FROM " + Table;
    String DELETE = "DELETE FROM " + Table;

    String _LIMIT = " LIMIT #{limit}";
    String _OFFSET = " OFFSET #{offset}";
    String _LIMIT_OFFSET = _LIMIT + _OFFSET;
    String _WHERE_ID = " WHERE id = #{id}";

    @Select(LIST + _WHERE_ID)
    Perm get(Serializable id);

    @Delete(DELETE + _WHERE_ID)
    Integer delete(Serializable id);

    @Select(HAS + " WHERE name = #{name}")
    Boolean hasPerm(String name);

    @Insert({
            "insert into Perm (id, name, memo, createUserId, valid)",
            "values (",
            "#{id,jdbcType=VARCHAR}, ",
            "#{name,jdbcType=VARCHAR}, ",
            "#{memo,jdbcType=VARCHAR}, ",
            "#{createUserId,jdbcType=INTEGER}, ",
            "#{valid,jdbcType=BIT}",
            ")"
    })
    int insert(Perm record);

    @Update({
            "update Perm",
            "set name = #{name,jdbcType=VARCHAR},",
            "    memo = #{memo,jdbcType=VARCHAR},",
            "    valid = #{valid,jdbcType=BIT}",
            "where id = #{id,jdbcType=INTEGER}"
    })
    int update(Perm record);

    @SelectProvider(type = PermSqlProvider.class, method = "query")
    List<Perm> query(Perm record);

    @SelectProvider(type = PermSqlProvider.class, method = "count")
    int count(Perm record);

    @SelectProvider(type = PermSqlProvider.class, method = "checkPerms")
    List<String> checkPerms(List<String> list);

    @InsertProvider(type = PermSqlProvider.class, method = "insertAll")
    int insertAll(List<Perm> list);

    @SuppressWarnings("unchecked")
    class PermSqlProvider {
        private String queryOrCount(Perm record, boolean select) {
            return new SQL() {
                {
                    SELECT(select ? "*" : "COUNT(*)");
                    FROM(Table);
                    if (record.getName() != null) WHERE("name like CONCAT('%',#{name},'%')");
                }
            }.toString() + (select && record.needPaging() ? _LIMIT_OFFSET : "");
        }

        public String query(Perm record) {
            return queryOrCount(record, true);
        }

        public String count(Perm record) {
            return queryOrCount(record, false);
        }

        public String insertAll(Map map) {
            List<Perm> PermList = (List<Perm>) map.get("list");
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT IGNORE INTO Perm ");
            sb.append("(id, name, memo, createUserId, valid) ");
            sb.append("VALUES ");
            MessageFormat mf = new MessageFormat("(#'{'list[{0}].id}, #'{'list[{0}].name}, #'{'list[{0}].memo}, #'{'list[{0}].createUserId}, #'{'list[{0}].valid})");

            sb.append(mf.format(new Object[]{0}));
            for (int i = 1; i < PermList.size(); i++) {
                sb.append(",").append(mf.format(new Object[]{i}));
            }
            return sb.toString();
        }

        public String checkPerms(Map map) {
            List<String> Perms = (List<String>) map.get("list");
            return new SQL() {
                {
                    SELECT("id");
                    FROM(Table);
                    MessageFormat mf = new MessageFormat(",#'{'list[{0}]}");

                    StringBuilder sb = new StringBuilder();
                    sb.append("(").append("#{list[0]}");
                    for (int i = 1; i < Perms.size(); i++) {
                        sb.append(mf.format(new Object[]{i}));
                    }
                    sb.append(")");
                    WHERE("id in " + sb.toString());
                }
            }.toString();
        }
    }
}