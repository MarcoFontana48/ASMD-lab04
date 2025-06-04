# Operational Task

## Verifica delle proprietà del metodo `filter`
La proprietà `filterAxioms` verifica il caso di lista vuota che deve restituire lista vuota e il caso in cui il predicato corrisponda al valore passato e il tal caso mantenerlo.

## Verifica delle proprietà del metodo `sum`
La proprietà `sumAxioms` verifica il caso di sequenza non vuota, in cui la somma deve essere uguale a 'head' più chiamata ricorsiva della 'tail', ed il caso di sequenza vuota, dove la somma deve essere zero.

## Verifica delle proprietà del metodo `concat`
La proprietà `concatAxioms` verifica il caso di una sequenza vuota e l'altra no: la concatenazione deve essere uguale alla sequenza non vuota; ed il caso di sequenze non vuote: la concatenazione deve essere uguale alla 'head' della prima sequenza concatenata con la chiamata ricorsiva della 'tail' della prima sequenza e la seconda sequenza

## Verifica delle proprietà del metodo `flatMap`
La proprietà `flatMapAxioms` verifica il caso di una sequenza vuota: l'applicazione di flatMap a una sequenza vuota restituisce sempre una sequenza vuota; ed il caso di una sequenza non vuota: l'applicazione di flatMap a una sequenza non vuota applica la funzione alla 'head' e concatena il risultato con il flatMap della 'tail'.

```scala 3
  // check axioms, universally
  property("mapAxioms") =
    forAll: (seq: Sequence[Int], f: Int => Int) =>
      //println(seq); println(f(10)) // inspect what's using
      (seq, f) match
        case (Nil(), f) =>  map(Nil())(f) == Nil()
        case (Cons(h, t), f) => map(Cons(h, t))(f) == Cons(f(h), map(t)(f))

  property("filterAxioms") =
    forAll: (seq: Sequence[Int], p: Int => Boolean) =>
      (seq, p) match
        case (Nil(), p) => filter(Nil())(p) == Nil()
        case (Cons(h, t), p) if p(h) => filter(Cons(h, t))(p) == Cons(h, filter(t)(p))
        case (Cons(h, t), p) => filter(Cons(h, t))(p) == filter(t)(p)

  property("sumAxioms") =
    forAll: (seq: Sequence[Int]) =>
      seq match
        case Cons(head, tail) => sum(Cons(head, tail)) == head + sum(tail)
        case Nil() => sum(Nil()) == 0

  property("concatAxioms") =
    forAll: (seq: Sequence[Int], seq2: Sequence[Int]) =>
      (seq, seq2) match
        case (Nil(), seq2) => concat(seq)(seq2) == seq2
        case (Cons(h, t), seq2) => concat(seq)(seq2) == Cons(h, concat(t)(seq2))

  property("flatMapAxioms") =
    forAll: (seq: Sequence[Int], f: Int => Sequence[Int]) =>
      (seq, f) match
        case (Nil(), f) => flatMap(seq)(f) == Nil()
        case (Cons(h, t), f) => flatMap(seq)(f) == concat(f(h))(t.flatMap(f))
```

## ScalaCheck parameters
- il numero di test che viene generato di default è 100
- è possibile modificare il numero di test generati ad un numero specificato:
```scala
  // edit the number of tests from the default number of 100 to a specified amount
  override def overrideParameters(p: Test.Parameters): Test.Parameters =
    p.withMinSuccessfulTests(200)
```
- è possibile impostare un seme passandolo come argomento della funzione 'withInitialSeed'
```scala
  override def overrideParameters(p: Test.Parameters): Test.Parameters =
  p.withMinSuccessfulTests(200)
   .withInitialSeed(1234)
```

## ScalaCheck and ScalaTest
ScalaTest non può eseguire test parametrizzati come invece si può fare con ScalaCheck, le due librerie sono pensate per
effettuare, l'una per fare test basato sulle proprietà, quindi definire proprietà che devono sempre essere soddisfatte e
vengono generati diversi input casuali che controllano queste proprietà (ScalaCheck), l'altra per effettuare test
generali con input specifici ed effettuare test come unit test e test di integrazione.

# R&D Task 02 (Java-Scala-Check)
Ho creato una classe Point2D in Java da testare usando ScalaCheck:

```java
public record Point2D(double x, double y) {
    public double distanceTo(Point2D other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }
    
    public Point2D rotate(final double angle) {
        double radians = Math.toRadians(angle);
        double newX = this.x * Math.cos(radians) - this.y * Math.sin(radians);
        double newY = this.x * Math.sin(radians) + this.y * Math.cos(radians);
        return new Point2D(newX, newY);
    }
    
    public Point2D translate(final double dx, final double dy) {
        return new Point2D(this.x + dx, this.y() + dy);
    }
}
```

per testare le proprietà della classe ho creato una suite di test che verifica le proprietà matematiche della classe,
come la simmetria della distanza, l'ineguaglianza triangolare e l'invarianza alla rotazione.

```scala
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
```

Un'altra classe effettua test sulle collezioni Java ArrayList e TreeSet:

```java
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
```

## Vantaggi e svantaggi sull'uso di ScalaCheck per altri linguaggi
Non ho riscontrato svantaggi, ma solo vantaggi, siccome entrambi i linguaggi fanno parte della JVM, è possibile
utilizzare le librerie Java in Scala e viceversa, quindi è possibile testare facilmente le classi Java in Scala.
