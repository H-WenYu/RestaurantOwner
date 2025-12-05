# 🏨 小旅店发家记 - Restaurant Owner

一个全自动经营模拟游戏，用 Java 编写。

## 游戏简介

从一家破旧小旅店，靠接客、做菜、升级，最后变成米其林级大酒店！
全程自动运行，你只需要看数据看板，偶尔做做决策。

## 核心玩法

- **全自动经营**: 员工会自动迎客、点单、做菜、上菜
- **升级系统**: 员工和旅店都可以升级，解锁新能力
- **体力机制**: 员工会疲劳，需要休息
- **随机事件**: 雨天、美食评论家、员工吵架等增加趣味
- **声望系统**: 好的服务带来更多顾客

## 如何运行

### 方式1：使用 Maven（推荐）

```bash
# 编译
mvn clean compile

# 运行
mvn exec:java -Dexec.mainClass="com.restaurant.Main"

# 打包成可执行 JAR
mvn clean package
java -jar target/restaurant-owner-1.0.0.jar
```

### 方式2：直接编译运行

```bash
# Windows
mkdir out
javac -encoding UTF-8 -d out src/main/java/com/restaurant/**/*.java src/main/java/com/restaurant/*.java
java -cp out com.restaurant.Main

# 或者使用批处理脚本
run.bat
```

## 操作说明

| 按键 | 功能 |
|------|------|
| Q | 退出游戏 |
| P | 暂停/继续 |
| 1-4 | 调整游戏速度 (1x/2x/3x/4x) |
| H | 招聘员工 |
| T | 购买桌子 |
| M | 查看菜单 |

## 游戏机制

### 顾客流程
1. 顾客到来 → 等待入座
2. 服务员迎客 → 顾客入座
3. 服务员点单 → 顾客点菜
4. 厨师做菜 → 菜品完成
5. 服务员上菜 → 顾客吃饭
6. 顾客付款 → 离开

### 升级收益

| 项目 | 升级收益 |
|------|---------|
| 服务员升级 | 服务速度↑，出错率↓，可同时服务更多桌 |
| 厨师升级 | 做菜时间↓，能做更高级菜品 |
| 旅店升级 | 解锁新菜品，增加座位，顾客到来率↑ |

### 菜品列表

| 菜品 | 价格 | 制作时间 | 等级要求 |
|------|------|----------|----------|
| 蛋炒饭 | $15 | 3s | Lv.1 |
| 番茄炒蛋 | $18 | 4s | Lv.1 |
| 麻婆豆腐 | $22 | 5s | Lv.1 |
| 宫保鸡丁 | $28 | 6s | Lv.1 |
| 糖醋里脊 | $35 | 8s | Lv.2 |
| 红烧肉 | $45 | 10s | Lv.2 |
| 清蒸鱼 | $58 | 12s | Lv.2 |
| 北京烤鸭 | $88 | 15s | Lv.3 |
| 龙虾 | $168 | 20s | Lv.4 |
| 和牛牛排 | $288 | 25s | Lv.5 |

## 项目结构

```
src/main/java/com/restaurant/
├── Main.java              # 主程序入口
├── model/                 # 数据模型
│   ├── Employee.java      # 员工基类
│   ├── Waiter.java        # 服务员
│   ├── Chef.java          # 厨师
│   ├── Customer.java      # 顾客
│   ├── Dish.java          # 菜品
│   ├── Order.java         # 订单
│   └── Table.java         # 餐桌
├── game/                  # 游戏逻辑
│   ├── Hotel.java         # 旅店主类（核心逻辑）
│   └── GameTimer.java     # 游戏计时器
└── ui/                    # 用户界面
    └── ConsoleUI.java     # 控制台界面
```

## 后续开发计划

- [ ] 添加 Swing 图形界面
- [ ] 添加存档/读档功能
- [ ] 添加更多随机事件
- [ ] 添加竞争对手系统
- [ ] 添加装修系统
- [ ] 移植到 libGDX 支持 Android

## 跨平台说明

### Windows
- 可以打包成 JAR 文件直接运行
- 也可以使用 jpackage 打包成 EXE

### Android
- Java Swing 不支持 Android
- 后期可以使用 libGDX 框架重写，实现跨平台
- libGDX 可以同时编译到 PC、Android、iOS、Web

## 素材资源

如果后期要添加图形界面，可以从以下网站获取免费素材：

- **itch.io** - https://itch.io/game-assets/free
- **OpenGameArt** - https://opengameart.org/
- **Kenney** - https://kenney.nl/assets (高质量免费素材)
- **Game-icons.net** - https://game-icons.net/ (图标)

## License

MIT License

