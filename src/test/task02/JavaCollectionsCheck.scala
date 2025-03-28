package task02

import org.scalacheck.{Arbitrary, Gen, Prop, Properties, Test}
import org.scalacheck.Prop.forAll

import java.util
import java.util.{ArrayList, Comparator, TreeSet}
import scala.jdk.CollectionConverters.*

object JavaCollectionsCheck extends Properties("JavaCollections"):
  // Override test parameters
  override def overrideParameters(p: Test.Parameters): Test.Parameters =
    p.withMinSuccessfulTests(200)
      .withInitialSeed(1234)

  // Generator for Integer
  given integerArbitrary: Arbitrary[Integer] = Arbitrary(
    Gen.choose(-1000, 1000).map(Integer.valueOf)
  )

  // Generator for Integer ArrayLists
  def intArrayListGen: Gen[util.ArrayList[Integer]] = for {
    size <- Gen.choose(0, 100)
    elements <- Gen.listOfN(size, Gen.choose(-1000, 1000))
  } yield new util.ArrayList[Integer](elements.map(Integer.valueOf).asJava)

  // Generator for Integer TreeSets
  def intTreeSetGen: Gen[util.TreeSet[Integer]] = for {
    size <- Gen.choose(0, 100)
    elements <- Gen.listOfN(size, Gen.choose(-1000, 1000))
  } yield new util.TreeSet[Integer](elements.map(Integer.valueOf).asJava)

  // Implicit arbitraries
  given intArrayListArbitrary: Arbitrary[util.ArrayList[Integer]] = Arbitrary(intArrayListGen)
  given intTreeSetArbitrary: Arbitrary[util.TreeSet[Integer]] = Arbitrary(intTreeSetGen)

  // ArrayList Properties
  property("ArrayList add and get") = forAll { (list: util.ArrayList[Integer], elem: Integer) =>
    val originalSize = list.size()
    val newList = new util.ArrayList[Integer](list)
    newList.add(elem)
    newList.get(originalSize) == elem && newList.size() == originalSize + 1
  }

  property("ArrayList contains after add") = forAll { (list: util.ArrayList[Integer], elem: Integer) =>
    val newList = new util.ArrayList[Integer](list)
    newList.add(elem)
    newList.contains(elem)
  }

  property("TreeSet does not allow duplicates") = forAll { (elem: Integer) =>
    val set = new util.TreeSet[Integer]()
    set.add(elem)
    set.add(elem)
    set.size() == 1
  }