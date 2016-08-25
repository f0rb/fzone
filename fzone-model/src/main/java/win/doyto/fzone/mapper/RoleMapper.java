package win.doyto.fzone.mapper;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import win.doyto.fzone.model.Role;
import win.doyto.rbac.RBACManager;
import win.doyto.rbac.RBACPermission;

@Mapper
//@CacheNamespace(implementation = org.mybatis.caches.hazelcast.HazelcastCache.class)
@CacheNamespace(implementation = org.mybatis.caches.hazelcast.LoggingHazelcastCache.class)
public interface RoleMapper extends RBACManager<Role> {
    String Table = "Role";
    String LIST = "SELECT * FROM " + Table;
    String HAS = "SELECT COUNT(*) > 0 FROM " + Table;
    String DELETE = "DELETE FROM " + Table;

    String _LIMIT = " LIMIT #{limit}";
    String _OFFSET = " OFFSET #{offset}";
    String _LIMIT_OFFSET = _LIMIT + _OFFSET;
    String _WHERE_ID = " WHERE id = #{id}";

    @Select(LIST + _WHERE_ID)
    Role get(Serializable id);

    @Select(LIST + " WHERE code = #{code}")
    Role getByCode(Serializable code);

    @Select("SELECT id, name, rank FROM Role WHERE rank > #{rank} ORDER BY rank")
    List<Role> listRoleByRank(Short rank);

    @Select(HAS + _WHERE_ID)
    Boolean has(Serializable id);

    @Select(HAS + " WHERE name = #{name} or code = #{code}")
    boolean hasRole(Role role);

    @Select(HAS + " WHERE name = #{name}")
    Boolean hasRoleName(String name);

    @Select(HAS + " WHERE code = #{code}")
    Boolean hasRoleCode(String code);

    @Delete({
            "delete from Role",
            "where id = #{id,jdbcType=INTEGER}"
    })
    int delete(Integer id);

    @Insert({
            "insert into Role (id, name, rank",
            "createTime, createUserId, ",
            "updateTime, updateUserId, ",
            "valid)",
            "values (#{id,jdbcType=INTEGER}, #{name,jdbcType=VARCHAR}, #{rank,jdbcType=SMALLINT}, ",
            "#{createTime,jdbcType=TIMESTAMP}, #{createUserId,jdbcType=INTEGER}, ",
            "#{updateTime,jdbcType=TIMESTAMP}, #{updateUserId,jdbcType=INTEGER}, ",
            "#{valid,jdbcType=BIT})"
    })
    @Options(useGeneratedKeys = true)
    int insert(Role record);


    @Select("select rank from Role where id = #{id}")
    Short getRankById(Integer id);

    @UpdateProvider(type = RoleSqlProvider.class, method = "updateByIdSelective")
    int updateByIdSelective(Role record);

    @Select("SELECT COUNT(*) > 0 FROM Perm WHERE id = #{0}")
    boolean hasPermission(String permission);

    @Select("SELECT COUNT(*) > 0 FROM RolePerm WHERE role = #{0} AND perm = #{1}")
    boolean checkIfRoleHasPermission(String role, String perm);

    @Insert("INSERT INTO RolePerm (role, perm) VALUES (#{0}, #{1})")
    int grantPermissionToRole(String role, String permission);

    @Delete("DELETE FROM RolePerm WHERE role = #{0} AND perm = #{1}")
    void revokePermissionFromRole(String role, String permission);

    @Delete("DELETE FROM RolePerm WHERE role = #{0}")
    void revokePermissionByRole(String role);

    @Delete("DELETE FROM Perm WHERE id = #{0}")
    void removePermission(String permission);

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
    int insertPermission(RBACPermission permission);

    @InsertProvider(type = RoleSqlProvider.class, method = "insertSelective")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertRole(Role role);

    @Delete("DELETE FROM Role WHERE id = #{0}")
    void removeRole(String role);

    @Update({
            "update Role",
            "set name = #{name,jdbcType=VARCHAR},",
            "rank = #{rank,jdbcType=SMALLINT},",
            "memo = #{memo,jdbcType=VARCHAR},",
            "updateTime = #{updateTime,jdbcType=TIMESTAMP},",
            "updateUserId = #{updateUserId,jdbcType=INTEGER},",
            "valid = #{valid,jdbcType=BIT}",
            "where id = #{id,jdbcType=INTEGER}"
    })
    int updateRole(Role record);

