import org.apache.spark.{SparkConf, SparkContext}
object wordgrouping {
  def main(args: Array[String]) {
    //    System.setProperty("hadoop.home.dir", "E:\\UMKC\\Sum_May\\KDM\\winutils")
    import org.apache.spark.SparkConf
    val conf = new SparkConf().setAppName("wordgrouping").setMaster("local[*]")
//    val conf = new SparkConf().setMaster("local").set("spark.driver.host","localhost")
    val sc = new SparkContext(conf)
    val f=sc.textFile("/Volumes/Data/myicp/Input/inputFile")
    val wc=f.flatMap(line=>{line.split(" ")})
    val out=wc.groupBy(word=>word.charAt(0))
    out.foreach(println)

    out.saveAsTextFile("output")

    val o=out.collect()
  }
}
