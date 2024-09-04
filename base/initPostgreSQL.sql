-- 创建数据库
CREATE DATABASE mysqlgpt_db;
\c mysqlgpt_db;

-- 数据库信息表，存储不同数据库的连接信息。
CREATE TABLE IF NOT EXISTS database_info
(
  database_info_id     SERIAL PRIMARY KEY,
  database_name        VARCHAR(255),
  host                 VARCHAR(255),
  port                 INT,
  username             VARCHAR(255),
  password             VARCHAR(255),
  auth_method          INT,
  database_type        VARCHAR(255),
  database_description TEXT,
  create_time          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  sync_time            TIMESTAMP,
  analyze_time         TIMESTAMP
);

COMMENT ON TABLE database_info IS '数据库信息表：用于存储所有可查询的数据库连接信息。';
COMMENT ON COLUMN database_info.database_info_id IS '数据库信息ID，唯一标识每个数据库信息记录';
COMMENT ON COLUMN database_info.database_name IS '数据库名称，用户定义的数据库描述名称';
COMMENT ON COLUMN database_info.host IS '主机地址，数据库服务器的IP地址或主机名';
COMMENT ON COLUMN database_info.port IS '端口号，用于访问数据库的端口';
COMMENT ON COLUMN database_info.username IS '数据库用户名，用于数据库登录认证';
COMMENT ON COLUMN database_info.password IS '数据库密码，建议加密存储，用于数据库登录认证';
COMMENT ON COLUMN database_info.auth_method IS '认证方式，1表示使用用户名和密码，0表示不使用';
COMMENT ON COLUMN database_info.database_type IS '数据库类型，存储数据库的类型信息，如 MySQL, PostgreSQL 等';
COMMENT ON COLUMN database_info.database_description IS '数据库解释，详细描述数据库的业务功能';
COMMENT ON COLUMN database_info.create_time IS '创建时间，记录信息的创建时间';
COMMENT ON COLUMN database_info.update_time IS '更新时间，记录信息的最后更新时间';
COMMENT ON COLUMN database_info.sync_time IS '同步时间，记录数据库信息的同步时间';
COMMENT ON COLUMN database_info.analyze_time IS '分析时间，记录数据库信息的分析时间';

