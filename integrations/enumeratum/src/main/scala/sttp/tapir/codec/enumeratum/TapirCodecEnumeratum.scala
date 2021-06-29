package sttp.tapir.codec.enumeratum

import enumeratum._
import enumeratum.values._
import sttp.tapir.Schema.SName
import sttp.tapir._

trait TapirCodecEnumeratum {
  // Regular enums

  def validatorEnumEntry[E <: EnumEntry](implicit enum: Enum[E]): Validator[E] =
    Validator.enumeration(enum.values.toList, v => Some(v.entryName), Some(SName(fullName(`enum`))))

  implicit def schemaForEnumEntry[E <: EnumEntry](implicit enum: Enum[E]): Schema[E] =
    Schema[E](SchemaType.SString()).validate(validatorEnumEntry)

  implicit def plainCodecEnumEntry[E <: EnumEntry](implicit enum: Enum[E]): Codec.PlainCodec[E] =
    Codec.string
      .mapDecode { s =>
        enum
          .withNameOption(s)
          .map(DecodeResult.Value(_))
          .getOrElse(DecodeResult.Mismatch(s"One of: ${enum.values.map(_.entryName).mkString(", ")}", s))
      }(_.entryName)
      .validate(validatorEnumEntry)

  // Value enums

  def validatorValueEnumEntry[T, E <: ValueEnumEntry[T]](implicit enum: ValueEnum[T, E]): Validator[E] =
    Validator.enumeration(enum.values.toList, v => Some(v.value), Some(SName(fullName(`enum`))))

  implicit def schemaForIntEnumEntry[E <: IntEnumEntry](implicit enum: IntEnum[E]): Schema[E] =
    Schema[E](SchemaType.SInteger()).validate(validatorValueEnumEntry[Int, E])

  implicit def schemaForLongEnumEntry[E <: LongEnumEntry](implicit enum: LongEnum[E]): Schema[E] =
    Schema[E](SchemaType.SInteger()).validate(validatorValueEnumEntry[Long, E])

  implicit def schemaForShortEnumEntry[E <: ShortEnumEntry](implicit enum: ShortEnum[E]): Schema[E] =
    Schema[E](SchemaType.SInteger()).validate(validatorValueEnumEntry[Short, E])

  implicit def schemaForStringEnumEntry[E <: StringEnumEntry](implicit enum: StringEnum[E]): Schema[E] =
    Schema[E](SchemaType.SString()).validate(validatorValueEnumEntry[String, E])

  implicit def schemaForByteEnumEntry[E <: ByteEnumEntry](implicit enum: ByteEnum[E]): Schema[E] =
    Schema[E](SchemaType.SInteger()).validate(validatorValueEnumEntry[Byte, E])

  implicit def schemaForCharEnumEntry[E <: CharEnumEntry](implicit enum: CharEnum[E]): Schema[E] =
    Schema[E](SchemaType.SString()).validate(validatorValueEnumEntry[Char, E])

  def plainCodecValueEnumEntry[T, E <: ValueEnumEntry[T]](implicit
      enum: ValueEnum[T, E],
      baseCodec: Codec.PlainCodec[T],
      schema: Schema[E]
  ): Codec.PlainCodec[E] =
    baseCodec
      .mapDecode { v =>
        enum
          .withValueOpt(v)
          .map(DecodeResult.Value(_))
          .getOrElse(DecodeResult.Mismatch(s"One of: ${enum.values.map(_.value).mkString(", ")}", v.toString))
      }(_.value)
      .schema(schema)

  implicit def plainCodecIntEnumEntry[E <: IntEnumEntry](implicit enum: IntEnum[E]): Codec.PlainCodec[E] =
    plainCodecValueEnumEntry[Int, E]

  implicit def plainCodecLongEnumEntry[E <: LongEnumEntry](implicit enum: LongEnum[E]): Codec.PlainCodec[E] =
    plainCodecValueEnumEntry[Long, E]

  implicit def plainCodecShortEnumEntry[E <: ShortEnumEntry](implicit enum: ShortEnum[E]): Codec.PlainCodec[E] =
    plainCodecValueEnumEntry[Short, E]

  implicit def plainCodecStringEnumEntry[E <: StringEnumEntry](implicit enum: StringEnum[E]): Codec.PlainCodec[E] =
    plainCodecValueEnumEntry[String, E]

  implicit def plainCodecByteEnumEntry[E <: ByteEnumEntry](implicit enum: ByteEnum[E]): Codec.PlainCodec[E] =
    plainCodecValueEnumEntry[Byte, E]

  private def fullName[T](t: T) = t.getClass.getName.replace("$", ".")

  // no Codec.PlainCodec[Char]
}
