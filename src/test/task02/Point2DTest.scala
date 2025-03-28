package task02

import org.scalacheck.Prop.forAll
import org.scalacheck.{Arbitrary, Gen, Properties, Test}

import scala.math.abs

object Point2DCheck extends Properties("Point2D"):
  // Override default test parameters
  override def overrideParameters(p: Test.Parameters): Test.Parameters =
    p.withMinSuccessfulTests(200)
      .withInitialSeed(1234)

  // Generator for Point2D
  def point2DGen: Gen[Point2D] = for {
    x <- Gen.choose(-1000.0, 1000.0)
    y <- Gen.choose(-1000.0, 1000.0)
  } yield Point2D(x, y)
  
  // Implicit arbitrary for Point2D
  given point2DArbitrary: Arbitrary[Point2D] = Arbitrary(point2DGen)

  // Property: Distance symmetry
  property("distance symmetry") = forAll { (a: Point2D, b: Point2D) =>
    abs(a.distanceTo(b) - b.distanceTo(a)) < 1e-10
  }

  // Property: Triangle inequality
  property("triangle inequality") = forAll { (a: Point2D, b: Point2D, c: Point2D) =>
    val ab = a.distanceTo(b)
    val bc = b.distanceTo(c)
    val ac = a.distanceTo(c)

    // Check that the distance between two points is less than or equal to
    // the sum of distances between other points
    ac <= ab + bc && ab <= ac + bc && bc <= ab + ac
  }

  // Property: Rotation invariance
  property("rotation identity") = forAll { (p: Point2D) =>
    val rotated = p.rotate(0)
    abs(rotated.x - p.x) < 1e-10 && abs(rotated.y - p.y) < 1e-10
  }
