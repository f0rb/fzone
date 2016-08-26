CREATE DATABASE IF NOT EXISTS `fzone` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `fzone`;

DROP TABLE IF EXISTS fzone.`Perm`;
CREATE TABLE fzone.Perm
(
    id CHAR(8) NOT NULL,
    name VARCHAR(100) NOT NULL COMMENT '通过权限名称进行过滤',
    memo VARCHAR(200),
    createTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    createUserId INT(11) NOT NULL,
    valid TINYINT(1) DEFAULT '1' NOT NULL,
    PRIMARY KEY (`id`)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS fzone.`Role`;
CREATE TABLE fzone.`Role` (
    id INT(11) NOT NULL AUTO_INCREMENT,
    code VARCHAR(31) COMMENT '角色编码, 预置角色有值',
    name VARCHAR(31) NOT NULL COMMENT '角色名',
    rank SMALLINT(6) DEFAULT '32767' NOT NULL,
    memo VARCHAR(200),
    createTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    createUserId INT(11),
    updateTime DATETIME,
    updateUserId INT(11),
    valid TINYINT(1) DEFAULT '1' NOT NULL,
    PRIMARY KEY (`id`)
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8
    COMMENT = '用户的角色';
CREATE INDEX Role_code_index ON Role (code);
CREATE INDEX Role_rank_index ON Role (rank);

DROP TABLE IF EXISTS fzone.`User`;
CREATE TABLE fzone.`User`
(
    id INT(11) NOT NULL AUTO_INCREMENT,
    username VARCHAR(31) NOT NULL,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(31) NOT NULL,
    email VARCHAR(100),
    mobile VARCHAR(20),
    lastLogin DATETIME,
    lastActive DATETIME,
    lastReset DATETIME,
    lastIp VARCHAR(63),
    online INT(11),
    emailFlag BIT(1),
    token VARCHAR(63),
    score INT(11),
    createTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updateTime DATETIME,
    updateUserId INT(11),
    valid TINYINT(1) DEFAULT '1',
    createUserId INT(11),
    rank SMALLINT(6) DEFAULT '32767' NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `nickname` (`nickname`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `mobile` (`mobile`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS fzone.`RolePerm`;
CREATE TABLE fzone.`RolePerm` (
    `role` INT(11)     NOT NULL,
    `perm` VARCHAR(50) NOT NULL,
    PRIMARY KEY (role, perm),
    KEY `fk_rp_perm` (perm),
    KEY `fk_rp_role` (role),
    CONSTRAINT `fk_rp_perm` FOREIGN KEY (perm) REFERENCES `Perm` (`id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT `fk_rp_role` FOREIGN KEY (role) REFERENCES `Role` (`id`)
        ON DELETE CASCADE
        ON UPDATE CASCADE
)
    ENGINE = InnoDB
    DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS fzone.`UserRole`;
CREATE TABLE fzone.`UserRole` (
  `roleId` INT(11) NOT NULL,
  `userId` INT(11) NOT NULL,
  PRIMARY KEY (`roleId`, `userId`),
  KEY `fk_ur_role` (`roleId`),
  KEY `fk_ur_user` (`userId`),
  CONSTRAINT `fk_ur_role` FOREIGN KEY (`roleId`) REFERENCES `role` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_ur_user` FOREIGN KEY (`userId`) REFERENCES `user` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS fzone.`Feedback`;
CREATE TABLE fzone.`Feedback` (
  `id`         INT(11)   NOT NULL AUTO_INCREMENT,
  `createTime` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `userId`     INT(11)            DEFAULT NULL,
  `name`       VARCHAR(63)        DEFAULT NULL,
  `email`      VARCHAR(63)        DEFAULT NULL,
  `tel`        VARCHAR(63)        DEFAULT NULL,
  `content`    TEXT,
  `reply`      TEXT,
  `replyTime`  TIMESTAMP NULL     DEFAULT NULL
  COMMENT '客服回复反馈的时间',
  PRIMARY KEY (`id`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8
  COMMENT = '用户反馈意见';


DROP TABLE IF EXISTS fzone.`Order`;
CREATE TABLE fzone.`Order` (
  `id`            BIGINT(20)     NOT NULL,
  `createTime`    TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP
  COMMENT '订单创建时间',
  `signed`        BIT(1)                  DEFAULT NULL,
  `paid`          BIT(1)         NOT NULL
  COMMENT '是否已付款',
  `shipped`       BIT(1)         NOT NULL
  COMMENT '是否已发货',
  `expireDate`    DATETIME                DEFAULT NULL
  COMMENT '订单过期时间',
  `amount`        DECIMAL(10, 2) NOT NULL
  COMMENT '订单总金额',
  `status`        TINYINT(4)     NOT NULL,
  `userId`        INT(11)        NOT NULL,
  `username`      VARCHAR(31)    NOT NULL
  COMMENT '用户名',
  `currency`      VARCHAR(3)     NOT NULL,
  `memo`          VARCHAR(255)            DEFAULT NULL
  COMMENT '备注有问题的订单',
  `transactionID` VARCHAR(31)             DEFAULT NULL
  COMMENT 'Patpal Transaction ID, 17位',
  PRIMARY KEY (`id`),
  UNIQUE KEY `createTime_UNIQUE` (`createTime`),
  KEY `idx_id_userId_paid` (id, `userId`, `paid`)
)
  ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

DROP TABLE IF EXISTS fzone.Menu;
CREATE TABLE fzone.Menu
(
    id INT(11)  NOT NULL AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL,
    url VARCHAR(100),
    html VARCHAR(50),
    sequence SMALLINT(6) DEFAULT '0',
    parentId INT(11),
    label VARCHAR(100),
    scope ENUM('ADMIN_ASIDE', 'DASHBOARD_ASIDE', 'DASHBOARD_NAV') DEFAULT 'ADMIN_ASIDE',
    rank SMALLINT(6) DEFAULT '1',
    createTime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    createUserId INT(11),
    updateTime DATETIME,
    updateUserId INT(11),
    valid TINYINT(1) DEFAULT '1' NOT NULL,
    PRIMARY KEY(id)
);

CREATE VIEW fzone.MenuView AS
    SELECT  m.*, p.name AS parentName,
                 cu.username AS createUserName ,
                 uu.username AS updateUserName
    FROM Menu2 m
        LEFT JOIN Menu2 p ON m.parentId = p.id
        LEFT JOIN User cu ON m.createUserId = cu.id
        LEFT JOIN User uu ON m.updateUserId = uu.id;


INSERT INTO fzone.Menu
(id, name, url, sequence, parentId, createTime, createUserId, updateTime, updateUserId, valid, label, scope, rank, html)
VALUES
    (1, 'admin', '/admin', 1, 0, '2016-08-22 00:11:19', 1, null, null, 1, '系统管理', 'ADMIN_ASIDE', 1, null),
    (2, '系统配置', '#', 1, 0, '2016-05-13 23:35:55', 1, '2016-08-22 12:44:51', 1, 1, '系统配置', 'ADMIN_ASIDE', 1, null),
    (3, '系统管理', '#', 2, 0, '2016-06-27 22:04:58', 1, '2016-08-22 12:45:04', 1, 1, '系统管理', 'ADMIN_ASIDE', 1, null),
    (4,'role', 'role', 3, 1, '2016-05-13 23:35:55', 1, '2016-06-27 22:07:44', 1, 1, '角色管理', 'ADMIN_ASIDE', 1, null),
    (5,'perm', 'perm', 4, 1, '2016-05-13 23:35:55', 1, null, null, 1, '权限管理', 'ADMIN_ASIDE', 1, null),
    (6,'menu', 'menu', 2, 2, '2016-05-13 23:35:55', 1, '2016-06-27 22:08:27', 1, 1, '菜单管理', 'ADMIN_ASIDE', 1, null),
    (7,'user', 'user', 1, 2, '2016-05-13 23:35:55', 1, '2016-06-30 21:09:47', 1, 1, '用户管理', 'ADMIN_ASIDE', 1, null),
    (8,'blog', '#', 3, 0, '2016-05-13 23:35:55', 1, '2016-08-21 19:46:23', 1, 1, '博客管理', 'ADMIN_ASIDE', 1, null),
    (9, 'dict', 'dict', 6, 1, '2016-05-14 13:36:00', 1, '2016-05-25 20:24:04', 1, 1, '字典管理', 'ADMIN_ASIDE', 1, null),
    (10,'post', 'post', 1, 10, '2016-05-26 16:12:20', 1, '2016-08-21 19:59:15', 1, 1, '文章列表', 'ADMIN_ASIDE', 1, null),
    (11,'category', 'category', 3, 10, '2016-07-10 18:54:03', 1, '2016-07-10 18:58:24', 1, 1, '分类管理', 'ADMIN_ASIDE', 1, null),
    (12,'dashboard', '#', 1, 0, '2016-08-21 23:58:03', 1, '2016-08-22 00:04:40', 1, 1, '用户管理面板', 'DASHBOARD_ASIDE', 1, null),
    (13,'config', '#', 1, 31, '2016-08-22 12:47:32', 1, null, null, 1, null, null, 1, null);

DROP TABLE IF EXISTS fzone.`Dict`;
CREATE TABLE fzone.Dict
(
    id           INT(11)     NOT NULL          AUTO_INCREMENT,
    name         VARCHAR(50) NOT NULL,
    parentId     INT(11),
    rank         SMALLINT    NOT NULL          DEFAULT 32767,
    `key`        VARCHAR(255),
    value        VARCHAR(255),
    memo         VARCHAR(255),
    leaf         BOOLEAN                       DEFAULT TRUE,

    sequence     SMALLINT                      DEFAULT 0,
    asDefault    BOOLEAN                       DEFAULT FALSE,

    createTime   TIMESTAMP   NOT NULL          DEFAULT CURRENT_TIMESTAMP,
    createUserId INT(11)     NOT NULL,
    updateTime   TIMESTAMP NULL,
    updateUserId INT(11),
    valid        BOOLEAN     NOT NULL          DEFAULT TRUE,
    PRIMARY KEY (`id`)
);

CREATE TABLE fzone.`Zone` (
    id           INT(11)           NOT NULL          AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL,
    userId     INTEGER     NOT NULL,
    css        VARCHAR(50),
    lang       VARCHAR(50),
    title      VARCHAR(100),
    uri        VARCHAR(50) NOT NULL,
    iView      INTEGER DEFAULT 0,
    level      INTEGER,
    popularity INTEGER,
    portrait   VARCHAR(50),

    createTime   TIMESTAMP   NOT NULL          DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);


DROP TABLE IF EXISTS fzone.Post ;
CREATE TABLE fzone.Post
(
    id varchar(20) NOT NULL,
    content LONGTEXT,
    iview INT(11) DEFAULT '0' NOT NULL,
    locked BIT(1),
    preview VARCHAR(512),
    title VARCHAR(45),
    state ENUM('NEW', 'DRAFT', 'PUB'),

    nileId       CHAR(32),
    userId       INTEGER           NOT NULL,
    categoryId   INTEGER           NOT NULL,
    zoneId       INTEGER           NOT NULL,

    createTime   TIMESTAMP   NOT NULL          DEFAULT CURRENT_TIMESTAMP,
    updateTime   DATETIME,
    PRIMARY KEY (`id`)
);

DROP VIEW IF EXISTS fzone.PostView ;
CREATE VIEW fzone.PostView AS
    SELECT
        p.*,
        u.nickname AS author,
        c.name AS category
    FROM Post p
        LEFT JOIN User u ON p.userId = u.id
        LEFT JOIN Category c ON p.categoryId = c.id;


CREATE TABLE fzone.Category (
    id           INT(11)     NOT NULL          AUTO_INCREMENT,
    name        VARCHAR(45) NOT NULL,
    memo    VARCHAR(225),
    zoneId      INTEGER     NOT NULL,
    createTime   TIMESTAMP   NOT NULL          DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);
CREATE TABLE fzone.Comment (
    id           INT(11)     NOT NULL          AUTO_INCREMENT,
    author    VARCHAR(45) NOT NULL,
    content   VARCHAR(45) NOT NULL,
    email     VARCHAR(45),
    forbidden BIT(1),
    home   VARCHAR(45),
    articleid INTEGER     NOT NULL,
    zoneId    INTEGER     NOT NULL,

    createTime   TIMESTAMP   NOT NULL          DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
);


DROP TABLE IF EXISTS fzone.Nile ;
CREATE TABLE fzone.Nile (
    id         CHAR(32)  NOT NULL,
    parentId   CHAR(32),
    name       VARCHAR(1000),
    type       SMALLINT,
    size       INT,
    ownerId    INT       NOT NULL,
    md5       VARCHAR(1000),
    sha1       VARCHAR(1000),
    mime       VARCHAR(100),

    createTime TIMESTAMP NOT NULL          DEFAULT CURRENT_TIMESTAMP,
    updateTime DATETIME,
    PRIMARY KEY (`id`)
)

