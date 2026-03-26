# 虚谷数据库 MCP 服务器

通过 MCP（Model Context Protocol）协议连接虚谷数据库，为 AI 工具提供数据库查询能力。

[![Java Version](https://img.shields.io/badge/Java-17+-blue.svg)](https://www.java.com/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-red.svg)](https://maven.apache.org/)

## 功能特性

- **SQL 查询**：执行 SELECT 查询语句
- **DML 操作**：INSERT/UPDATE/DELETE
- **DDL 操作**：CREATE/ALTER/DROP TABLE
- **表结构查看**：获取表的列信息
- **表列表**：列出所有用户表
- **环境变量配置**：灵活配置数据库连接
- **UTF-8 编码支持**：中文数据正常处理
- **Windows 可执行文件**：无需安装 Java 环境即可运行

## 快速开始

### 前置要求

- JDK 17+（如使用源码编译）
- Maven 3.6+
- 虚谷数据库（Xugu Database）

### 下载预构建版本（推荐）

下载 `dist/xugu-mcp.exe` 压缩包，解压后直接运行：

```bash
# 配置环境变量后运行
XUGU_HOST=your-host XUGU_PORT=5138 XUGU_DATABASE=your-db XUGU_USERNAME=user XUGU_PASSWORD=pass ./xugu-mcp/xugu-mcp.exe
```

### 方式一：使用可执行文件（无需 Java）

1. 下载最新版本的 `xugu-mcp-windows.zip`
2. 解压到任意目录
3. 配置环境变量或创建 `xugu-mcp.properties` 配置文件
4. 运行 `xugu-mcp.exe`

### 方式二：从源码构建

```bash
git clone https://github.com/hivesoft/xugu-mcp.git
cd xugu-mcp
mvn clean package -DskipTests
```

### 打包为 Windows 可执行文件

使用 jpackage 将 jar 打包为原生 Windows 可执行文件（无需安装 Java）：

```bash
# 1. 先编译项目
mvn clean package -DskipTests

# 2. 使用 jpackage 打包
jpackage --type app-image --input target --main-jar xugu-mcp-1.0.jar --name xugu-mcp --dest ./dist --java-options "-Dfile.encoding=UTF-8"
```

生成的 exe 文件位于 `dist/xugu-mcp/xugu-mcp.exe`

**jpackage 原理：**
- 将 jar 和 Java 运行时一起打包
- 生成原生 Windows 可执行文件
- 无需目标机器安装 Java 环境

## 配置数据库连接

### 方式一：环境变量配置

```bash
# Linux/Mac
export XUGU_HOST="your-host"
export XUGU_PORT="5138"
export XUGU_DATABASE="your-database"
export XUGU_USERNAME="your-username"
export XUGU_PASSWORD="your-password"

# Windows PowerShell
$env:XUGU_HOST="your-host"
$env:XUGU_PORT="5138"
$env:XUGU_DATABASE="your-database"
$env:XUGU_USERNAME="your-username"
$env:XUGU_PASSWORD="your-password"
```

### 方式二：配置文件（仅适用于 exe 版本）

在可执行文件同级目录创建 `xugu-mcp.properties`：

```properties
xugu.host=your-host
xugu.port=5138
xugu.database=your-database
xugu.username=your-username
xugu.password=your-password
```

## MCP 客户端配置

### Claude Code

在项目根目录创建或编辑 `.mcp.json`：

```json
{
  "mcpServers": {
    "xugu": {
      "command": "D:/工具/xugu-mcp/dist/xugu-mcp/xugu-mcp.exe",
      "env": {
        "XUGU_HOST": "192.168.51.68",
        "XUGU_PORT": "5138",
        "XUGU_DATABASE": "SKIFF_DMS",
        "XUGU_USERNAME": "CRAB",
        "XUGU_PASSWORD": "Hivesoft@123"
      }
    }
  }
}
```

> **注意**：推荐使用 exe 版本，因为 Claude Code 的 MCP 客户端可能不支持通过 `java -jar` 方式传递环境变量。

### 其他 MCP 客户端

支持任何兼容 MCP 协议的工具，配置方式类似。

## MCP 工具列表

| 工具 | 参数 | 说明 |
|------|------|------|
| `execute_query` | `sql` | 执行 SELECT 查询，返回结果集 |
| `execute_update` | `sql` | 执行 INSERT/UPDATE/DELETE，返回影响行数 |
| `create_table` | `sql` | 执行 CREATE TABLE 语句 |
| `alter_table` | `sql` | 执行 ALTER TABLE 语句 |
| `drop_table` | `table_name` | 删除指定表 |
| `get_table_schema` | `table_name` | 获取表结构信息（列名、类型、可空、注释等） |
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

| 变量 | 说明 | 必需 |
|------|------|------|
| `XUGU_URL` | 完整 JDBC URL（如 `jdbc:xugu://host:port/database`） | 是（方式一） |
| `XUGU_HOST` | 数据库主机地址 | 是（方式二） |
| `XUGU_PORT` | 数据库端口 | 是（方式二） |
| `XUGU_DATABASE` | 数据库名称 | 是（方式二） |
| `XUGU_USERNAME` | 数据库用户名 | 是 |
| `XUGU_PASSWORD` | 数据库密码 | 是 |
| `XUGU_DRIVER` | JDBC 驱动类名 | 否（默认 `com.xugu.cloudjdbc.Driver`） |
| `XUGU_POOL_SIZE` | 连接池大小 | 否（默认 5） |

## 项目结构

```
xugu-mcp/
├── pom.xml                    # Maven 配置
├── README.md                  # 使用手册
├── LICENSE                    # 许可证
├── dist/                      # 打包输出目录（exe 版本）
│   └── xugu-mcp/
│       ├── xugu-mcp.exe      # Windows 可执行文件
│       └── runtime/           # Java 运行时
├── src/main/
│   ├── java/com/hivesoft/dms/xugumcp/
│   │   ├── XuGuMcpApplication.java       # 启动类
│   │   ├── config/XuGuConfig.java         # 配置类（支持环境变量和配置文件）
│   │   ├── domain/                        # 数据模型
│   │   │   ├── McpRequest.java            # MCP 请求
│   │   │   ├── McpResponse.java            # MCP 响应
│   │   │   └── McpTool.java               # MCP 工具定义（符合 JSON Schema）
│   │   ├── handler/McpRequestHandler.java # 请求处理器
│   │   ├── server/McpStdioServer.java     # STDIO 服务器
│   │   └── service/                       # 数据库服务
│   │       ├── DatabaseService.java        # 服务接口
│   │       └── impl/XuguDatabaseServiceImpl.java  # 虚谷数据库实现
│   └── resources/
│       └── log4j.properties                # 日志配置
```

## 技术亮点

1. **符合 MCP 协议规范**：工具定义使用标准 JSON Schema 格式
2. **支持直接工具调用**：兼容 Claude Code 的 `mcp__xugu__*` 调用方式
3. **优雅降级**：数据库未配置时仍可启动并返回工具列表
4. **jpackage 打包**：无需 Java 环境即可运行
5. **连接池管理**：复用数据库连接，提高性能

## 注意事项

1. **编码问题**：必须使用 `-Dfile.encoding=UTF-8` 参数或 exe 版本启动

2. **SQL 语法差异**：虚谷数据库为国产数据库，部分语法与 Oracle/MySQL 略有差异
   - 不支持 `ROWNUM`，请使用 `LIMIT`
   - 不支持 `DESC table_name`，请使用 `get_table_schema` 工具

3. **安全建议**
   - 生产环境请使用强度较高的密码
   - 避免在配置文件中明文存储密码
   - 优先使用环境变量配置敏感信息

## 常见问题

**Q: Claude Code 无法识别 xugu MCP？**

A: 确保使用 exe 版本而非 jar 版本。Claude Code 的 MCP 客户端对 jar 方式的环境变量支持存在问题。

**Q: 连接失败怎么办？**

1. 检查网络是否可达
2. 确认虚谷数据库服务是否启动
3. 验证用户名密码是否正确
4. 检查防火墙端口是否开放

**Q: 查询结果乱码？**

A: 确保启动时添加 `-Dfile.encoding=UTF-8` 参数或使用 exe 版本。

## License

Apache License 2.0
