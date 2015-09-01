package io.getquill.source.finagle.mysql

import java.util.Date

import scala.reflect.ClassTag
import scala.reflect.classTag

import com.twitter.finagle.exp.mysql.BigDecimalValue
import com.twitter.finagle.exp.mysql.ByteValue
import com.twitter.finagle.exp.mysql.DoubleValue
import com.twitter.finagle.exp.mysql.FloatValue
import com.twitter.finagle.exp.mysql.IntValue
import com.twitter.finagle.exp.mysql.LongValue
import com.twitter.finagle.exp.mysql.RawValue
import com.twitter.finagle.exp.mysql.Row
import com.twitter.finagle.exp.mysql.ShortValue
import com.twitter.finagle.exp.mysql.StringValue
import com.twitter.finagle.exp.mysql.TimestampValue
import com.twitter.finagle.exp.mysql.Value

import io.getquill.util.Messages.fail

trait FinagleMysqlDecoders {
  this: FinagleMysqlSource =>

  protected val timestampValue =
    new TimestampValue(
      dateTimezone,
      dateTimezone)

  def decoder[T: ClassTag](f: PartialFunction[Value, T]): Decoder[T] =
    new Decoder[T] {
      def apply(index: Int, row: Row) = {
        val value = row.values(index)
        f.lift(value).getOrElse(fail(s"Value '$value' can't be decoded to '${classTag[T].runtimeClass}'"))
      }
    }

  implicit val stringDecoder =
    decoder[String] {
      case StringValue(v) => v
    }
  implicit val bigDecimalDecoder =
    decoder[BigDecimal] {
      case BigDecimalValue(v) => v
    }
  implicit val booleanDecoder =
    decoder[Boolean] {
      case v: RawValue => v.bytes.head == (1: Byte)
    }
  implicit val byteDecoder =
    decoder[Byte] {
      case ByteValue(v)  => v
      case ShortValue(v) => v.toByte
    }
  implicit val shortDecoder =
    decoder[Short] {
      case ShortValue(v) => v
    }
  implicit val intDecoder =
    decoder[Int] {
      case IntValue(v)  => v
      case LongValue(v) => v.toInt
    }
  implicit val longDecoder =
    decoder[Long] {
      case LongValue(v) => v
    }
  implicit val floatDecoder =
    decoder[Float] {
      case FloatValue(v) => v
    }
  implicit val doubleDecoder =
    decoder[Double] {
      case DoubleValue(v) => v
    }
  implicit val byteArrayDecoder =
    decoder[Array[Byte]] {
      case v: RawValue => v.bytes
    }
  implicit val dateDecoder =
    decoder[Date] {
      case `timestampValue`(v) => new Date(v.getTime)
    }
}
