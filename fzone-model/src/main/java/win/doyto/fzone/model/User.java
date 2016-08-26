package win.doyto.fzone.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import win.doyto.fzone.common.PageableModel;
import win.doyto.rbac.RBACUser;

public class User extends PageableModel<User> implements RBACUser<Role> {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String username;
    private String password;
    private String email;
    private Boolean emailFlag; //是否绑定邮箱
    private String mobile;
    private Boolean mobileFlag; //是否绑定手机
    private Short rank;
    private String nickname;
    private String lastIp;
    private Integer online;
    private String token; //激活码
    private Timestamp createTime; //注册时间
    private Timestamp lastActive; //上次激活请求
    private Timestamp lastReset; //上次发送密码请求
    private Timestamp lastLogin; //最后登录时间
    private Integer score; //积分
    private Date updateTime;
    private Integer updateUserId;
    private Boolean valid;
    private List<Role> roles;
    private Short rankGt;

    private synchronized List<Role> internalGetRoles() {
        if (roles == null) roles = new ArrayList<Role>();
        return roles;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public void setRoleId(Integer roleId) {
        internalGetRoles().add(new Role(roleId));
    }

    public User toSessionUser() {
        User o = new User();
        o.id = id;
        o.username = username;
        o.nickname = nickname;
        o.lastLogin = lastLogin;
        o.email = email;
        o.mobile = mobile;
        o.emailFlag = emailFlag;
        o.rank = rank;
        o.roles = new ArrayList<>(getRoles());//显示调用getRoles, 解决MyBatis懒加载的问题
        return o;
    }

    /**
     * 返回一个初始化好数据的model对象，用于数据库插入
     *
     * @return T 待插入数据库的model对象
     */
    @Override
    public User toInsertModel() {
        User user = super.toInsertModel();
        user.lastActive = new Timestamp(System.currentTimeMillis());
        user.emailFlag = true;
        user.online = 0;
        user.score = 0;
        user.token = UUID.randomUUID().toString().toUpperCase();
        return user;
    }

    @Override
    public void fillBy(User user) {
        if (user == this || user == null) return;
        id = user.id;
        username = user.username;
        password = user.password;
        email = user.email;
        mobile = user.mobile;
        nickname = user.nickname;
        emailFlag = user.emailFlag;
        lastLogin = user.lastLogin;
        online = user.online;
        roles = user.roles;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getEmailFlag() {
        return emailFlag;
    }

    public void setEmailFlag(Boolean emailFlag) {
        this.emailFlag = emailFlag;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Boolean getMobileFlag() {
        return mobileFlag;
    }

    public void setMobileFlag(Boolean mobileFlag) {
        this.mobileFlag = mobileFlag;
    }

    public Short getRank() {
        return rank;
    }

    public void setRank(Short rank) {
        this.rank = rank;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getLastIp() {
        return lastIp;
    }

    public void setLastIp(String lastIp) {
        this.lastIp = lastIp;
    }

    public Integer getOnline() {
        return online;
    }

    public void setOnline(Integer online) {
        this.online = online;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getLastActive() {
        return lastActive;
    }

    public void setLastActive(Timestamp lastActive) {
        this.lastActive = lastActive;
    }

    public Timestamp getLastReset() {
        return lastReset;
    }

    public void setLastReset(Timestamp lastReset) {
        this.lastReset = lastReset;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(Integer updateUserId) {
        this.updateUserId = updateUserId;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public Short getRankGt() {
        return rankGt;
    }

    public void setRankGt(Short rankGt) {
        this.rankGt = rankGt;
    }
}
