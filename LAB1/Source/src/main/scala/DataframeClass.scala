

import org.apache.spark.{SparkConf, SparkContext}
object DataframeClasss {

  def main(args: Array[String]) {

    import org.apache.spark.SparkConf
    val conf = new SparkConf().setAppName("DataframeClasss").setMaster("local[*]")
    val sc = new SparkContext(conf)

    val lines = sc.textFile("/Volumes/Data/myicp/Input/u.data")
    val lineLengths = lines.map(line => line.split("\t")).map(k=>(k(0),1)).reduceByKey(_+_)
    val filterkey= lineLengths.filter(_._2 > 25 )



    filterkey.foreach(println)

    lineLengths.saveAsTextFile("Output")
    val o=lineLengths.collect()

  }

}
