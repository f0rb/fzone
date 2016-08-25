package win.doyto.fzone.mapper;

import java.io.Serializable;
import java.util.List;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import win.doyto.fzone.model.Role;
import win.doyto.fzone.model.User;

/**
 * 有关User表的处理
 *
 * @author Yuanzhen on 2013-01-05
 * @version 0.0.1
 */
@Mapper
@CacheNamespace(implementation = org.mybatis.caches.hazelcast.HazelcastCache.class)
public interface UserMapper {
    String Table = "User";
    String LIST = "SELECT * FROM " + Table;
    String HAS = "SELECT COUNT(*) > 0 FROM " + Table;
    String DELETE = "DELETE FROM " + Table;

    String _LIMIT = " LIMIT #{limit}";
    String _OFFSET = " OFFSET #{offset}";
    String _LIMIT_OFFSET = _LIMIT + _OFFSET;
    String _WHERE_ID = " WHERE id = #{id}";
    String WHERE_USERNAME = " WHERE username = #{username}";

    @Select(LIST + _WHERE_ID)
    User get(Serializable id);

    @Delete(DELETE + _WHERE_ID)
    Integer delete(Serializable id);

    @Insert({
            "INSERT IGNORE INTO User ",
            "(username, password, nickname, email, mobile, rank, lastIp, online, emailFlag, token, lastActive, lastReset, lastLogin)",
            " VALUES ",
            "(#{username}, #{password}, #{nickname}, #{email}, #{mobile}, #{rank}, #{lastIp}, #{online}, #{emailFlag}, #{token}, #{lastActive}, #{lastReset}, #{lastLogin})"
    })
    @Options(useGeneratedKeys = true)
    Integer insert(User user);

    @SelectProvider(type = UserSqlProvider.class, method = "select")
    List<User> query(User user);

    @SelectProvider(type = UserSqlProvider.class, method = "count")
    Long count(User user);

    @Select(LIST + WHERE_USERNAME)
    User getByUsername(String username);

    @Select(LIST + " WHERE email = #{email}")
    User getByEmail(String email);

    @Update("UPDATE User SET lastIp = #{lastIp}, lastLogin = #{lastLogin}, online = online + 1 " + _WHERE_ID)
    Integer login(User user);

    @Update("UPDATE User SET online = online - 1 " + _WHERE_ID)
    Integer logout(Integer id);

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

    @Select(LIST + " WHERE token = #{token}")
    User getByToken(String token);

    @Select("SELECT u.*, u.id AS userId FROM User u" + WHERE_USERNAME)
    @Results(value = @Result(id = true, column = "userId", property = "roles", javaType = List.class,
            many = @Many(select = "listRolesByUserId")))
    User getByUsernameWithRoles(String username);

    @Select("SELECT u.*, u.id AS userId FROM User u" + _WHERE_ID)
    @Results(value = @Result(id = true, column = "userId", property = "roles", javaType = List.class,
            many = @Many(select = "listRolesByUserId")))
    User getWithRoles(Integer id);

    @Select("SELECT r.* FROM Role r INNER JOIN UserRole ur ON r.id = ur.roleId WHERE userId = #{userId}")
    List<Role> listRolesByUserId(Integer userId);

    @InsertProvider(type = UserSqlProvider.class, method = "insertSelective")
    @Options(useGeneratedKeys = true)
    int insertSelective(User user);

    @UpdateProvider(type = UserSqlProvider.class, method = "updateByIdSelective")
    int updateByIdSelective(User user);