    @Select("SELECT id FROM perm")
    @Options(flushCache = Options.FlushCachePolicy.TRUE)
    List<String> listPermission();

    @Select(LIST + " ORDER BY rank")
    List<Role> listRole();

    @Select("SELECT CONCAT(role, '-', perm) AS RP FROM RolePerm")
    Set<String> listRolePermission();

    @Insert("INSERT INTO UserRole (userId, roleId, createUserId) VALUES (#{0}, #{1}, #{2})")
    Integer assignRole(Integer userId, Integer roleId, Integer createUserId);

    //@Delete("DELETE FROM UserRole WHERE (userId = #{0} AND roleId = #{1})")
    //Integer removeRole(Integer userId, Integer roleId);

    @Delete("DELETE FROM UserRole WHERE userId = #{0}")
    Integer removeRolesByUserId(Integer userId);

    @Select("SELECT roleId FROM UserRole WHERE userId = #{0} LIMIT 1")
    Integer getRoleIdByUserId(Integer userId);

    @SelectProvider(type = RoleSqlProvider.class, method = "query")
    List<Role> query(Role record);

    @SelectProvider(type = RoleSqlProvider.class, method = "count")
    int count(Role record);

    //角色菜单管理
    @Delete("DELETE FROM MenuRole WHERE roleId = #{0}")
    Integer removeMenusByRoleId(String roleId);

    @Insert("INSERT INTO MenuRole (menuId, roleId, createUserId) VALUES (#{0}, #{1}, #{2})")
    Integer assignMenu(Serializable menuId, Serializable roleId, Integer createUserId);
    //角色菜单管理

    @Select("SELECT menuId FROM MenuRole WHERE roleId = #{0}")
    List<Integer> listMenuIdsByRoleId(Serializable roleId);

    class RoleSqlProvider {
        private String queryOrCount(final Role record, boolean select) {
            return new SQL() {{
                SELECT(select ? "*" : "COUNT(*)");
                FROM(RoleMapper.Table);
                if (record.getName() != null) {
                    WHERE("name like CONCAT('%',#{name},'%')");
                }
                ORDER_BY("rank");
            }}.toString() + (select && record.needPaging() ? _LIMIT_OFFSET : "");
        }

        public String query(Role record) {
            return queryOrCount(record, true);
        }

        public String count(Role record) {
            return queryOrCount(record, false);
        }

        public String insertSelective(Role record) {
            return new SQL() {{
                INSERT_INTO("Role");
                if (record.getId() != null) {
                    VALUES("id", "#{id,jdbcType=INTEGER}");
                }
                if (record.getCode() != null) {
                    VALUES("code", "#{code,jdbcType=VARCHAR}");
                }
                if (record.getName() != null) {
                    VALUES("name", "#{name,jdbcType=VARCHAR}");
                }
                if (record.getRank() != null) {
                    VALUES("rank", "#{rank,jdbcType=SMALLINT}");
                }
                if (record.getMemo() != null) {
                    VALUES("memo", "#{memo,jdbcType=VARCHAR}");
                }
                if (record.getCreateUserId() != null) {
                    VALUES("createUserId", "#{createUserId,jdbcType=INTEGER}");
                }
                if (record.getValid() != null) {
                    VALUES("valid", "#{valid,jdbcType=BIT}");
                }
            }}.toString();
        }

        public String updateByIdSelective(Role record) {
            return new SQL() {{
                UPDATE("Role");
                if (record.getName() != null) {
                    SET("name = #{name,jdbcType=VARCHAR}");
                }
                if (record.getRank() != null) {
                    SET("rank = #{rank,jdbcType=SMALLINT}");
                }
                if (record.getCode() != null) {
                    SET("code = #{code,jdbcType=VARCHAR}");
                }
                if (record.getMemo() != null) {
                    SET("memo = #{memo,jdbcType=VARCHAR}");
                }
                if (record.getUpdateTime() != null) {
                    SET("updateTime = #{updateTime,jdbcType=TIMESTAMP}");
                }
                if (record.getUpdateUserId() != null) {
                    SET("updateUserId = #{updateUserId,jdbcType=INTEGER}");
                }
                if (record.getValid() != null) {
                    SET("valid = #{valid,jdbcType=BIT}");
                }
                WHERE("id = #{id,jdbcType=INTEGER}");
            }}.toString();
        }
    }
}