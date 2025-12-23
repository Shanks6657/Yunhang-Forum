# ☁️ Yunhang Forum（云航论坛）

> **基于 Java 25 + JavaFX 的现代化校园信息交流平台**

![Java 25](https://img.shields.io/badge/Java-25-orange?style=flat-square)
![JavaFX](https://img.shields.io/badge/JavaFX-21.0.5-blue?style=flat-square)
![Maven](https://img.shields.io/badge/Build-Maven-green?style=flat-square)
![Strategy Pattern](https://img.shields.io/badge/Design-Strategy%20Pattern-purple?style=flat-square)
![Virtual Threads](https://img.shields.io/badge/Concurrency-Virtual%20Threads-brightgreen?style=flat-square)

---

## 🌟 Key Features（核心功能）

### 🔐 用户认证（Auth）
- ✅ **注册 / 登录 / 注销**：面向校园账号体系的轻量认证流程
- ✅ **邮箱验证码**：集成 **SMTP（Jakarta Mail）**，支持真实验证码发送
- ✅ **密码安全**：密码采用 **PBKDF2（带 Salt）** 哈希存储（非明文）

### 📝 发帖与内容展示（Posting & Feed）
- ✅ **发布新帖**：支持“匿名发布”
- ✅ **帖子列表渲染**：基于 JavaFX `ListView + CellFactory + FXML` 的组件化渲染
- ✅ **帖子详情**：支持进入详情页查看内容与互动信息

### 💬 互动体系（Interaction）
- ✅ **点赞**：实时反馈，支持点赞/取消点赞
- ✅ **评论盖楼**：支持评论与回复结构（楼中楼）
- ✅ **通知系统（持久化）**：评论/点赞直接通知作者，并写入本地 JSON（重登不丢失）

### 🔎 策略搜索与多维排序（Strategy）
- ✅ **标题关键词搜索**：基于策略模式解耦搜索逻辑
- ✅ **多维排序**：按“最新发布 / 热度”切换（策略模式实现）

### 💾 数据持久化（DAO + JSON）
- ✅ **本地 JSON 持久化**：基于 `DataLoader` 接口（DAO 模式）抽象数据层
- ✅ **重启不丢数据**：用户、帖子、互动数据均落盘到 `data/*.json`
- ✅ **自动初始化**：首次运行自动创建 `data/` 目录与空 JSON 文件

---

## 💎 Technical Highlights（技术亮点 / 加分点）

### 🧵 Java 25 Virtual Threads（虚拟线程）
- 使用**虚拟线程**处理 I/O 密集任务（如：邮件发送、加载/写入 JSON、列表刷新），避免传统线程池的复杂度。
- 目标：在桌面端保证 UI 响应流畅，同时提升并发处理能力。

> 代码中采用了统一的异步任务封装（如 `TaskRunner`），将耗时操作从 JavaFX UI 线程中剥离。

### 🧠 纯正 OOP 架构（非 Spring / 非 Lombok）
- **Strategy Pattern（策略模式）**：
  - `PostSortStrategy`：排序策略接口
  - `PostSearchStrategy`：搜索策略接口
  - `impl/*Strategy`：具体策略实现（如 Time/Hot/Title Keyword）
- **DAO Pattern（数据访问层解耦）**：
  - `DataLoader` 接口
  - `JsonDataLoader` 具体实现（Gson + 本地文件）
- **Singleton（单例）**：
  - `UserSession` 作为登录态单例入口（安全集中管理）
- **Observer / Event（事件模型）**：
  - 项目内存在事件/可观察实体结构（如 `Event` / `ObservableEntity`），用于互动事件建模。
  - 交付版通知实现采用“**直接通知作者 + 持久化落盘**”的鲁棒方案，避免运行时 Observer 列表带来的重复/丢失风险。

### 📦 模块化（JPMS）
- 项目使用 `module-info.java` 管理模块依赖：JavaFX、Gson、Jakarta Mail 等。

---

## 🛠 Tech Stack（技术栈）

- **Language**：Java 25（编译级别以 `pom.xml` 为准）
- **UI**：JavaFX 21.0.5（Modular）
- **Build**：Maven
- **JSON**：Gson
- **Mail**：Jakarta Mail（SMTP）

---

## 📂 Project Structure（项目结构）

> 与当前仓库目录保持一致（以实际代码为准）

```text
src/
  main/
    java/
      com/yunhang/forum/
        MainApp.java
        controller/
          auth/      # Login / Register / UserProfile / PostList
          main/      # MainLayout / MyPosts
          post/      # PostDetail / PostEditor
        dao/
          DataLoader.java
          impl/
            JsonDataLoader.java
            FileDataLoader.java
        model/
          entity/    # User / Student / Post / Comment / Notification ...
          enums/     # PostCategory / PostStatus / EventType ...
          session/   # UserSession
        service/
          EmailService.java
          strategy/  # Strategy interfaces + implementations
        util/        # ViewManager / AppContext / TaskRunner / LogUtil / ResourcePaths ...

    resources/
      application.properties
      com/yunhang/forum/
        css/
        fxml/
          auth/
            Login.fxml
            Register.fxml
            PostList.fxml
            PostItem.fxml
            UserProfile.fxml
            main/
              MainLayout.fxml
              MyPosts.fxml
          post/
            PostDetail.fxml
            PostEditor.fxml
          user/
            Settings.fxml
```

---

## 🚀 Getting Started（运行指南）

### ✅ 环境要求
- **JDK 21+**（推荐 **JDK 25**）
- Maven 3.8+
- IntelliJ IDEA（推荐）

### ▶️ 方式 A：Maven 一键运行（推荐）

```bash
git clone <your-repo-url>
cd Yunhang-Forum
mvn clean javafx:run
```

### ▶️ 方式 B：IDEA 运行
- 打开项目根目录（Maven 会自动导入依赖）
- 运行：`src/main/java/com/yunhang/forum/MainApp.java`

### 📌 重要提示（零配置开箱即用）
- 首次运行会自动创建本地数据目录：`data/`
- 数据文件默认：
  - `data/users.json`
  - `data/posts.json`

### ✉️ SMTP 配置（开启真实邮箱验证码）
- 邮件发送由 `EmailService` 驱动（Jakarta Mail）。
- 你需要在 `application.properties`（或环境变量）中配置 SMTP 账号信息。

> 若未配置 SMTP：注册页仍可提示配置缺失并给出引导信息（见 `UserService.smtpConfigHelp()`）。

---

## 📸 Screenshots（截图占位）

> 提交前把图片放到仓库（例如 `docs/screenshots/`），再替换链接即可。

| 页面 | 预览 |
|---|---|
| 🔐 Login | ![Login](docs/screenshots/login.png) |
| 📰 Post List | ![Post List](docs/screenshots/post-list.png) |
| 📄 Post Detail | ![Post Detail](docs/screenshots/post-detail.png) |
| ✍️ Post Editor | ![Post Editor](docs/screenshots/post-editor.png) |

---

## 👥 Team（团队分工）

> 交付前请补全。

| 姓名 | 学号 | 分工 | 权重 |
|---|---|---|---|
| 张三 | 2025xxxxx | 架构设计 / DAO 持久化 / 通知系统 | 0.30 |
| 李四 | 2025xxxxx | JavaFX UI / ViewManager 路由 | 0.30 |
| 王五 | 2025xxxxx | 策略模式（搜索/排序）/ 交互模块 | 0.25 |
| 赵六 | 2025xxxxx | 测试用例 / 文档 / 代码规范 | 0.15 |

---

## ✅ Deliverable Checklist（交付自检）

- [x] Maven 构建通过：`mvn test`
- [x] 数据落盘：重启不丢用户/帖子/通知
- [x] 模块化依赖：`module-info.java` 完整声明 JavaFX / Gson / Jakarta Mail
- [x] 核心功能闭环：注册/登录、发帖、评论、点赞、搜索、排序

---

## 📄 License

MIT License
