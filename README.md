# 虚谷数据库 MCP 服务器

通过 MCP（Model Context Protocol）协议连接虚谷数据库，为 AI 工具提供数据库查询能力。

[![Java Version](https://img.shields.io/badge/Java-8+-blue.svg)](https://www.java.com/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-red.svg)](https://maven.apache.org/)

## 功能特性

- SQL 查询：执行 SELECT 查询语句
- DML 操作：INSERT/UPDATE/DELETE
- DDL 操作：CREATE/ALTER/DROP TABLE
- 表结构查看：获取表的列信息
- 表列表：列出所有用户表
- 环境变量配置：灵活配置数据库连接
- UTF-8 编码支持：中文数据正常处理

## 快速开始

### 前置要求

- JDK 8 或更高版本
- Maven 3.6+
- 虚谷数据库（Xugu Database）

### 1. 编译项目

```bash
git clone https://github.com/your-repo/xugu-mcp.git
cd xugu-mcp
mvn clean package -DskipTests
```

### 2. 配置数据库连接

本项目**不包含默认数据库配置**，首次使用必须通过环境变量配置连接信息。

**方式一：完整 JDBC URL（推荐）**

```bash
# Linux/Mac
export XUGU_URL="jdbc:xugu://your-host:5138/your-database"
export XUGU_USERNAME="your-username"
export XUGU_PASSWORD="your-password"

# Windows PowerShell
$env:XUGU_URL="jdbc:xugu://your-host:5138/your-database"
$env:XUGU_USERNAME="your-username"
$env:XUGU_PASSWORD="your-password"
```

**方式二：分别配置各参数**

```bash
export XUGU_HOST="your-host"
export XUGU_PORT="5138"
export XUGU_DATABASE="your-database"
export XUGU_USERNAME="your-username"
export XUGU_PASSWORD="your-password"
```

### 3. 启动服务

```bash
# Linux/Mac
./start.sh

# Windows
.\start.bat

# 或手动指定
java -Dfile.encoding=UTF-8 -jar target/xugu-mcp-1.0.jar
```

> **注意**：必须添加 `-Dfile.encoding=UTF-8` 参数，否则中文数据可能显示乱码。

### 4. 验证安装

```bash
echo '{"jsonrpc":"2.0","id":1,"method":"tools/list","params":{}}' | java -Dfile.encoding=UTF-8 -jar target/xugu-mcp-1.0.jar
```

正常情况下会返回可用的工具列表。

## MCP 客户端配置

### Claude Code

在项目根目录创建 `.mcp.json`：

```json
{
  "mcpServers": {
    "xugu": {
      "command": "java",
      "args": ["-Dfile.encoding=UTF-8", "-jar", "/path/to/xugu-mcp-1.0.jar"],
      "env": {
        "XUGU_HOST": "your-host",
        "XUGU_PORT": "5138",
        "XUGU_DATABASE": "your-database",
        "XUGU_USERNAME": "your-username",
        "XUGU_PASSWORD": "your-password"
      }
    }
  }
}
```

### 其他 MCP 客户端

支持任何兼容 MCP 协议的工具，配置方式类似，只需确保：
- 使用 `java -Dfile.encoding=UTF-8 -jar` 启动
- 通过环境变量或命令行传入数据库连接信息

## MCP 工具列表

| 工具 | 参数 | 说明 |
|------|------|------|
| `execute_query` | `sql` | 执行 SELECT 查询，返回结果集 |
| `execute_update` | `sql` | 执行 INSERT/UPDATE/DELETE，返回影响行数 |
| `create_table` | `sql` | 执行 CREATE TABLE 语句 |
| `alter_table` | `sql` | 执行 ALTER TABLE 语句 |
| `drop_table` | `table_name` | 删除指定表 |
| `get_table_schema` | `table_name` | 获取表结构信息（列名、类型、可空等） |
| `list_tables` | 无 | 列出所有用户表 |

## 使用示例

配置完成后，可通过 AI 助手直接操作数据库：

```
帮我查询 SYS_USER 表的前 5 条数据
```

```
查看 DMS_PRODUCT 表的结构
```

```
列出所有以 DMS_ 开头的表
```

## 环境变量说明

支持两种配置方式，**二选一**：

**方式一：使用完整 JDBC URL**

| 变量 | 说明 | 必需 |
|------|------|------|
| `XUGU_URL` | 完整 JDBC URL（如 `jdbc:xugu://host:port/database`） | 是 |
| `XUGU_USERNAME` | 数据库用户名 | 是 |
| `XUGU_PASSWORD` | 数据库密码 | 是 |
| `XUGU_DRIVER` | JDBC 驱动类名 | 否（默认 `com.xugu.cloudjdbc.Driver`） |
| `XUGU_POOL_SIZE` | 连接池大小 | 否（默认 5） |

**方式二：使用分散的组件参数**

| 变量 | 说明 | 必需 |
|------|------|------|
| `XUGU_HOST` | 数据库主机地址 | 是 |
| `XUGU_PORT` | 数据库端口 | 是 |
| `XUGU_DATABASE` | 数据库名称 | 是 |
| `XUGU_USERNAME` | 数据库用户名 | 是 |
| `XUGU_PASSWORD` | 数据库密码 | 是 |
| `XUGU_DRIVER` | JDBC 驱动类名 | 否（默认 `com.xugu.cloudjdbc.Driver`） |
| `XUGU_POOL_SIZE` | 连接池大小 | 否（默认 5） |

> **注意**：必需的环境变量未配置时，程序启动时会报错并提示具体缺少哪些变量。

## 项目结构

```
xugu-mcp/
├── pom.xml                    # Maven 配置
├── README.md                  # 使用手册
├── start.bat                  # Windows 启动脚本
├── start.sh                   # Linux/Mac 启动脚本
├── .mcp.json.example          # MCP 客户端配置示例
└── src/main/
    ├── java/com/hivesoft/dms/xugumcp/
    │   ├── XuGuMcpApplication.java       # 启动类
    │   ├── config/XuGuConfig.java         # 配置类
    │   ├── domain/                        # 数据模型
    │   ├── handler/McpRequestHandler.java # 请求处理器
    │   ├── server/McpStdioServer.java     # STDIO 服务器
    │   └── service/                       # 数据库服务
    └── resources/
        └── application.yml                # 应用配置
```

## 注意事项

1. **编码问题**：必须添加 `-Dfile.encoding=UTF-8` 参数启动

2. **SQL 语法**：虚谷数据库为国产数据库，部分语法与 Oracle/MySQL 略有差异：
   - 不支持 `ROWNUM`，请使用 `LIMIT`
   - 不支持 `DESC table_name`，请使用 `get_table_schema` 工具

3. **安全建议**：
   - 生产环境请使用强度较高的密码
   - 避免在配置文件中明文存储密码
   - 优先使用环境变量配置敏感信息

## 常见问题

**Q: 连接失败怎么办？**

1. 检查网络是否可达
2. 确认虚谷数据库服务是否启动
3. 验证用户名密码是否正确
4. 检查防火墙端口是否开放

**Q: 查询结果乱码？**

确保启动时添加 `-Dfile.encoding=UTF-8` 参数。

**Q: 如何查看更详细的日志？**

修改 `src/main/resources/log4j.properties` 中的日志级别。

## License

Apache License 2.0
