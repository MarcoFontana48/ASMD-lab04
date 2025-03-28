# Operational Task

## Verifica delle proprietà del metodo `filter`
La proprietà `filterAxioms` verifica tre casi fondamentali per il metodo `filter`:
- caso di sequenza vuota
- caso in cui la 'head' della sequenza soddisfa il predicato e deve essere inclusa nel risultato, seguita da una chiamata ricorsiva su 'tail'
- caso in cui la 'head' della sequenza non soddisfa il predicato e non deve essere inclusa nel risultato, seguita da una chiamata ricorsiva su 'tail'

## Verifica delle proprietà del metodo `sum`
La proprietà `sumAxioms` verifica due casi fondamentali per il metodo `sum`:
- caso di sequenza non vuota, la somma deve essere uguale a 'head' più chiamata ricorsiva della 'tail'
- caso di sequenza vuota, la somma deve essere zero

## Verifica delle proprietà del metodo `concat`
La proprietà `concatAxioms` verifica due casi fondamentali per il metodo `concat`:
- caso di una sequenza vuota e l'altra no: la concatenazione deve essere uguale alla sequenza non vuota
- caso di sequenze non vuote: la concatenazione deve essere uguale alla 'head' della prima sequenza concatenata con la chiamata ricorsiva della 'tail' della prima sequenza e la seconda sequenza

## Verifica delle proprietà del metodo `flatMap`
La proprietà `flatMapAxioms` verifica due casi fondamentali per il metodo `flatMap`:
- caso di una sequenza vuota: l'applicazione di flatMap a una sequenza vuota restituisce sempre una sequenza vuota
- caso di una sequenza non vuota: l'applicazione di flatMap a una sequenza non vuota applica la funzione alla 'head' e concatena il risultato con il flatMap della 'tail'.

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