-- 表信息表，记录每个数据库中的表及其基本信息，加入表注释和表功能解释。
CREATE TABLE IF NOT EXISTS table_info
(
  table_info_id     SERIAL PRIMARY KEY,
  database_info_id  INT NOT NULL,
  table_name        VARCHAR(255),
  table_comment     TEXT,
  table_description TEXT,
  create_time       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE table_info IS '表信息表：用于存储各个数据库中的表名称及其相关信息，包括表的注释和功能描述。';
COMMENT ON COLUMN table_info.table_info_id IS '表信息ID，唯一标识每个表信息记录';
COMMENT ON COLUMN table_info.database_info_id IS '关联的数据库信息ID，引用数据库信息表的ID';
COMMENT ON COLUMN table_info.table_name IS '表名，存储具体的表名称';
COMMENT ON COLUMN table_info.table_comment IS '表注释，简短描述表的内容和用途';
COMMENT ON COLUMN table_info.table_description IS '表功能解释，详细描述表的业务功能和重要性';
COMMENT ON COLUMN table_info.create_time IS '创建时间，记录表信息的创建时间';
COMMENT ON COLUMN table_info.update_time IS '更新时间，记录表信息的最后更新时间';

-- 列信息表，详细记录表中的每一列信息。
CREATE TABLE IF NOT EXISTS column_info
(
  column_info_id     SERIAL PRIMARY KEY,
  table_info_id      INT NOT NULL,
  database_info_id   INT NOT NULL,
  column_name        VARCHAR(255),
  column_comment     TEXT,
  column_description TEXT,
  data_type          VARCHAR(255),
  data_type_comment  TEXT,
  is_current         BOOLEAN   DEFAULT TRUE,
  create_time        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time        TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE column_info IS '列信息表：用于存储表中每一列的详细信息，包括数据类型、注释等。';
COMMENT ON COLUMN column_info.column_info_id IS '列信息ID，唯一标识每个列信息记录';
COMMENT ON COLUMN column_info.table_info_id IS '关联的表信息ID，引用表信息表的ID';
COMMENT ON COLUMN column_info.database_info_id IS '关联的数据库信息ID，引用数据库信息表的ID';
COMMENT ON COLUMN column_info.column_name IS '列名，存储具体的列名称';
COMMENT ON COLUMN column_info.column_comment IS '列注释，描述列的内容和用途';
COMMENT ON COLUMN column_info.column_description IS '列数据格式解释，详细描述列字段数据格式';
COMMENT ON COLUMN column_info.data_type IS '数据类型，列的数据类型（如INT, VARCHAR等）';
COMMENT ON COLUMN column_info.data_type_comment IS '数据类型注释，对列数据类型的额外说明（如使用限制等）';
COMMENT ON COLUMN column_info.is_current IS '标识是否为当前结构，标识列信息是否是当前使用的结构';
COMMENT ON COLUMN column_info.create_time IS '创建时间，记录列信息的创建时间';
COMMENT ON COLUMN column_info.update_time IS '更新时间，记录列信息的最后更新时间';

-- 用户表，存储使用系统的用户信息。
CREATE TABLE IF NOT EXISTS users
(
  user_id       SERIAL PRIMARY KEY,
  username      VARCHAR(255) UNIQUE,
  password_hash VARCHAR(255),
  email         VARCHAR(255) UNIQUE,
  avatar        VARCHAR(255),
  create_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE users IS '用户表：用于存储用户的基本信息，包括用户名、密码哈希和邮箱等。';
COMMENT ON COLUMN users.user_id IS '用户ID，唯一标识每个用户';
COMMENT ON COLUMN users.username IS '用户名，用户的唯一用户名';
COMMENT ON COLUMN users.password_hash IS '密码哈希，存储用户密码的哈希值，用于验证用户登录';
COMMENT ON COLUMN users.email IS '电子邮箱，用户的电子邮箱地址，唯一';
COMMENT ON COLUMN users.avatar IS '头像';
COMMENT ON COLUMN users.create_time IS '创建时间，用户记录的创建时间';
COMMENT ON COLUMN users.update_time IS '更新时间，用户记录的最后更新时间';

-- 会话表，记录用户的每次对话会话信息。
CREATE TABLE IF NOT EXISTS sessions
(
  session_id           SERIAL PRIMARY KEY,
  user_id              INT NOT NULL,
  model_id             INT NOT NULL,
  database_info_id     INT ,
  database_info        TEXT,
  database_info_cheked TEXT,
  title                VARCHAR(255),
  messages             TEXT,
  create_time          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE sessions IS '会话表：用于记录用户的每次对话会话，包括会话标题和时间。';
COMMENT ON COLUMN sessions.session_id IS '会话ID，唯一标识每个会话';
COMMENT ON COLUMN sessions.user_id IS '用户ID，引用用户表的用户ID';
COMMENT ON COLUMN sessions.model_id IS '模型ID,应用模型表的ID';
COMMENT ON COLUMN sessions.database_info_id IS '模数据库ID';
COMMENT ON COLUMN sessions.database_info IS 'system预定义信息,用于筛选';
COMMENT ON COLUMN sessions.database_info_cheked IS 'system预定义信息,选中的数组key';
COMMENT ON COLUMN sessions.title IS '会话标题，用户定义的会话标题';
COMMENT ON COLUMN sessions.messages IS '消息内容，存储会话中交互的具体内容jsonlist格式';
COMMENT ON COLUMN sessions.create_time IS '创建时间，会话的创建时间';
COMMENT ON COLUMN sessions.update_time IS '更新时间，会话的最后更新时间';

-- 用户查询记录表，记录用户通过系统进行的每一次查询。
CREATE TABLE IF NOT EXISTS queries
(
  query_id         SERIAL PRIMARY KEY,
  database_info_id INT     NOT NULL,
  session_id       INT     NOT NULL,
  user_id          INT     NOT NULL,
  query_text       TEXT,
  sql_text         TEXT,
  response_text    TEXT,
  execution_time   FLOAT,
  executed_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  create_time      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  success          BOOLEAN NOT NULL
);

COMMENT ON TABLE queries IS '用户查询记录表：用于记录用户通过系统进行的每一次查询，包括查询文本、生成的SQL/JS代码、响应文本等。';
COMMENT ON COLUMN queries.query_id IS '查询ID，唯一标识每个查询';
COMMENT ON COLUMN queries.database_info_id IS '关联的数据库信息ID，引用数据库信息表的ID';
COMMENT ON COLUMN queries.session_id IS '会话ID，引用会话表的ID';
COMMENT ON COLUMN queries.user_id IS '用户ID，引用用户表的ID';
COMMENT ON COLUMN queries.query_text IS '自然语言查询文本，用户输入的查询请求文本';
COMMENT ON COLUMN queries.sql_text IS '生成的SQL语句，系统根据自然语言查询生成的SQL代码';
COMMENT ON COLUMN queries.response_text IS '系统响应文本，系统对查询请求的回应';
COMMENT ON COLUMN queries.execution_time IS '执行时间（秒），查询执行的时间长度';
COMMENT ON COLUMN queries.executed_at IS '执行时间，查询实际执行的时间点';
COMMENT ON COLUMN queries.create_time IS '创建时间，记录查询的创建时间';
COMMENT ON COLUMN queries.update_time IS '更新时间，查询记录的最后更新时间';
COMMENT ON COLUMN queries.success IS '执行结果，指示查询是否成功';

-- 图表配置表，存储用户自定义的图表配置信息。
CREATE TABLE IF NOT EXISTS charts
(
  chart_id     SERIAL PRIMARY KEY,
  user_id      INT NOT NULL,
  query_id     INT NOT NULL,
  chart_config TEXT,
  create_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE charts IS '图表配置表：用于存储用户自定义的图表配置信息，关联到具体的查询。';
COMMENT ON COLUMN charts.chart_id IS '图表ID，唯一标识每个图表配置';
COMMENT ON COLUMN charts.user_id IS '用户ID，引用用户表的ID';
COMMENT ON COLUMN charts.query_id IS '查询ID，引用查询记录表的ID';
COMMENT ON COLUMN charts.chart_config IS '图表配置信息，存储用户自定义的图表配置数据';
COMMENT ON COLUMN charts.create_time IS '创建时间，图表配置的创建时间';
COMMENT ON COLUMN charts.update_time IS '更新时间，图表配置的最后更新时间';

CREATE TABLE IF NOT EXISTS model
(
  model_id    SERIAL PRIMARY KEY,
  model_name  VARCHAR(255) NOT NULL UNIQUE,
  api_key     VARCHAR(255),
  use_proxy   BOOLEAN   DEFAULT FALSE,
  proxy_host  VARCHAR(255),
  proxy_port  INT,
  base_url    VARCHAR(255),
  model       VARCHAR(255),
  description TEXT,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE model IS 'model表：用于存储不同模型的配置信息，包括URL、Token、Proxy等。';
COMMENT ON COLUMN model.model_id IS '模型配置ID，唯一标识每个模型配置记录';
COMMENT ON COLUMN model.model_name IS '模型名称，唯一标识不同的模型';
COMMENT ON COLUMN model.api_key IS 'API密钥，用于认证访问模型服务';
COMMENT ON COLUMN model.use_proxy IS '是否使用代理，指示是否需要通过代理访问模型服务';
COMMENT ON COLUMN model.proxy_host IS '代理服务器主机地址';
COMMENT ON COLUMN model.proxy_port IS '代理服务器端口号';
COMMENT ON COLUMN model.base_url IS 'API基础URL，用于访问模型服务的基础URL';
COMMENT ON COLUMN model.model IS '模型标识符，用于指定具体的模型';
COMMENT ON COLUMN model.description IS '模型描述信息，简要描述模型的用途和功能';
COMMENT ON COLUMN model.create_time IS '创建时间，记录信息的创建时间';
COMMENT ON COLUMN model.update_time IS '更新时间，记录信息的最后更新时间';


CREATE EXTENSION vector;
CREATE TABLE IF NOT EXISTS query_vector
(
  query_vector_id  SERIAL PRIMARY KEY,
  database_info_id INT     NOT NULL,
  session_id       INT,
  user_id          INT     NOT NULL,
  query_text       TEXT,
  result_text      TEXT,
  create_time      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  success          BOOLEAN NOT NULL,
  embedding_vector VECTOR(768)
);

COMMENT ON TABLE query_vector IS '向量知识库表：用于记录和检索query_text向量';
COMMENT ON COLUMN query_vector.query_vector_id IS '向量ID，唯一标识每个向量';
COMMENT ON COLUMN query_vector.database_info_id IS '关联的数据库信息ID，引用数据库信息表的ID';
COMMENT ON COLUMN query_vector.session_id IS '会话ID，引用会话表的ID';
COMMENT ON COLUMN query_vector.user_id IS '用户ID，引用用户表的ID';
COMMENT ON COLUMN query_vector.query_text IS '自然语言查询文本，用户输入的查询请求文本';
COMMENT ON COLUMN query_vector.result_text IS '生成的SQL语句,BI系统的一个url，系统根据自然语言查询生成的SQL代码';
COMMENT ON COLUMN query_vector.create_time IS '创建时间，记录查询的创建时间';
COMMENT ON COLUMN query_vector.update_time IS '更新时间，查询记录的最后更新时间';
COMMENT ON COLUMN query_vector.success IS '执行结果，指示查询是否成功';
COMMENT ON COLUMN query_vector.embedding_vector IS '嵌入向量，用于存储查询文本的向量表示';

-- 用户输入记录表，记录用户的每次输入内容
CREATE TABLE IF NOT EXISTS user_input_records
(
  input_id       SERIAL PRIMARY KEY,
  user_id        INT NOT NULL,
  input_text     TEXT,
  create_time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE user_input_records IS '用户输入记录表：用于记录用户的每次输入内容，以便用于用户历史输入推荐。';
COMMENT ON COLUMN user_input_records.input_id IS '输入ID，唯一标识每条输入记录';
COMMENT ON COLUMN user_input_records.user_id IS '用户ID，引用用户表的ID';
COMMENT ON COLUMN user_input_records.input_text IS '输入内容，存储用户每次输入的具体内容';
COMMENT ON COLUMN user_input_records.create_time IS '创建时间，记录输入内容的创建时间';
COMMENT ON COLUMN user_input_records.update_time IS '更新时间，记录输入内容的最后更新时间';
