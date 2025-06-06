package io.exoquery.example

import kotlin.reflect.KClass

interface Elem {
  data class Static(val name: String) : Elem
  object Variable : Elem
}

interface PathBase {
  val elems: List<Elem>
}
data class Path0(override val elems: List<Elem>): PathBase
data class Path1<T1>(override val elems: List<Elem>): PathBase
data class Path2<T1, T2>(override val elems: List<Elem>): PathBase
data class Path3<T1, T2, T3>(override val elems: List<Elem>): PathBase
data class Path4<T1, T2, T3, T4>(override val elems: List<Elem>): PathBase

fun <T1, R> Path1<T1>.then(then: (T1) -> R): R = TODO()
fun <T1, T2, R> Path2<T1, T2>.then(then: (T1, T2) -> R): R = TODO()


class Var<T>


interface PathBuilder {
  val root get(): Path0 = Path0(emptyList())
  operator fun Path0.div(other: String): Path0 = Path0(elems + Elem.Static(other))

  operator fun <T1> Path0.div(other: Var<T1>): Path1<T1> = Path1(elems + Elem.Variable)
  operator fun <T1> Path1<T1>.div(other: String): Path1<T1> = Path1(elems + Elem.Static(other))
  operator fun <T1, T2> Path1<T1>.div(other: Var<T2>): Path2<T1, T2> = Path2(elems + Elem.Variable)
}

fun <P: PathBase> path(builder: PathBuilder.() -> P): P = TODO()

object V {
  val String: Var<kotlin.String> = Var()
  val Int: Var<kotlin.Int> = Var()
}


fun myStuff() {
  path { root/"foo"/V.String/"bar"/V.Int }.then { name: String, id: Int ->
    println("id: $id, name: $name")
  }
}
