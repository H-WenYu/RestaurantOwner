# 元梦之星：餐厅版功能梳理（仅餐厅，不含农场）

基于现有代码，我们把“农场”侧完全摘除，专注餐厅循环。下面是仍需补齐的要点，并注明哪些能力已经具备。

## 菜单与厨房流程
- **已接入配置与解锁**：`menu.csv` 驱动菜谱，`Hotel` 初始化时按餐厅等级自动解锁，并记录日志。【F:src/main/java/com/restaurant/config/MenuConfigLoader.java†L13-L48】【F:src/main/java/com/restaurant/game/Hotel.java†L82-L120】
- **已补充品质/多份与返工**：厨师烹饪会按体力、效率计算时间，生成带品质的 `PreparedDish`，支持一次多份、烧焦返工和顾客离店时的取消。【F:src/main/java/com/restaurant/model/Chef.java†L62-L112】【F:src/main/java/com/restaurant/game/Hotel.java†L541-L606】
- **仍需设备层与排产**：尚未建模设备并发/半成品/加速道具，也没有厨房容量或专用烹饪站点；高峰期排产和自动预制仍待实现。

## 餐厅布局与装修
- **已有摆放与环境评分**：桌子支持坐标移动并做最小间距校验；装饰物可购买/移除并影响环境与卫生评分，评分会参与顾客评价。【F:src/main/java/com/restaurant/game/Hotel.java†L430-L464】【F:src/main/java/com/restaurant/game/Hotel.java†L703-L759】
- **仍缺编辑界面**：Swing 端尚未提供拖拽/对齐网格、区域锁或可视化布局模式，装修也只能通过接口调用而非交互式编辑。

## 厨师出菜与服务员上菜
- **已成型传菜口链路**：新增传菜口队列 `passCounter`，厨师完成菜品后推送，服务员领取后按桌号送达，支持多份批量、品质衰减与摔盘子处理。【F:src/main/java/com/restaurant/game/Hotel.java†L473-L556】
- **仍需动线/容量优化**：服务员暂无托盘容量/优先级调度或路径避障，成品区也未区分窗口/设备，无法针对高峰批量调度或分拣。

## 顾客、评分与经济循环
- **已补充催单与环境权重**：顾客会催单、评分会叠加环境/卫生因子，桌面清理降低卫生，差评/好评影响声望与事件触发。【F:src/main/java/com/restaurant/game/Hotel.java†L215-L286】【F:src/main/java/com/restaurant/game/Hotel.java†L632-L691】
- **仍缺扩展玩法**：等位队列、预约/VIP事件的差异化奖励、任务/成就驱动的经济循环以及评分衰减/维护机制尚未实现。

## 交互与存档现状
- **已有 Swing 界面入口**：`SwingMain` 启动了包含开始/继续按钮的窗口，结合 `GameWindow` 呈现餐厅状态。【F:src/main/java/com/restaurant/SwingMain.java†L13-L147】
- **已有存档/读档**：`SaveManager` 在用户目录创建存档，保存酒店、员工、菜单等数据并可续玩。【F:src/main/java/com/restaurant/game/SaveManager.java†L12-L198】
- **界面仍需支持布局/装修编辑**：虽有 Swing UI，但尚未提供场景拖拽、桌椅摆放或装饰交互，需要在现有窗口中扩展编辑模式。

以上聚焦“餐厅版”仍需完善的核心环节，可按布局编辑 → 厨房/服务流程 → 顾客与评分循环的顺序迭代。
