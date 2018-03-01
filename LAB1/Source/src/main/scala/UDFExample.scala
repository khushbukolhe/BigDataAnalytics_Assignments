import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.SQLContext
import org.apache.spark.sql.Row
import org.apache.spark.sql.types.{StructType,StructField,StringType,DoubleType,IntegerType}
import org.apache.spark.sql.functions.udf

object UDFExample {
  def main(args : Array[String]):Unit =
  {
    val sc= new SparkContext(new SparkConf().setAppName("UDFExample"))
    val sqlContext = new SQLContext(sc)
    //Dataframe contains 2 columns
    val schemaString = "FirstNum,SecondNum,thROW,FOUROW"
    val schema=StructType(schemaString.split(",",-1).map(fieldName =>StructField(fieldName,IntegerType)))

    //Load the textfile and generate a DataFrame with 2 integer columns
    val inputRows = sc.textFile("/Volumes/Data/myicp/Input/u.data").map((p=>Row.fromSeq(p.split(",",-1) ))).map({ case Row(i: java.lang.String,j: java.lang.String) => Row(i.toInt,j.toInt) })
    val inputDataFrame = sqlContext.createDataFrame(inputRows, schema)

    //Anomynous function which takes 2 Integer and returns the sum
    val addColumn : (Int,Int)=>Int=(num1:Int,num2:Int)=>{num1+num2}

    //Declare the UDF
    val addColumnUDF = udf(addColumn)

    //Add the new column "sum" by calling the udf
    val output = inputDataFrame.withColumn("sum",addColumnUDF(inputDataFrame.col("FirstNum"),inputDataFrame.col("SecondNum")))

    output.take(4).foreach { println }
  }
}

