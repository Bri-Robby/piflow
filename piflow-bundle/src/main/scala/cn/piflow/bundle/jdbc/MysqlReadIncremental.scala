package cn.piflow.bundle.jdbc

import cn.piflow.{JobContext, JobInputStream, JobOutputStream, ProcessContext}
import cn.piflow.conf.{ConfigurableIncrementalStop, PortEnum, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.spark.sql.SparkSession

/**
  * Created by xjzhu@cnic.cn on 7/15/19
  */
class MysqlReadIncremental extends ConfigurableIncrementalStop{
  override var incrementalField: String = _
  override var incrementalStart: String = _
  override val authorEmail: String = "xjzhu@cnic.cn"
  override val description: String = "Read data from jdbc database"
  override val inportList: List[String] = List(PortEnum.NonePort.toString)
  override val outportList: List[String] = List(PortEnum.DefaultPort.toString)

  //var driver:String = _
  var url:String = _
  var user:String = _
  var password:String = _
  var sql:String = _

  override def setProperties(map: Map[String, Any]): Unit = {
    //driver = MapUtil.get(map,"driver").asInstanceOf[String]
    url = MapUtil.get(map,"url").asInstanceOf[String]
    user = MapUtil.get(map,"user").asInstanceOf[String]
    password = MapUtil.get(map,"password").asInstanceOf[String]
    sql = MapUtil.get(map,"sql").asInstanceOf[String]
    incrementalField = MapUtil.get(map,"incrementalField").asInstanceOf[String]
    incrementalStart = MapUtil.get(map,"incrementalStart").asInstanceOf[String]

  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor : List[PropertyDescriptor] = List()
    //val driver=new PropertyDescriptor().name("driver").displayName("Driver").description("The driver name, for example com.mysql.jdbc.Driver").defaultValue("").required(true)
    //descriptor = driver :: descriptor

    val url=new PropertyDescriptor().name("url").displayName("url").description("The Url, for example jdbc:mysql://127.0.0.1/dbname").defaultValue("").required(true)
    descriptor = url :: descriptor

    val user=new PropertyDescriptor().name("user").displayName("user").description("The user name of database").defaultValue("").required(true)
    descriptor = user :: descriptor

    val password=new PropertyDescriptor().name("password").displayName("password").description("The password of database").defaultValue("").required(true)
    descriptor = password :: descriptor

    val sql=new PropertyDescriptor().name("sql").displayName("sql").description("The sql sentence you want to execute").defaultValue("").required(true)
    descriptor = sql :: descriptor

    val incrementalField=new PropertyDescriptor().name("incrementalField").displayName("incrementalField").description("The incremental field").defaultValue("").required(true)
    descriptor = incrementalField :: descriptor

    val incrementalStart=new PropertyDescriptor().name("incrementalStart").displayName("incrementalStart").description("The incremental start value").defaultValue("").required(true)
    descriptor = incrementalStart :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/jdbc/jdbcRead.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.JdbcGroup.toString)
  }

  override def initialize(ctx: ProcessContext): Unit = {}

  override def perform(in: JobInputStream, out: JobOutputStream, pec: JobContext): Unit = {
    val spark = pec.get[SparkSession]()
    val dbtable = "( "  + sql + ") AS Temp"
    val jdbcDF = spark.read.format("jdbc")
      .option("url", url)
      //.option("driver", driver)
      .option("dbtable", dbtable)
      .option("user", user)
      .option("password",password)
      .load()
    //jdbcDF.show(10)

    out.write(jdbcDF)
  }
}
