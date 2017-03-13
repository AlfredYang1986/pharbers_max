代码重构整理，并添加数据层抽象的逻辑处理。

现在的数据计算处理逻辑有以下几个概念

1。 数据Storage
    1。1 一个数据Storage分为多个Protion
    1。2 对Protion进行分布计算
    1。3 每个Protion在系统中以文件形式保存，和传递（各个结点中）
    
2。 架构层次
    2。1 参考Spark架构，Reception为最外层
    2。2 Reception管理Master，Master管理着各个结点中的Worker
    2。3 每个结点一个Worker，每个Worker根据结点机器上硬件CPU核数，对Task进行多线程
    
    
3。 Task抽象
    3。1 Task分为两种计算
        Map：生成另一种Storage的。或者教Storage的另一个stage
        Action：通过当下的stage，计算出需要的结果
        
    3。2 当前计算框架
        stage1：从Excel读出数据
        stage2：Max放大
        Action1：计算平均值
        Action2：计算最终结果
        Action3：数据库插入        
    
    
    终于写完了  累死