    class UserSqlProvider {
        public String insertSelective(User record) {
            return new SQL() {
                {
                    INSERT_INTO("User");
                    if (record.getId() != null) {
                        VALUES("id", "#{id,jdbcType=INTEGER}");
                    }
                    if (record.getUsername() != null) {
                        VALUES("username", "#{username,jdbcType=VARCHAR}");
                    }
                    if (record.getPassword() != null) {
                        VALUES("password", "#{password,jdbcType=VARCHAR}");
                    }
                    if (record.getRank() != null) {
                        VALUES("rank", "#{rank,jdbcType=INTEGER}");
                    }
                    if (record.getNickname() != null) {
                        VALUES("nickname", "#{nickname,jdbcType=VARCHAR}");
                    }
                    if (record.getEmail() != null) {
                        VALUES("email", "#{email,jdbcType=VARCHAR}");
                    }
                    if (record.getMobile() != null) {
                        VALUES("mobile", "#{mobile,jdbcType=VARCHAR}");
                    }
                    if (record.getLastLogin() != null) {
                        VALUES("lastLogin", "#{lastLogin,jdbcType=TIMESTAMP}");
                    }
                    if (record.getLastActive() != null) {
                        VALUES("lastActive", "#{lastActive,jdbcType=TIMESTAMP}");
                    }
                    if (record.getLastReset() != null) {
                        VALUES("lastReset", "#{lastReset,jdbcType=TIMESTAMP}");
                    }
                    if (record.getLastIp() != null) {
                        VALUES("lastIp", "#{lastIp,jdbcType=VARCHAR}");
                    }
                    if (record.getOnline() != null) {
                        VALUES("online", "#{online,jdbcType=INTEGER}");
                    }
                    if (record.getEmailFlag() != null) {
                        VALUES("emailFlag", "#{emailFlag,jdbcType=BIT}");
                    }
                    if (record.getToken() != null) {
                        VALUES("token", "#{token,jdbcType=VARCHAR}");
                    }
                    if (record.getScore() != null) {
                        VALUES("score", "#{score,jdbcType=INTEGER}");
                    }
                    if (record.getCreateTime() != null) {
                        VALUES("createTime", "#{createTime,jdbcType=TIMESTAMP}");
                    }
                    if (record.getUpdateTime() != null) {
                        VALUES("updateTime", "#{updateTime,jdbcType=TIMESTAMP}");
                    }
                    if (record.getUpdateUserId() != null) {
                        VALUES("updateUserId", "#{updateUserId,jdbcType=INTEGER}");
                    }
                    if (record.getValid() != null) {
                        VALUES("valid", "#{valid,jdbcType=BIT}");
                    }
                }
            }.toString();
        }

        public String updateByIdSelective(User record) {
            return new SQL() {
                {
                    UPDATE("User");
                    if (record.getPassword() != null) {
                        SET("password = #{password,jdbcType=VARCHAR}");
                    }
                    if (record.getNickname() != null) {
                        SET("nickname = #{nickname,jdbcType=VARCHAR}");
                    }
                    if (record.getEmail() != null) {
                        SET("email = #{email,jdbcType=VARCHAR}");
                    }
                    if (record.getMobile() != null) {
                        SET("mobile = #{mobile,jdbcType=VARCHAR}");
                    }
                    if (record.getRank() != null) {
                        SET("rank = #{rank,jdbcType=INTEGER}");
                    }
                    if (record.getLastLogin() != null) {
                        SET("lastLogin = #{lastLogin,jdbcType=TIMESTAMP}");
                    }
                    if (record.getLastActive() != null) {
                        SET("lastActive = #{lastActive,jdbcType=TIMESTAMP}");
                    }
                    if (record.getLastReset() != null) {
                        SET("lastReset = #{lastReset,jdbcType=TIMESTAMP}");
                    }
                    if (record.getLastIp() != null) {
                        SET("lastIp = #{lastIp,jdbcType=VARCHAR}");
                    }
                    if (record.getOnline() != null) {
                        SET("online = #{online,jdbcType=INTEGER}");
                    }
                    if (record.getEmailFlag() != null) {
                        SET("emailFlag = #{emailFlag,jdbcType=BIT}");
                    }
                    if (record.getToken() != null) {
                        SET("token = #{token,jdbcType=VARCHAR}");
                    }
                    if (record.getScore() != null) {
                        SET("score = #{score,jdbcType=INTEGER}");
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
                }
            }.toString();
        }

        private String selectOrCount(User record, boolean select) {
            return new SQL() {
                {
                    SELECT(select ? "t.*, t.id AS userId" : "COUNT(*)");
                    FROM(Table + " t");
                    if (record.getValid() != null) {
                        WHERE("valid = #{valid}");
                    } else {
                        WHERE("valid = true");
                    }
                    if (record.getUsername() != null) {
                        WHERE("username like CONCAT('%',#{username},'%')");
                    }
                    if (record.getNickname() != null) {
                        WHERE("nickname like CONCAT('%',#{name},'%')");
                    }
                    if (record.getEmail() != null) {
                        WHERE("email = #{email}");
                    }
                    if (record.getMobile() != null) {
                        WHERE("mobile = #{mobile}");
                    }
                    if (record.getToken() != null) {
                        WHERE("token = #{token}");
                    }
                    if (record.getRankGt() != null) {
                        WHERE("rank > #{rankGt}");
                    }
                    if (record.getRoles() != null && !record.getRoles().isEmpty()) {
                        StringBuilder roleId = new StringBuilder("(");
                        for (Role role : record.getRoles()) {
                            roleId.append(role.getId()).append(",");
                        }
                        roleId.deleteCharAt(roleId.length() - 1).append(")");
                        WHERE("id in (select userId from UserRole where roleId in " + roleId.toString() + ")");
                    }
                }
            }.toString() + (select && record.needPaging() ? _LIMIT_OFFSET : "");
        }

        public String select(User record) {
            return selectOrCount(record, true);
        }

        public String count(User record) {
            return selectOrCount(record, false);
        }
    }
}
