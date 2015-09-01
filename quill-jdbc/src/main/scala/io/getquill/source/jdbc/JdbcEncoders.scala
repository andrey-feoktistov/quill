package io.getquill.source.jdbc

import java.sql
import java.sql.PreparedStatement
import java.util
import java.util.Calendar
import java.util.TimeZone

trait JdbcEncoders {
  this: JdbcSource =>

  protected val dateTimeZone = TimeZone.getDefault

  private def encoder[T](f: PreparedStatement => (Int, T) => Unit): Encoder[T] =
    new Encoder[T] {
      override def apply(index: Int, value: T, row: PreparedStatement) = {
        f(row)(index + 1, value)
        row
      }
    }

  implicit val stringEncoder = encoder(_.setString)
  implicit val bigDecimalEncoder: Encoder[BigDecimal] =
    new Encoder[BigDecimal] {
      override def apply(index: Int, value: BigDecimal, row: PreparedStatement) = {
        row.setBigDecimal(index + 1, value.bigDecimal)
        row
      }
    }
  implicit val booleanEncoder = encoder(_.setBoolean)
  implicit val byteEncoder = encoder(_.setByte)
  implicit val shortEncoder = encoder(_.setShort)
  implicit val intEncoder = encoder(_.setInt)
  implicit val longEncoder = encoder(_.setLong)
  implicit val floatEncoder = encoder(_.setFloat)
  implicit val doubleEncoder = encoder(_.setDouble)
  implicit val byteArrayEncoder = encoder(_.setBytes)
  implicit val dateEncoder: Encoder[util.Date] =
    new Encoder[util.Date] {
      override def apply(index: Int, value: util.Date, row: PreparedStatement) = {
        row.setTimestamp(index + 1, new sql.Timestamp(value.getTime), Calendar.getInstance(dateTimeZone))
        row
      }
    }
}
