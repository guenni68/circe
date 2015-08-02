package io.jfc

import io.jfc.test.JfcSuite
import org.scalacheck.{ Arbitrary, Gen }

class JsonNumberTests extends JfcSuite {
  case class JsonNumberString(s: String)

  object JsonNumberString {
    implicit val arbitraryJsonNumberString: Arbitrary[JsonNumberString] =
      Arbitrary(
        for {
          sign <- Gen.oneOf("", "-")
          number <- Gen.oneOf(
            Gen.const("0"),
            for {
              nonZero <- Gen.choose(1, 9).map(_.toString)
              rest <- Gen.numStr
            } yield s"$nonZero$rest"
          )
          frac <- Gen.oneOf(
            Gen.const(""),
            Gen.nonEmptyListOf(Gen.numChar).map(_.mkString).map("." + _)
          )
          exp <- Gen.oneOf(
            Gen.const(""),
            for {
              e <- Gen.oneOf("e", "E")
              s <- Gen.oneOf("", "+", "-")
              n <- Gen.nonEmptyListOf(Gen.numChar).map(_.mkString)
            } yield s"$e$s$n"
          )
        } yield JsonNumberString(s"$sign$number$frac$exp")
      )
  }

  test("fromString") {
    check { (jsn: JsonNumberString) =>
      JsonNumber.fromString(jsn.s).nonEmpty
    }
  }
}