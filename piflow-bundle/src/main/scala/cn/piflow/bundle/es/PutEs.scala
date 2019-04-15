package cn.piflow.bundle.es

import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import cn.piflow.conf.{ConfigurableStop, PortEnum, StopGroup}
import cn.piflow.{JobContext, JobInputStream, JobOutputStream, ProcessContext}
import org.apache.spark.sql.SparkSession
import org.elasticsearch.spark.sql.EsSparkSQL

class PutEs extends ConfigurableStop {

  override val description: String = "Put data into Elasticsearch"
  val authorEmail: String = "ygang@cnic.cn"

  override val inportList: List[String] = List(PortEnum.DefaultPort.toString)
  override val outportList: List[String] = List(PortEnum.NonePort.toString)

  var es_nodes : String =  _
  var es_port  : String  =  _
  var es_index : String =  _
  var es_type  : String  =  _
  var configuration_item:String = _


  def perform(in: JobInputStream, out: JobOutputStream, pec: JobContext): Unit = {
    val spark = pec.get[SparkSession]()
    val inDf = in.read()

    val sc = spark.sparkContext
    var options = Map("es.index.auto.create"-> "true",
      "es.nodes"->es_nodes,
      "es.port"->es_port)

    if(configuration_item.length > 0){
      configuration_item.split(",").foreach(each =>{
        options += (each.split("->")(0) -> each.split("->")(1))
      })
    }

    EsSparkSQL.saveToEs(inDf,s"${es_index}/${es_type}",options)

  }


  def initialize(ctx: ProcessContext): Unit = {

  }

  def setProperties(map : Map[String, Any]): Unit = {
    es_nodes=MapUtil.get(map,key="es_nodes").asInstanceOf[String]
    es_port=MapUtil.get(map,key="es_port").asInstanceOf[String]
    es_index=MapUtil.get(map,key="es_index").asInstanceOf[String]
    es_type=MapUtil.get(map,key="es_type").asInstanceOf[String]
    configuration_item=MapUtil.get(map,key="configuration_item").asInstanceOf[String]
  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor : List[PropertyDescriptor] = List()
    val es_nodes = new PropertyDescriptor().name("es_nodes").displayName("es_nodes")
      .description("Node of Elasticsearch").defaultValue("").required(true)
    val es_port = new PropertyDescriptor().defaultValue("9200").name("es_port").displayName("es_port")
      .description("Port of Elasticsearch").required(true)
    val es_index = new PropertyDescriptor().name("es_index").displayName("es_index")
      .description("Index of Elasticsearch").defaultValue("").required(true)
    val es_type = new PropertyDescriptor().name("es_type").displayName("es_type")
      .description("Type of Elasticsearch").defaultValue("").required(true)
    val configuration_item = new PropertyDescriptor().name("configuration_item").displayName("configuration_item")
      .defaultValue("Configuration Item of Es.such as:es.mapping.parent->id_1,es.mapping.parent->id_2").required(false)


    descriptor = es_nodes :: descriptor
    descriptor = es_port :: descriptor
    descriptor = es_index :: descriptor
    descriptor = es_type :: descriptor
    descriptor = configuration_item :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/elasticsearch/PutEs.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.ESGroup.toString)
  }

}
