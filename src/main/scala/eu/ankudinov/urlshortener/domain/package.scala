package eu.ankudinov.urlshortener

package object domain {
  type Id = types.Id
  @inline def Id(a: String): Id = types.Id(a)

  type ShortUrl = types.ShortUrl
}
