USE sharemount;

CREATE TABLE IF NOT EXISTS admin
(
    id              BIGINT UNSIGNED NOT NULL    AUTO_INCREMENT  COMMENT 'ID',
    account         VARCHAR(16)     NOT NULL                    COMMENT '管理员登录名',
    password        CHAR(64)        NOT NULL                    COMMENT '密码（经SHA-256）',
    create_time     TIMESTAMP       NOT NULL                    COMMENT '创建时间',
    update_time     TIMESTAMP       NOT NULL                    COMMENT '修改时间',
    is_deleted      BOOLEAN         NOT NULL    DEFAULT 0       COMMENT '是否删除',

    CONSTRAINT      pk_id           PRIMARY KEY (id),
    CONSTRAINT      uk_account      UNIQUE KEY (account)
) ENGINE=InnoDB DEFAULT CHARSET=utf8                            COMMENT '超级管理员表';

CREATE TABLE IF NOT EXISTS user
(
    id              BIGINT UNSIGNED NOT NULL    AUTO_INCREMENT  COMMENT 'ID',
    name            VARCHAR(64)     NOT NULL                    COMMENT '用户名',
    create_time     TIMESTAMP       NOT NULL                    COMMENT '创建时间',
    update_time     TIMESTAMP       NOT NULL                    COMMENT '修改时间',
    is_deleted      BOOLEAN         NOT NULL    DEFAULT 0       COMMENT '是否删除',

    CONSTRAINT      pk_id           PRIMARY KEY (id),
    CONSTRAINT      uk_name         UNIQUE KEY (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8                            COMMENT '用户/用户组表';

CREATE TABLE IF NOT EXISTS user_info
(
    id              BIGINT UNSIGNED NOT NULL    AUTO_INCREMENT  COMMENT 'ID',
    user_id         BIGINT UNSIGNED NOT NULL                    COMMENT '用户主键外键',
    password        CHAR(64)        NOT NULL                    COMMENT '密码(经SHA256加密)',
    email           VARCHAR(64)                                 COMMENT '电子邮箱',
    phone_number    VARCHAR(16)                                 COMMENT '手机号码',
    allocated       INT UNSIGNED    NOT NULL                    COMMENT '已被分配的存储空间(MB)',
    create_time     TIMESTAMP       NOT NULL                    COMMENT '创建时间',
    update_time     TIMESTAMP       NOT NULL                    COMMENT '修改时间',
    is_deleted      BOOLEAN         NOT NULL    DEFAULT 0       COMMENT '是否删除',

    CONSTRAINT      pk_id           PRIMARY KEY (id),
    CONSTRAINT      uk_user_id      UNIQUE KEY (user_id),
    INDEX           idx_email (email(16)),
    INDEX           idx_phone_number (phone_number(8))
) ENGINE=InnoDB DEFAULT CHARSET=utf8                            COMMENT '用户信息表';

CREATE TABLE IF NOT EXISTS participation
(
    id              BIGINT UNSIGNED NOT NULL    AUTO_INCREMENT  COMMENT 'ID',
    user_id         BIGINT UNSIGNED NOT NULL                    COMMENT '用户主键',
    group_id        BIGINT UNSIGNED NOT NULL                    COMMENT '用户组主键',
    donated         INT UNSIGNED    NOT NULL                    COMMENT '用户向用户组贡献的容量大小（MB）',
    privilege       INT UNSIGNED    NOT NULL                    COMMENT '权限',
    create_time     TIMESTAMP       NOT NULL                    COMMENT '创建时间',
    update_time     TIMESTAMP       NOT NULL                    COMMENT '修改时间',
    is_deleted      BOOLEAN         NOT NULL    DEFAULT 0       COMMENT '是否删除',

    CONSTRAINT      pk_id           PRIMARY KEY (id),
    CONSTRAINT      uk_group_user   UNIQUE KEY (group_id, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8                            COMMENT '用户加入用户组表';

CREATE TABLE IF NOT EXISTS storage
(
    id              BIGINT UNSIGNED NOT NULL    AUTO_INCREMENT  COMMENT 'ID',
    owner           BIGINT UNSIGNED NOT NULL                    COMMENT '所有者主键',
    name            VARCHAR(64)                                 COMMENT '盘符',
    occupation      INT UNSIGNED    NOT NULL                    COMMENT '介质占用空间（MB）(外部介质为0）',
    readonly        BOOLEAN         NOT NULL                    COMMENT '是否以只读方式挂载',
    create_time     TIMESTAMP       NOT NULL                    COMMENT '创建时间',
    update_time     TIMESTAMP       NOT NULL                    COMMENT '修改时间',
    is_deleted      BOOLEAN         NOT NULL    DEFAULT 0       COMMENT '是否删除',

    CONSTRAINT      pk_id           PRIMARY KEY (id),
    INDEX           idx_owner (owner)
) ENGINE=InnoDB DEFAULT CHARSET=utf8                            COMMENT '用户存储介质表';

CREATE TABLE IF NOT EXISTS storage_interface
(
    id              BIGINT UNSIGNED NOT NULL                    COMMENT 'ID',
    interface       TEXT            NOT NULL                    COMMENT '介质访问接口（JSON格式）',

    CONSTRAINT      pk_id           PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8                            COMMENT '用户存储介质挂载接口表';

CREATE TABLE IF NOT EXISTS storage_log
(
    id              BIGINT UNSIGNED NOT NULL                    COMMENT 'ID,与storage表一一对应',
    update_time     TIMESTAMP       NOT NULL                    COMMENT '最近一次尝试挂载时间',
    success         BOOLEAN         NOT NULL                    COMMENT '最近一次尝试挂载是否成功',
    log             TEXT            NOT NULL                    COMMENT '最近一次挂载日志',

    CONSTRAINT      pk_id           PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8                            COMMENT '用户存储介质挂载日志表';

CREATE TABLE IF NOT EXISTS filesystem
(
    id              BIGINT UNSIGNED NOT NULL    AUTO_INCREMENT  COMMENT 'ID',
    owner           BIGINT UNSIGNED NOT NULL                    COMMENT '所有者主键',
    name            VARCHAR(64)     NOT NULL                    COMMENT '文件夹名',
    parent          BIGINT UNSIGNED                             COMMENT '父文件夹id',
    create_time     TIMESTAMP       NOT NULL                    COMMENT '创建时间',
    update_time     TIMESTAMP       NOT NULL                    COMMENT '修改时间',
    is_deleted      BOOLEAN         NOT NULL    DEFAULT 0       COMMENT '是否删除',

    CONSTRAINT      pk_id           PRIMARY KEY (id),
    INDEX           idx_owner (owner),
    INDEX           idx_parent (parent)
) ENGINE=InnoDB DEFAULT CHARSET=utf8                            COMMENT '外围文件系统表';

CREATE TABLE IF NOT EXISTS mount
(
    id              BIGINT UNSIGNED NOT NULL    AUTO_INCREMENT  COMMENT 'ID',
    path            BIGINT UNSIGNED NOT NULL                    COMMENT '挂载外围文件系统位置',
    storage_id      BIGINT UNSIGNED NOT NULL                    COMMENT '挂载介质主键',
    create_time     TIMESTAMP       NOT NULL                    COMMENT '创建时间',
    update_time     TIMESTAMP       NOT NULL                    COMMENT '修改时间',
    is_deleted      BOOLEAN         NOT NULL    DEFAULT 0       COMMENT '是否删除',

    CONSTRAINT      pk_id           PRIMARY KEY (id),
    CONSTRAINT      uk_path         UNIQUE KEY (path)
) ENGINE=InnoDB DEFAULT CHARSET=utf8                            COMMENT '挂载点表';

CREATE TABLE IF NOT EXISTS symbolic_link
(
    id              BIGINT UNSIGNED NOT NULL    AUTO_INCREMENT  COMMENT 'ID',
    parent          BIGINT UNSIGNED NOT NULL                    COMMENT '符号链接父文件夹主键',
    name            VARCHAR(64)     NOT NULL                    COMMENT '符号链接名',
    target_user     BIGINT UNSIGNED NOT NULL                    COMMENT '目标位置根目录的所有用户（组）',
    create_time     TIMESTAMP       NOT NULL                    COMMENT '创建时间',
    update_time     TIMESTAMP       NOT NULL                    COMMENT '修改时间',
    is_deleted      BOOLEAN         NOT NULL    DEFAULT 0       COMMENT '是否删除',

    CONSTRAINT      pk_id           PRIMARY KEY (id),
    INDEX           idx_parent (parent)
) ENGINE=InnoDB DEFAULT CHARSET=utf8                            COMMENT '符号链接表';

CREATE TABLE IF NOT EXISTS symbolic_link_target_path
(
    id              BIGINT UNSIGNED NOT NULL                    COMMENT 'ID',
    path            TEXT            NOT NULL                    COMMENT '目标路径，以"/"分割文件夹名，开头结尾无"/"',

    CONSTRAINT      pk_id           PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8                            COMMENT '符号链接路径表';

CREATE TABLE IF NOT EXISTS recycle
(
    id              BIGINT UNSIGNED NOT NULL    AUTO_INCREMENT  COMMENT 'ID',
    storage_id      BIGINT UNSIGNED NOT NULL                    COMMENT '文件（夹）所在存储介质',
    expire_time     TIMESTAMP       NOT NULL                    COMMENT '执行彻底删除时间',
    create_time     TIMESTAMP       NOT NULL                    COMMENT '创建时间',
    update_time     TIMESTAMP       NOT NULL                    COMMENT '修改时间',
    is_deleted      BOOLEAN         NOT NULL    DEFAULT 0       COMMENT '是否已取消（完成删除或被用户恢复）',

    CONSTRAINT      pk_id           PRIMARY KEY (id),
    INDEX           idx_storage_id (storage_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8                            COMMENT '回收站表';

CREATE TABLE IF NOT EXISTS recycle_path
(
    id              BIGINT UNSIGNED NOT NULL                    COMMENT 'ID',
    path            TEXT            NOT NULL                    COMMENT '目标路径，以"/"分割文件夹名，开头结尾无"/" ',

    CONSTRAINT      pk_id           PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8                            COMMENT '回收站路径表';

CREATE TABLE IF NOT EXISTS sharing
(
    id              BIGINT UNSIGNED NOT NULL    AUTO_INCREMENT  COMMENT 'ID',
    owner           BIGINT UNSIGNED NOT NULL                    COMMENT '所有者主键',
    link            CHAR(26)        NOT NULL                    COMMENT '分享链接（只含ULID部分）',
    pwd             CHAR(4)                                     COMMENT '分享口令，为NULL则不存在',
    expire_time     TIMESTAMP       NOT NULL                    COMMENT '共享结束时间',
    create_time     TIMESTAMP       NOT NULL                    COMMENT '创建时间',
    update_time     TIMESTAMP       NOT NULL                    COMMENT '修改时间',
    is_deleted      BOOLEAN         NOT NULL    DEFAULT 0       COMMENT '是否已取消（完成删除或被用户恢复）',

    CONSTRAINT      pk_id           PRIMARY KEY (id),
    INDEX           idx_owner (owner),
    INDEX           idx_link (link)
) ENGINE=InnoDB DEFAULT CHARSET=utf8                            COMMENT '链接分享表';

CREATE TABLE IF NOT EXISTS sharing_path
(
    id              BIGINT UNSIGNED NOT NULL    AUTO_INCREMENT  COMMENT 'ID',
    path            TEXT            NOT NULL                    COMMENT '目标路径，以"/"分割文件夹名，开头结尾无"/"',

    CONSTRAINT      pk_id           PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '链接分享路径表';

CREATE TABLE IF NOT EXISTS invitation
(
    id              BIGINT UNSIGNED NOT NULL    AUTO_INCREMENT  COMMENT 'ID',
    group_id        BIGINT UNSIGNED NOT NULL                    COMMENT '用户组主键',
    link            CHAR(26)        NOT NULL                    COMMENT '邀请链接（只含ULID部分）',
    pwd             CHAR(8)                                     COMMENT '分享口令，为NULL则不存在',
    expire_time     TIMESTAMP       NOT NULL                    COMMENT '共享结束时间',
    create_time     TIMESTAMP       NOT NULL                    COMMENT '创建时间',
    update_time     TIMESTAMP       NOT NULL                    COMMENT '修改时间',
    is_deleted      BOOLEAN         NOT NULL    DEFAULT 0       COMMENT '是否已取消（完成删除或被用户恢复）',

    CONSTRAINT      pk_id           PRIMARY KEY (id),
    INDEX           idx_group_id (group_id),
    INDEX           idx_link (link)
) ENGINE=InnoDB DEFAULT CHARSET=utf8                            COMMENT '加入用户组邀请表';