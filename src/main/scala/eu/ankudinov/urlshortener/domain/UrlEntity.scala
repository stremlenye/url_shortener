package eu.ankudinov.urlshortener.domain

import java.time.LocalDateTime

import io.lemonlabs.uri.AbsoluteUrl

/** Persistence model describing the mapping between the generated key and original url */
final case class UrlEntity (id: Id, url: AbsoluteUrl, createdAt: LocalDateTime)
