package eu.ankudinov.urlshortener.domain

import java.time.LocalDateTime

import io.lemonlabs.uri.AbsoluteUrl

final case class UrlEntity (id: Id, url: AbsoluteUrl, createdAt: LocalDateTime)
