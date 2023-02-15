# blockchain-trace
该项目是使用Java实现区块链的一个学习项目，只限学习使用。
主要使用了Java1.8，mysql8，Rocks，其他的所需的依赖全在pom文件里面。

- 最新一次提交弃用了数据存mysql里面的做法，全存在Rocks里面，如果想参考数据存数据库的做法可以查看历史的commit记录
- 数据库代码为：
  ~~~ mysql
    create database block;
    use block;
    create table traces(
        hash varchar(256) primary key not null ,
        productName varchar(40) not null ,
        productPlace varchar(40) not null ,
        productTime varchar(40) not null ,
        producer varchar(40) not null ,
        distributor varchar(40) not null ,
        distributeTime varchar(40) not null ,
        retailer varchar(40) not null ,
        retailTime varchar(40) not null,
        recordTimeStamp varchar(40) not null
    );
  ~~~
- 该项目使用了，椭圆算法对数据进行签名加密，在取回数据时再方向进行签名的验证来判断数据的真实性。
- 在区块的内部Merkel树结构进行了优化，在每一个节点上新增了一个布隆过滤器，以在查找时对数据进行快速进行判断是否存在。
  伪代码：
  ~~~ java
    输入：产品序列号
    输出：产品具体信息
    Information find(String serialNumber){
    for(Block block: Blocks){
        if(区块头的布隆过滤器包含serialNumber){
            Node node = root;//root为merkle树根节点。
            if(node不是叶子节点){
            if(node.left节点布隆过滤器包含serialNumber){
                node = node.left;
                continue;
             }
            if(node.right节点布隆过滤器包含serialNumber){
                node = node.right;
             }
            }else{
                return node.information;
            }
        }   
    }
  return null;
  }
  ~~~
  在查找的过程不断剪枝以提高查询效率。
- 在最新一次提交新增了ArrayList结构来保存每一区块，在查询时只需查询ArrayList。而原先的使用RocksDB用前后hash值进行迭代方式历遍区块方式弃用。

详细思路请看pdf介绍