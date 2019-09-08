package eu.ankudinov.urlshortener.common

/** Type class witnessing that an error of type `A` can be lifted as an error of type `B`. */
trait LiftError[A, B] {
  def apply(a: A): B
}
